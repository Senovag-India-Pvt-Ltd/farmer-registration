package com.sericulture.registration.sevasindhu;

import com.sericulture.registration.model.api.farmer.FarmerResponse;
import com.sericulture.registration.model.api.farmer.FarmerSaveRequest;
import com.sericulture.registration.model.api.farmer.GetFarmerResponse;
import com.sericulture.registration.model.dto.farmer.FarmerLandDetailsDTO;
import com.sericulture.registration.model.entity.Farmer;
import com.sericulture.registration.repository.FarmerRepository;
import com.sericulture.registration.service.FarmerService;
import com.sericulture.registration.sevasindhu.SevaSindhuFarmerDtos.CheckResponse;
import com.sericulture.registration.sevasindhu.SevaSindhuFarmerDtos.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Seva Sindhu citizen farmer check / self-registration rules. */
@ExtendWith(MockitoExtension.class)
class SevaSindhuFarmerServiceTest {

    private static final String FID = "FID2806000049272";

    @Mock
    private SevaSindhuCitizenRequestRepository requestRepository;
    @Mock
    private FarmerRepository farmerRepository;
    @Mock
    private FarmerService farmerService;

    private SevaSindhuFarmerService service;

    @BeforeEach
    void setUp() {
        service = new SevaSindhuFarmerService(requestRepository, farmerRepository, farmerService, 60);
    }

    static SevaSindhuCitizenRequest request(String mobile, boolean assisted) {
        SevaSindhuCitizenRequest r = new SevaSindhuCitizenRequest();
        r.setId(7L);
        r.setMobileNumber(mobile);
        r.setAssistedMode(assisted);
        r.setRequestStatus(SevaSindhuCitizenRequest.STATUS_RECEIVED);
        return r;
    }

    static Farmer farmer(String mobile) {
        Farmer f = new Farmer();
        f.setFarmerId(124380L);
        f.setFruitsId(FID);
        f.setFirstName("Vijaykumar");
        f.setLastName("M");
        f.setMobileNumber(mobile);
        return f;
    }

    static GetFarmerResponse fruits() {
        FarmerResponse fr = new FarmerResponse();
        fr.setFirstName("Vijaykumar M");
        fr.setFatherName("Muniyappa");
        fr.setNameKan("ವಿಜಯಕುಮಾರ್");
        fr.setGenderId(1L);
        fr.setCasteId(0L);
        FarmerLandDetailsDTO land = new FarmerLandDetailsDTO();
        land.setSurveyNumber("12");
        land.setAcre(1L);
        land.setGunta(20L);
        land.setFGunta(0d);
        land.setDistrictId(23L);
        land.setTalukId(182L);
        land.setHobliId(877L);
        land.setVillageId(29114L);
        land.setVillageName("MUTTUKADAHALLI");
        FarmerLandDetailsDTO unmatched = new FarmerLandDetailsDTO();
        unmatched.setSurveyNumber("40");
        unmatched.setAcre(0L);
        unmatched.setGunta(10L);
        unmatched.setVillageName("NEWVILLAGE");
        GetFarmerResponse res = new GetFarmerResponse();
        res.setFarmerResponse(fr);
        res.setFarmerLandDetailsDTOList(List.of(land, unmatched));
        return res;
    }

    static RegisterRequest validRegistration() {
        RegisterRequest r = new RegisterRequest();
        r.setFruitsId(FID);
        r.setMobileNumber("9876543210");
        r.setPassbookNumber("207796");
        r.setBankName("State Bank of India");
        r.setBankBranchName("Chintamani");
        r.setIfscCode("sbin0040510");
        r.setAccountNumber("12345678901");
        r.setDistrictId(23L);
        r.setTalukId(182L);
        r.setHobliId(877L);
        r.setVillageId(29114L);
        r.setAddressText("Muttukadahalli, Chintamani");
        r.setPincode("563125");
        r.setConsent(true);
        return r;
    }

