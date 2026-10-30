package com.sericulture.registration.sevasindhu;

import com.sericulture.registration.model.api.farmer.FarmerRequest;
import com.sericulture.registration.model.api.farmer.FarmerResponse;
import com.sericulture.registration.model.api.farmer.FarmerSaveRequest;
import com.sericulture.registration.model.api.farmer.GetFarmerRequest;
import com.sericulture.registration.model.api.farmer.GetFarmerResponse;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressRequest;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountRequest;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsRequest;
import com.sericulture.registration.model.dto.farmer.FarmerAddressDTO;
import com.sericulture.registration.model.dto.farmer.FarmerLandDetailsDTO;
import com.sericulture.registration.model.entity.Farmer;
import com.sericulture.registration.repository.FarmerRepository;
import com.sericulture.registration.service.FarmerService;
import com.sericulture.registration.sevasindhu.SevaSindhuFarmerDtos.CheckResponse;
import com.sericulture.registration.sevasindhu.SevaSindhuFarmerDtos.LandItem;
import com.sericulture.registration.sevasindhu.SevaSindhuFarmerDtos.Option;
import com.sericulture.registration.sevasindhu.SevaSindhuFarmerDtos.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Seva Sindhu citizen: find the farmer by FRUITS ID, or self-register a new farmer.
 * The citizen session is the one dbt issued after URL 1 (shared table sc_sevasindhu_request).
 * Reuses the existing FRUITS lookup and complete-farmer save in FarmerService unchanged.
 *
 * Identity rule (no OTP exists in the approved workflow, so none is invented):
 *   an existing farmer is linked to the request only when Seva Sindhu's mobile matches the registered
 *   mobile, or the request is in assisted mode (an accountable K1/B1/G1 operator).
 * Registration data from FRUITS (name, father, gender, caste, land) is fetched server-side, never taken
 * from the browser.
 */
@Service
@Slf4j
public class SevaSindhuFarmerService {

    public static final String SESSION_HEADER = "X-SevaSindhu-Session";
    static final long KARNATAKA_STATE_ID = 2L;

    private static final Pattern FRUITS_ID = Pattern.compile("^FID\\d{13}$");
    private static final Pattern MOBILE = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern IFSC = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");
    private static final Pattern ACCOUNT = Pattern.compile("^\\d{9,18}$");
    private static final Pattern PASSBOOK = Pattern.compile("^[A-Za-z0-9/ -]{1,30}$");
    private static final Pattern PINCODE = Pattern.compile("^5[6-9]\\d{4}$");
    private static final Pattern TEXT = Pattern.compile("^[\\p{L}\\p{M}0-9 .,()&'/-]{2,100}$");

    static final String BENEFICIARY_FARMER = "Farmer";
    static final String BENEFICIARY_REELER = "Reeler";

    private final SevaSindhuCitizenRequestRepository requestRepository;
    private final FarmerRepository farmerRepository;
    private final com.sericulture.registration.repository.ReelerRepository reelerRepository;
    private final FarmerService farmerService;
    private final long sessionIdleMinutes;

    @org.springframework.beans.factory.annotation.Autowired
    com.sericulture.registration.service.FarmerChangeHistoryService farmerChangeHistoryService;

    public SevaSindhuFarmerService(SevaSindhuCitizenRequestRepository requestRepository,
                                   FarmerRepository farmerRepository,
                                   com.sericulture.registration.repository.ReelerRepository reelerRepository,
                                   FarmerService farmerService,
                                   @Value("${SEVASINDHU_SESSION_IDLE_MINUTES:60}") long sessionIdleMinutes) {
        this.requestRepository = requestRepository;
        this.farmerRepository = farmerRepository;
        this.reelerRepository = reelerRepository;
        this.farmerService = farmerService;
        this.sessionIdleMinutes = sessionIdleMinutes;
    }

    /** Safe-to-show problem; status is the HTTP status the controller returns. */
    public static class CitizenException extends RuntimeException {
        private final int status;
        private final String code;

        public CitizenException(int status, String code, String message) {
            super(message);
            this.status = status;
            this.code = code;
        }

        public int getStatus() {
            return status;
        }

        public String getCode() {
            return code;
        }
    }

    // ------------------------------------------------------------------ session

    public SevaSindhuCitizenRequest requireSession(String sessionToken) {
        if (sessionToken == null || !sessionToken.matches("^[A-Za-z0-9_-]{43}$")) {
            throw new CitizenException(401, "SESSION_INVALID", "Your session is not valid. Please start again from Seva Sindhu.");
        }
        SevaSindhuCitizenRequest request = requestRepository.findBySessionHashAndActive(sha256Hex(sessionToken), true)
                .orElseThrow(() -> new CitizenException(401, "SESSION_INVALID", "Your session is not valid. Please start again from Seva Sindhu."));
        Date now = new Date();
        if (request.getSessionExpiresAt() == null || request.getSessionExpiresAt().before(now)) {
            throw new CitizenException(401, "SESSION_INVALID", "Your session has expired. Please start again from Seva Sindhu.");
        }
        request.setSessionExpiresAt(new Date(now.getTime() + sessionIdleMinutes * 60_000L));
        return requestRepository.save(request);
    }

    // ------------------------------------------------------------------ check

    public CheckResponse check(SevaSindhuCitizenRequest request, String rawFruitsId) {
        String fruitsId = normaliseFruitsId(rawFruitsId);

        if (request.getFarmerId() != null) {
            if (!fruitsId.equals(request.getFruitsId())) {
                throw new CitizenException(409, "ALREADY_LINKED",
                        "This Seva Sindhu request is already linked to FRUITS ID " + request.getFruitsId() + ".");
            }
            if (BENEFICIARY_REELER.equals(request.getBeneficiaryType())) {
                return registeredReeler(reelerRepository.findByReelerIdAndActive(request.getFarmerId(), true), request);
            }
            Farmer linked = farmerRepository.findByFarmerIdAndActive(request.getFarmerId(), true);
            return registered(linked, request);
        }
        // The FRUITS ID Seva Sindhu sent (URL 1 key1) is authoritative: the citizen cannot apply for another one.
        if (request.getFruitsId() != null && !fruitsId.equals(request.getFruitsId())) {
            throw new CitizenException(409, "FRUITS_ID_MISMATCH",
                    "This application was started in Seva Sindhu for FRUITS ID " + request.getFruitsId()
                            + ". Please continue with that FRUITS ID, or start a new application in Seva Sindhu.");
        }
        if (SevaSindhuCitizenRequest.STATUS_SUBMITTED.equals(request.getRequestStatus())) {
            throw new CitizenException(409, "ALREADY_SUBMITTED", "An application has already been submitted for this Seva Sindhu request.");
        }

        Farmer farmer = farmerRepository.findByFruitsIdAndActive(fruitsId, true);
        if (farmer != null) {
            if (!identityConfirmed(request, farmer)) {
                log.info("Seva Sindhu request {}: FRUITS id registered but mobile does not match", request.getId());
                return CheckResponse.builder()
                        .status("MOBILE_MISMATCH")
                        .fruitsId(fruitsId)
                        .registeredMobile(mask(farmer.getMobileNumber()))
                        .message("This FRUITS ID is registered in e-Reshme with a different mobile number. "
                                + "Please apply with the mobile number registered with the Department of Sericulture, "
                                + "or visit your nearest Sericulture office or Seva Sindhu centre.")
                        .build();
            }
            link(request, farmer);
            return registered(farmer, request);
        }

        // Not a farmer: an existing reeler with this FRUITS ID can apply. Registration through Seva Sindhu is for farmers only.
        com.sericulture.registration.model.entity.Reeler reeler = reelerRepository.findByFruitsIdAndActive(fruitsId, true);
        if (reeler != null) {
            String reelerMobile = reeler.getMobileNumber() == null ? null : reeler.getMobileNumber().trim();
            boolean confirmed = Boolean.TRUE.equals(request.getAssistedMode())
                    || (request.getMobileNumber() != null && request.getMobileNumber().equals(reelerMobile));
            if (!confirmed) {
                log.info("Seva Sindhu request {}: FRUITS id registered as reeler but mobile does not match", request.getId());
                return CheckResponse.builder()
                        .status("MOBILE_MISMATCH")
                        .beneficiaryType(BENEFICIARY_REELER)
                        .fruitsId(fruitsId)
                        .registeredMobile(mask(reelerMobile))
                        .message("This FRUITS ID is registered in e-Reshme as a reeler with a different mobile number. "
                                + "Please apply with the mobile number registered with the Department of Sericulture, "
                                + "or visit your nearest Sericulture office or Seva Sindhu centre.")
                        .build();
            }
            request.setFarmerId(reeler.getReelerId());
            request.setFruitsId(reeler.getFruitsId());
            request.setBeneficiaryType(BENEFICIARY_REELER);
            request.setRequestStatus(SevaSindhuCitizenRequest.STATUS_FARMER_REGISTERED);
            request.setModifiedDate(new Date());
            requestRepository.save(request);
            log.info("Seva Sindhu request {} linked to reeler {}", request.getId(), reeler.getReelerId());
            return registeredReeler(reeler, request);
        }

        GetFarmerResponse fruits = fetchFromFruits(fruitsId);
        // Remember which FRUITS ID registration is for; the registration call must use the same one.
        request.setFruitsId(fruitsId);
        request.setModifiedDate(new Date());
        requestRepository.save(request);
        return notRegistered(fruits, request);
    }