    private void saveReturnsArgument() {
        when(requestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    // ---------------------------------------------------------------- session

    @Test
    void session_mustBeWellFormedKnownAndUnexpired() {
        assertEquals(401, assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> service.requireSession(null)).getStatus());
        String token = "A".repeat(43);
        when(requestRepository.findBySessionHashAndActive(SevaSindhuFarmerService.sha256Hex(token), true)).thenReturn(Optional.empty());
        assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> service.requireSession(token));

        SevaSindhuCitizenRequest expired = request(null, false);
        expired.setSessionExpiresAt(new Date(System.currentTimeMillis() - 1));
        String t2 = "B".repeat(43);
        when(requestRepository.findBySessionHashAndActive(SevaSindhuFarmerService.sha256Hex(t2), true)).thenReturn(Optional.of(expired));
        assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> service.requireSession(t2));
    }

    // ---------------------------------------------------------------- check

    @Test
    void badFruitsId_isRejectedBeforeAnyLookup() {
        var e = assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> service.check(request(null, false), "2806000049272123"));
        assertEquals(400, e.getStatus());
        verifyNoInteractions(farmerRepository, farmerService);
    }

    @Test
    void registeredFarmer_withMatchingSevaSindhuMobile_isLinked() {
        SevaSindhuCitizenRequest req = request("7624939949", false);
        when(farmerRepository.findByFruitsIdAndActive(FID, true)).thenReturn(farmer("7624939949"));
        saveReturnsArgument();
        CheckResponse res = service.check(req, " fid2806000049272 ");
        assertEquals("REGISTERED", res.getStatus());
        assertEquals("76XXXXXX49", res.getRegisteredMobile());
        assertEquals(124380L, req.getFarmerId());
        assertEquals(SevaSindhuCitizenRequest.STATUS_FARMER_REGISTERED, req.getRequestStatus());
        verifyNoInteractions(farmerService);
    }

    @Test
    void registeredFarmer_withDifferentOrMissingMobile_isNotLinked() {
        when(farmerRepository.findByFruitsIdAndActive(FID, true)).thenReturn(farmer("7624939949"));
        SevaSindhuCitizenRequest other = request("9999999999", false);
        assertEquals("MOBILE_MISMATCH", service.check(other, FID).getStatus());
        assertNull(other.getFarmerId());
        SevaSindhuCitizenRequest none = request(null, false);
        assertEquals("MOBILE_MISMATCH", service.check(none, FID).getStatus());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void registeredFarmer_inAssistedMode_isLinked() {
        SevaSindhuCitizenRequest req = request(null, true);
        when(farmerRepository.findByFruitsIdAndActive(FID, true)).thenReturn(farmer("7624939949"));
        saveReturnsArgument();
        assertEquals("REGISTERED", service.check(req, FID).getStatus());
        assertEquals(124380L, req.getFarmerId());
    }

    @Test
    void unregisteredFarmer_getsFruitsPrefill_andRequestRemembersTheFruitsId() throws Exception {
        SevaSindhuCitizenRequest req = request("9876543210", false);
        when(farmerRepository.findByFruitsIdAndActive(FID, true)).thenReturn(null);
        when(farmerService.getDetailsByFruitsId(any())).thenReturn(fruits());
        saveReturnsArgument();
        CheckResponse res = service.check(req, FID);
        assertEquals("NOT_REGISTERED", res.getStatus());
        assertTrue(res.getMessage().startsWith("You are not registered with the e-Reshme Portal yet"));
        assertEquals("Vijaykumar M", res.getFarmerName());
        assertEquals(true, res.getMobileFromSevaSindhu());
        assertEquals("98XXXXXX10", res.getSevaSindhuMobile());
        assertEquals(29114L, res.getSuggestedVillageId());
        assertEquals(2, res.getLands().size());
        assertEquals(1, res.getWarnings().size());
        assertEquals(FID, req.getFruitsId());
        assertNull(req.getFarmerId());
    }

    @Test
    void fruitsDown_or_unknownInFruits_givesClearErrors() throws Exception {
        when(farmerRepository.findByFruitsIdAndActive(FID, true)).thenReturn(null);
        when(farmerService.getDetailsByFruitsId(any())).thenThrow(new RuntimeException("timeout"));
        assertEquals(503, assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> service.check(request(null, false), FID)).getStatus());

        reset(farmerService);
        when(farmerService.getDetailsByFruitsId(any())).thenReturn(new GetFarmerResponse());
        assertEquals(404, assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> service.check(request(null, false), FID)).getStatus());
    }

    @Test
    void alreadyLinkedRequest_cannotSwitchToAnotherFruitsId() {
        SevaSindhuCitizenRequest req = request(null, false);
        req.setFarmerId(1L);
        req.setFruitsId("FID1111111111111");
        assertEquals(409, assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> service.check(req, FID)).getStatus());
    }

    // ---------------------------------------------------------------- register

    @Test
    void registration_validation() {
        RegisterRequest r = validRegistration();
        assertDoesNotThrow(() -> SevaSindhuFarmerService.validate(r, "9876543210"));
        r.setIfscCode("SBIN040510");
        assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> SevaSindhuFarmerService.validate(r, "9876543210"));
        RegisterRequest r2 = validRegistration();
        r2.setPincode("400001");
        assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> SevaSindhuFarmerService.validate(r2, "9876543210"));
        RegisterRequest r3 = validRegistration();
        r3.setConsent(false);
        assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> SevaSindhuFarmerService.validate(r3, "9876543210"));
        assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> SevaSindhuFarmerService.validate(validRegistration(), "12345"));
        RegisterRequest r4 = validRegistration();
        r4.setAccountNumber("12AB");
        assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> SevaSindhuFarmerService.validate(r4, "9876543210"));
    }

    @Test
    void register_requiresTheCheckedFruitsId() {
        SevaSindhuCitizenRequest req = request(null, false);
        req.setFruitsId("FID1111111111111");
        assertEquals(409, assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> service.register(req, validRegistration())).getStatus());
        verifyNoInteractions(farmerService);
    }

    @Test
    void register_usesFruitsData_sevaSindhuMobile_andExistingSave_thenLinks() throws Exception {
        SevaSindhuCitizenRequest req = request("9876543210", false);
        req.setFruitsId(FID);
        when(farmerRepository.findByFruitsIdAndActive(FID, true)).thenReturn(null);
        when(farmerRepository.findByMobileNumber("9876543210")).thenReturn(Collections.emptyList());
        when(farmerRepository.findByFarmerNumber(any())).thenReturn(Collections.emptyList());
        when(farmerService.getDetailsByFruitsId(any())).thenReturn(fruits());
        when(requestRepository.tscForLocation(182L, 877L, 29114L)).thenReturn(39L);
        FarmerResponse saved = new FarmerResponse();
        saved.setFarmerId(900001L);
        when(farmerService.insertCompleteFarmerDetails(any())).thenReturn(saved);
        Farmer created = farmer("9876543210");
        created.setFarmerId(900001L);
        when(farmerRepository.findByFarmerIdAndActive(900001L, true)).thenReturn(created);
        saveReturnsArgument();

        RegisterRequest in = validRegistration();
        in.setMobileNumber("6000000000"); // ignored: Seva Sindhu supplied the mobile
        CheckResponse res = service.register(req, in);

        ArgumentCaptor<FarmerSaveRequest> sent = ArgumentCaptor.forClass(FarmerSaveRequest.class);
        verify(farmerService).insertCompleteFarmerDetails(sent.capture());
        FarmerSaveRequest s = sent.getValue();
        assertEquals("Vijaykumar M", s.getFarmerRequest().getFirstName());
        assertEquals("9876543210", s.getFarmerRequest().getMobileNumber());
        assertEquals("SEVA_SINDHU", s.getFarmerRequest().getRegistrationSource());
        assertEquals(39L, s.getFarmerRequest().getTscMasterId());
        assertNull(s.getFarmerRequest().getCasteId(), "caste 0 (no FRUITS match) is not saved as a fake id");
        assertEquals("1.75", s.getFarmerRequest().getTotalLandHolding()); // 1a20g + 0a10g
        assertEquals(14, s.getFarmerRequest().getFarmerNumber().length());
        assertEquals("SBIN0040510", s.getFarmerBankAccountRequest().getFarmerBankIfscCode());
        assertEquals(2L, s.getFarmerAddressRequests().get(0).getStateId());
        assertEquals(2, s.getFarmerLandDetailsRequests().size());
        assertNotNull(s.getFarmerFamilyRequestList());

        assertEquals("REGISTERED", res.getStatus());
        assertTrue(res.getMessage().startsWith("Farmer registration completed successfully"));
        assertEquals(900001L, req.getFarmerId());
        assertEquals(SevaSindhuCitizenRequest.STATUS_FARMER_REGISTERED, req.getRequestStatus());
    }

    @Test
    void register_mobileAlreadyUsedByAnotherFarmer_isRefused() {
        SevaSindhuCitizenRequest req = request(null, false);
        req.setFruitsId(FID);
        when(farmerRepository.findByFruitsIdAndActive(FID, true)).thenReturn(null);
        when(farmerRepository.findByMobileNumber("9876543210")).thenReturn(List.of(new Farmer()));
        var e = assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> service.register(req, validRegistration()));
        assertEquals("MOBILE_IN_USE", e.getCode());
        verifyNoInteractions(farmerService);
    }

    @Test
    void register_whenSaveRefuses_reportsAFriendlyReason_andDoesNotLink() throws Exception {
        SevaSindhuCitizenRequest req = request(null, false);
        req.setFruitsId(FID);
        when(farmerRepository.findByFruitsIdAndActive(FID, true)).thenReturn(null);
        when(farmerRepository.findByMobileNumber(any())).thenReturn(Collections.emptyList());
        when(farmerRepository.findByFarmerNumber(any())).thenReturn(Collections.emptyList());
        when(farmerService.getDetailsByFruitsId(any())).thenReturn(fruits());
        FarmerResponse refused = new FarmerResponse();
        refused.setError(true);
        refused.setError_description("Bank account number already exists");
        when(farmerService.insertCompleteFarmerDetails(any())).thenReturn(refused);
        var e = assertThrows(SevaSindhuFarmerService.CitizenException.class, () -> service.register(req, validRegistration()));
        assertTrue(e.getMessage().contains("bank account"));
        assertNull(req.getFarmerId());
    }

    @Test
    void acres_convertsGuntas() {
        FarmerLandDetailsDTO l = new FarmerLandDetailsDTO();
        l.setAcre(2L);
        l.setGunta(20L);
        l.setFGunta(160d);
        assertEquals(0, new BigDecimal("2.75").compareTo(SevaSindhuFarmerService.acres(l).setScale(2, java.math.RoundingMode.HALF_UP)));
    }
}