    /** Existing farmer may be linked only with Seva Sindhu's mobile matching, or in assisted mode. */
    static boolean identityConfirmed(SevaSindhuCitizenRequest request, Farmer farmer) {
        if (Boolean.TRUE.equals(request.getAssistedMode())) return true;
        String ss = request.getMobileNumber();
        return ss != null && ss.equals(farmer.getMobileNumber() == null ? null : farmer.getMobileNumber().trim());
    }

    private void link(SevaSindhuCitizenRequest request, Farmer farmer) {
        request.setFarmerId(farmer.getFarmerId());
        request.setBeneficiaryType(BENEFICIARY_FARMER);
        request.setFruitsId(farmer.getFruitsId());
        request.setRequestStatus(SevaSindhuCitizenRequest.STATUS_FARMER_REGISTERED);
        request.setModifiedDate(new Date());
        requestRepository.save(request);
        log.info("Seva Sindhu request {} linked to farmer {}", request.getId(), farmer.getFarmerId());
    }

    private CheckResponse registeredReeler(com.sericulture.registration.model.entity.Reeler reeler, SevaSindhuCitizenRequest request) {
        return CheckResponse.builder()
                .status("REGISTERED")
                .beneficiaryType(BENEFICIARY_REELER)
                .fruitsId(reeler == null ? request.getFruitsId() : reeler.getFruitsId())
                .farmerName(reeler == null ? null : blankToNull(reeler.getReelerName()))
                .farmerNameInKannada(reeler == null ? null : blankToNull(reeler.getReelerNameKannada()))
                .registeredMobile(reeler == null ? null : mask(reeler.getMobileNumber()))
                .message("You are registered with the e-Reshme Portal as a reeler. You can continue with your application.")
                .build();
    }

    private CheckResponse registered(Farmer farmer, SevaSindhuCitizenRequest request) {
        return CheckResponse.builder()
                .status("REGISTERED")
                .beneficiaryType(BENEFICIARY_FARMER)
                .fruitsId(farmer == null ? request.getFruitsId() : farmer.getFruitsId())
                .farmerName(farmer == null ? null : fullName(farmer.getFirstName(), farmer.getLastName()))
                .farmerNameInKannada(farmer == null ? null : blankToNull(farmer.getNameKan()))
                .registeredMobile(farmer == null ? null : mask(farmer.getMobileNumber()))
                .message("You are registered with the e-Reshme Portal. You can continue with your application.")
                .build();
    }

    private CheckResponse notRegistered(GetFarmerResponse fruits, SevaSindhuCitizenRequest request) {
        FarmerResponse f = fruits.getFarmerResponse();
        List<LandItem> lands = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        Long dId = null, tId = null, hId = null, vId = null;
        if (fruits.getFarmerLandDetailsDTOList() != null) {
            for (FarmerLandDetailsDTO l : fruits.getFarmerLandDetailsDTOList()) {
                lands.add(LandItem.builder()
                        .surveyNumber(l.getSurveyNumber()).hissa(l.getHissa())
                        .acre(l.getAcre()).gunta(l.getGunta()).fGunta(l.getFGunta())
                        .ownerName(l.getOwnerName())
                        .districtName(l.getDistrictName()).talukName(l.getTalukName())
                        .hobliName(l.getHobliName()).villageName(l.getVillageName())
                        .build());
                if (l.getVillageId() == null) {
                    warnings.add("Survey " + nvl(l.getSurveyNumber()) + ": village " + nvl(l.getVillageName())
                            + " is not in e-Reshme yet. The officer will update this land record.");
                } else if (vId == null) {
                    dId = l.getDistrictId();
                    tId = l.getTalukId();
                    hId = l.getHobliId();
                    vId = l.getVillageId();
                }
            }
        }
        String addressText = null, pincode = null;
        if (fruits.getFarmerAddressDTOList() != null && !fruits.getFarmerAddressDTOList().isEmpty()) {
            FarmerAddressDTO a = fruits.getFarmerAddressDTOList().get(0);
            addressText = blankToNull(a.getAddressText());
            pincode = blankToNull(a.getPincode());
        }
        boolean ssMobile = request.getMobileNumber() != null;
        return CheckResponse.builder()
                .status("NOT_REGISTERED")
                .message("You are not registered with the e-Reshme Portal yet. Please complete your farmer registration to continue with this application.")
                .fruitsId(request.getFruitsId())
                // FarmerService's FRUITS mapping puts the father's name in middleName, so it is not part of the name.
                .farmerName(fullName(f.getFirstName(), f.getLastName()))
                .farmerNameInKannada(blankToNull(f.getNameKan()))
                .fatherName(blankToNull(f.getFatherName()))
                .sevaSindhuMobile(ssMobile ? mask(request.getMobileNumber()) : null)
                .mobileFromSevaSindhu(ssMobile)
                .addressText(addressText)
                .pincode(pincode != null && PINCODE.matcher(pincode).matches() ? pincode : null)
                .suggestedDistrictId(dId).suggestedTalukId(tId).suggestedHobliId(hId).suggestedVillageId(vId)
                .lands(lands)
                .warnings(warnings.isEmpty() ? null : warnings)
                .build();
    }

    // ------------------------------------------------------------------ register

    public CheckResponse register(SevaSindhuCitizenRequest request, RegisterRequest in) {
        if (in == null) {
            throw new CitizenException(400, "INVALID_INPUT", "Please fill in the registration details.");
        }
        String fruitsId = normaliseFruitsId(in.getFruitsId());
        if (request.getFarmerId() != null) {
            throw new CitizenException(409, "ALREADY_LINKED", "You are already registered for this Seva Sindhu request.");
        }
        if (!fruitsId.equals(request.getFruitsId())) {
            throw new CitizenException(409, "CHECK_FIRST", "Please verify your FRUITS ID again before registering.");
        }
        String mobile = request.getMobileNumber() != null ? request.getMobileNumber() : trim(in.getMobileNumber());
        validate(in, mobile);

        // Registered meanwhile (another tab, an office registration, or a retried request)?
        Farmer existing = farmerRepository.findByFruitsIdAndActive(fruitsId, true);
        if (existing != null) {
            if (identityConfirmed(request, existing) || Objects.equals(mobile, trim(existing.getMobileNumber()))) {
                link(request, existing);
                return registered(existing, request);
            }
            throw new CitizenException(409, "ALREADY_REGISTERED",
                    "This FRUITS ID is already registered with e-Reshme. Please visit your nearest Sericulture office.");
        }
        if (!farmerRepository.findByMobileNumber(mobile).isEmpty()) {
            throw new CitizenException(409, "MOBILE_IN_USE",
                    "This mobile number is already registered with another farmer in e-Reshme. "
                            + "Please use your own mobile number or visit your nearest Sericulture office.");
        }

        // Trusted personal and land data come from FRUITS, fetched again here (never from the browser).
        GetFarmerResponse fruits = fetchFromFruits(fruitsId);
        FarmerResponse f = fruits.getFarmerResponse();

        FarmerRequest farmer = new FarmerRequest();
        farmer.setFruitsId(fruitsId);
        farmer.setFirstName(trim(f.getFirstName()));
        farmer.setMiddleName(trim(f.getMiddleName()));
        farmer.setLastName(trim(f.getLastName()));
        farmer.setNameKan(trim(f.getNameKan()));
        farmer.setFatherName(trim(f.getFatherName()));
        farmer.setFatherNameKan(trim(f.getFatherNameKan()));
        farmer.setGenderId(f.getGenderId());
        farmer.setGenderStatus(f.getGenderStatus());
        farmer.setCasteId(f.getCasteId() != null && f.getCasteId() > 0 ? f.getCasteId() : null);
        farmer.setCasteStatus(f.getCasteStatus());
        farmer.setFarmerTypeId(f.getFarmerTypeId());
        farmer.setDifferentlyAbled(f.getDifferentlyAbled());
        farmer.setMinority(f.getMinority());
        farmer.setRdNumber(f.getRdNumber());
        farmer.setIsOtherStateFarmer(false);
        farmer.setMobileNumber(mobile);
        farmer.setPassbookNumber(trim(in.getPassbookNumber()));
        farmer.setTscMasterId(requestRepository.tscForLocation(in.getTalukId(), in.getHobliId(), in.getVillageId()));
        farmer.setRegistrationSource("SEVA_SINDHU");
        farmer.setFarmerNumber(newFarmerNumber());

        List<FarmerLandDetailsRequest> lands = new ArrayList<>();
        BigDecimal acres = BigDecimal.ZERO;
        if (fruits.getFarmerLandDetailsDTOList() != null) {
            for (FarmerLandDetailsDTO l : fruits.getFarmerLandDetailsDTOList()) {
                lands.add(toLandRequest(l));
                acres = acres.add(acres(l));
            }
        }
        farmer.setTotalLandHolding(acres.setScale(2, RoundingMode.HALF_UP).toPlainString());

        FarmerBankAccountRequest bank = new FarmerBankAccountRequest();
        bank.setFarmerBankName(trim(in.getBankName()));
        bank.setFarmerBankBranchName(trim(in.getBankBranchName()));
        bank.setFarmerBankIfscCode(trim(in.getIfscCode()).toUpperCase());
        bank.setFarmerBankAccountNumber(trim(in.getAccountNumber()));

        FarmerAddressRequest address = new FarmerAddressRequest();
        address.setStateId(KARNATAKA_STATE_ID);
        address.setDistrictId(in.getDistrictId());
        address.setTalukId(in.getTalukId());
        address.setHobliId(in.getHobliId());
        address.setVillageId(in.getVillageId());
        address.setAddressText(trim(in.getAddressText()));
        address.setPincode(trim(in.getPincode()));
        address.setDefaultAddress(true);

        FarmerSaveRequest save = new FarmerSaveRequest();
        save.setFarmerRequest(farmer);
        save.setFarmerBankAccountRequest(bank);
        save.setFarmerAddressRequests(List.of(address));
        save.setFarmerFamilyRequestList(new ArrayList<>());
        save.setFarmerLandDetailsRequests(lands);

        FarmerResponse saved = farmerService.insertCompleteFarmerDetails(save);
        if (saved == null || Boolean.TRUE.equals(saved.getError()) || saved.getFarmerId() == null) {
            String reason = saved == null ? null : saved.getError_description();
            log.warn("Seva Sindhu request {}: farmer save refused: {}", request.getId(), reason);
            throw new CitizenException(409, "REGISTRATION_REFUSED", friendlySaveError(reason));
        }
        Farmer created = farmerRepository.findByFarmerIdAndActive(saved.getFarmerId(), true);
        // Keep the original FRUITS values so later manual corrections never lose them.
        farmerChangeHistoryService.recordImport(created);
        link(request, created);
        log.info("Seva Sindhu request {}: farmer {} self-registered", request.getId(), saved.getFarmerId());
        CheckResponse response = registered(created, request);
        response.setMessage("Farmer registration completed successfully. You can now continue with your Seva Sindhu application.");
        return response;
    }

    static void validate(RegisterRequest in, String mobile) {
        if (mobile == null || !MOBILE.matcher(mobile).matches()) bad("Enter a valid 10-digit mobile number.");
        if (blank(in.getPassbookNumber()) || !PASSBOOK.matcher(trim(in.getPassbookNumber())).matches()) bad("Enter your bank passbook number.");
        if (blank(in.getBankName()) || !TEXT.matcher(trim(in.getBankName())).matches()) bad("Enter your bank name.");
        if (blank(in.getBankBranchName()) || !TEXT.matcher(trim(in.getBankBranchName())).matches()) bad("Enter your bank branch name.");
        if (blank(in.getIfscCode()) || !IFSC.matcher(trim(in.getIfscCode()).toUpperCase()).matches()) bad("Enter a valid 11-character IFSC code, for example SBIN0040510.");
        if (blank(in.getAccountNumber()) || !ACCOUNT.matcher(trim(in.getAccountNumber())).matches()) bad("Enter a valid bank account number (9 to 18 digits).");
        if (in.getDistrictId() == null || in.getTalukId() == null || in.getHobliId() == null || in.getVillageId() == null) bad("Select your district, taluk, hobli and village.");
        if (blank(in.getAddressText()) || trim(in.getAddressText()).length() < 5 || trim(in.getAddressText()).length() > 250) bad("Enter your address (5 to 250 characters).");
        if (blank(in.getPincode()) || !PINCODE.matcher(trim(in.getPincode())).matches()) bad("Enter a valid 6-digit Karnataka pincode.");
        if (!Boolean.TRUE.equals(in.getConsent())) bad("Please confirm the declaration to continue.");
    }

    /** Called by the controller after validate() so a bad location gives a clear message. */
    public void requireValidLocation(RegisterRequest in) {
        if (requestRepository.locationChainMatches(in.getDistrictId(), in.getTalukId(), in.getHobliId(), in.getVillageId()) != 1) {
            bad("Select your district, taluk, hobli and village again.");
        }
    }

    private static String friendlySaveError(String reason) {
        String r = reason == null ? "" : reason.toLowerCase();
        if (r.contains("bank")) return "This bank account number is already registered with another farmer. Please check it, or visit your nearest Sericulture office.";
        if (r.contains("mobile")) return "This mobile number is already registered with another farmer. Please visit your nearest Sericulture office.";
        if (r.contains("already")) return "This farmer is already registered with e-Reshme. Please visit your nearest Sericulture office.";
        return "Registration could not be completed. Please try again or visit your nearest Sericulture office.";
    }

    // ------------------------------------------------------------------ FRUITS

    private GetFarmerResponse fetchFromFruits(String fruitsId) {
        GetFarmerRequest req = new GetFarmerRequest();
        req.setFruitsId(fruitsId);
        GetFarmerResponse res;
        try {
            res = farmerService.getDetailsByFruitsId(req);
        } catch (Exception e) {
            log.error("FRUITS lookup failed for a Seva Sindhu registration", e);
            throw new CitizenException(503, "FRUITS_UNAVAILABLE",
                    "We could not fetch your details from FRUITS right now. Please try again in a little while.");
        }
        // The existing FRUITS client reports every failure (unknown id, FRUITS down, 401 from FRUITS) the same
        // way, so the message must not claim the farmer does not exist.
        if (res == null || res.getFarmerResponse() == null || blank(res.getFarmerResponse().getFirstName())) {
            throw new CitizenException(404, "NOT_IN_FRUITS",
                    "We could not get details for this FRUITS ID from FRUITS. Please check the number and try again. "
                            + "If it is correct, FRUITS may be unavailable; please try again later.");
        }
        return res;
    }

    private static FarmerLandDetailsRequest toLandRequest(FarmerLandDetailsDTO l) {
        FarmerLandDetailsRequest r = new FarmerLandDetailsRequest();
        r.setSurveyNumber(l.getSurveyNumber());
        r.setHissa(l.getHissa());
        r.setSurNoc(l.getSurNoc());
        r.setOwnerName(l.getOwnerName());
        r.setNameScore(l.getNameScore());
        r.setOwnerNo(l.getOwnerNo());
        r.setMainOwnerNo(l.getMainOwnerNo());
        r.setAcre(l.getAcre());
        r.setGunta(l.getGunta());
        r.setFGunta(l.getFGunta());
        r.setLandCode(l.getLandCode());
        r.setDistrictCode(l.getDistrictCode());
        r.setTalukCode(l.getTalukCode());
        r.setHobliCode(l.getHobliCode());
        r.setVillageCode(l.getVillageCode());
        r.setStateId(l.getStateId());
        r.setDistrictId(l.getDistrictId());
        r.setTalukId(l.getTalukId());
        r.setHobliId(l.getHobliId());
        r.setVillageId(l.getVillageId());
        return r;
    }

    /** acre + gunta/40 + fgunta/640 (1 acre = 40 guntas = 640 fractional guntas). */
    static BigDecimal acres(FarmerLandDetailsDTO l) {
        BigDecimal a = BigDecimal.valueOf(l.getAcre() == null ? 0 : l.getAcre());
        BigDecimal g = BigDecimal.valueOf(l.getGunta() == null ? 0 : l.getGunta()).divide(BigDecimal.valueOf(40), 6, RoundingMode.HALF_UP);
        BigDecimal fg = BigDecimal.valueOf(l.getFGunta() == null ? 0 : l.getFGunta()).divide(BigDecimal.valueOf(640), 6, RoundingMode.HALF_UP);
        return a.add(g).add(fg);
    }

    /** Same format the registration screen uses (HHmmssddMMyyyy), made unique server-side. */
    private String newFarmerNumber() {
        LocalDateTime t = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HHmmssddMMyyyy");
        for (int i = 0; i < 120; i++) {
            String number = t.plusSeconds(i).format(fmt);
            if (farmerRepository.findByFarmerNumber(number).isEmpty()) return number;
        }
        throw new CitizenException(503, "SERVICE_UNAVAILABLE", "Registration is busy right now. Please try again in a minute.");
    }

    // ------------------------------------------------------------------ locations

    public List<Option> options(List<Object[]> rows) {
        List<Option> out = new ArrayList<>();
        for (Object[] r : rows) {
            out.add(new Option(((Number) r[0]).longValue(), blankToNull(r[1]), blankToNull(r[2])));
        }
        return out;
    }

    // ------------------------------------------------------------------ helpers

    static String normaliseFruitsId(String raw) {
        String v = raw == null ? "" : raw.trim().toUpperCase();
        if (!FRUITS_ID.matcher(v).matches()) {
            throw new CitizenException(400, "INVALID_INPUT", "Enter a valid FRUITS ID: FID followed by 13 digits, for example FID2806000049272.");
        }
        return v;
    }

    private static void bad(String message) {
        throw new CitizenException(400, "INVALID_INPUT", message);
    }

    static String mask(String mobile) {
        String m = mobile == null ? null : mobile.trim();
        if (m == null || m.length() < 4) return null;
        return m.substring(0, 2) + "X".repeat(m.length() - 4) + m.substring(m.length() - 2);
    }

    private static String fullName(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p != null && !p.isBlank()) sb.append(sb.length() > 0 ? " " : "").append(p.trim());
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    private static String blankToNull(Object o) {
        if (o == null) return null;
        String s = o.toString().trim();
        return s.isEmpty() ? null : s;
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }

    private static String nvl(String s) {
        return s == null ? "-" : s;
    }

    static String sha256Hex(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
