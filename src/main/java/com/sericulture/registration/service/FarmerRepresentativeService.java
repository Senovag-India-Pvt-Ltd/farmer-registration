package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmerRepresentative.FarmerRepresentativeRequest;
import com.sericulture.registration.model.api.farmerRepresentative.FarmerRepresentativeResponse;
import com.sericulture.registration.model.entity.FarmerRepresentative;
import com.sericulture.registration.repository.FarmerRepresentativeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Authorised agents / representatives of a farmer (SR-005). A farmer may have at most
 * {@code FARMER_MAX_REPRESENTATIVES} active agents (default 3), enforced with the farmer row locked so parallel adds
 * cannot exceed it. Each agent is authorised from {@code authorisedFrom} up to and including {@code authorisedTo}
 * (open ended when empty). Aadhaar numbers are stored masked. Market transactions done through an agent are linked
 * in market-auction (farmer_representative_txn).
 */
@Service
@Slf4j
public class FarmerRepresentativeService {

    public static final String AADHAAR = "AADHAAR";
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private final FarmerRepresentativeRepository repository;
    private final int maxRepresentatives;

    public FarmerRepresentativeService(FarmerRepresentativeRepository repository,
                                       @Value("${FARMER_MAX_REPRESENTATIVES:3}") int maxRepresentatives) {
        this.repository = repository;
        this.maxRepresentatives = Math.max(1, maxRepresentatives);
    }

    public int getMaxRepresentatives() {
        return maxRepresentatives;
    }

    public Map<String, Object> config() {
        Map<String, Object> m = new HashMap<>();
        m.put("maxRepresentatives", maxRepresentatives);
        return m;
    }

    public Map<String, Object> getByFarmerId(Long farmerId) {
        List<FarmerRepresentative> rows = farmerId == null ? List.of()
                : repository.findByFarmerIdAndActiveOrderByFarmerRepresentativeIdAsc(farmerId, true);
        Map<Long, String> relationNames = relationshipNames(rows);
        LocalDate today = today();
        List<FarmerRepresentativeResponse> list = rows.stream().map(r -> toResponse(r, relationNames, today)).collect(Collectors.toList());
        Map<String, Object> m = new HashMap<>();
        m.put("farmerRepresentative", list);
        m.put("totalItems", list.size());
        m.put("maxRepresentatives", maxRepresentatives);
        return m;
    }

    @Transactional
    public FarmerRepresentativeResponse add(FarmerRepresentativeRequest request) {
        if (repository.lockActiveFarmer(request.getFarmerId()).isEmpty()) {
            return error("Farmer not found or inactive");
        }
        long active = repository.countByFarmerIdAndActive(request.getFarmerId(), true);
        if (active >= maxRepresentatives) {
            return error("Maximum " + maxRepresentatives + " agent" + (maxRepresentatives == 1 ? "" : "s") + " allowed for a farmer");
        }
        String mobile = trim(request.getMobileNumber());
        if (mobileTaken(request.getFarmerId(), mobile, null)) {
            return error("An agent with this mobile number is already added for this farmer");
        }
        String idError = validateIdProof(request, null);
        if (idError != null) return error(idError);
        String periodError = validatePeriod(request);
        if (periodError != null) return error(periodError);

        FarmerRepresentative r = new FarmerRepresentative();
        r.setFarmerId(request.getFarmerId());
        apply(r, request);
        r.setActive(true);
        FarmerRepresentative saved = repository.save(r);
        return toResponse(saved, relationshipNames(List.of(saved)), today());
    }

    @Transactional
    public FarmerRepresentativeResponse edit(FarmerRepresentativeRequest request) {
        if (request.getFarmerRepresentativeId() == null) {
            return error("farmerRepresentativeId is required");
        }
        FarmerRepresentative r = repository.findByFarmerRepresentativeIdAndActive(request.getFarmerRepresentativeId(), true).orElse(null);
        if (r == null || !Objects.equals(r.getFarmerId(), request.getFarmerId())) {
            return error("Agent not found");
        }
        String mobile = trim(request.getMobileNumber());
        if (mobileTaken(r.getFarmerId(), mobile, r.getFarmerRepresentativeId())) {
            return error("An agent with this mobile number is already added for this farmer");
        }
        String idError = validateIdProof(request, r);
        if (idError != null) return error(idError);
        if (request.getAuthorisedFrom() == null) request.setAuthorisedFrom(r.getAuthorisedFrom());
        String periodError = validatePeriod(request);
        if (periodError != null) return error(periodError);

        apply(r, request);
        FarmerRepresentative saved = repository.save(r);
        return toResponse(saved, relationshipNames(List.of(saved)), today());
    }

    @Transactional
    public FarmerRepresentativeResponse delete(Long farmerRepresentativeId) {
        FarmerRepresentative r = farmerRepresentativeId == null ? null
                : repository.findByFarmerRepresentativeIdAndActive(farmerRepresentativeId, true).orElse(null);
        if (r == null) {
            return error("Agent not found");
        }
        r.setActive(false);
        repository.save(r);
        FarmerRepresentativeResponse res = toResponse(r, Map.of(), today());
        res.setError(false);
        return res;
    }

    /** Agents by exact mobile number or by name (contains), each with the farmer it is linked to. */
    public Map<String, Object> search(String query) {
        String q = query == null ? "" : query.trim();
        String digits = q.replaceAll("\\D", "");
        boolean byMobile = !digits.isEmpty() && digits.length() == q.replaceAll("[\\s+-]", "").length();
        if (byMobile && digits.length() > 10) digits = digits.substring(digits.length() - 10);
        String mobile = byMobile ? digits : "";
        String name = byMobile ? "" : q;
        Map<String, Object> out = new HashMap<>();
        if ((byMobile && mobile.length() != 10) || (!byMobile && name.length() < 3)) {
            out.put("error", true);
            out.put("error_description", "Enter the agent's 10-digit mobile number or at least 3 letters of the name");
            out.put("farmerRepresentative", List.of());
            return out;
        }
        LocalDate today = today();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object[] o : repository.search(mobile, name)) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("farmerRepresentativeId", num(o[0]));
            m.put("name", str(o[1]));
            m.put("mobileNumber", str(o[2]));
            m.put("address", str(o[3]));
            m.put("relationshipId", num(o[4]));
            m.put("relationshipName", str(o[5]));
            LocalDate from = date(o[6]);
            LocalDate to = date(o[7]);
            m.put("authorisedFrom", from);
            m.put("authorisedTo", to);
            m.put("authorisedToday", isAuthorisedOn(from, to, today));
            m.put("farmerId", num(o[8]));
            m.put("farmerName", str(o[9]));
            m.put("fruitsId", str(o[10]));
            m.put("farmerNumber", str(o[11]));
            m.put("farmerMobileNumber", str(o[12]));
            rows.add(m);
        }
        out.put("error", false);
        out.put("farmerRepresentative", rows);
        out.put("totalItems", rows.size());
        return out;
    }

    // ------------------------------------------------------------------ helpers

    static boolean isAuthorisedOn(LocalDate from, LocalDate to, LocalDate day) {
        if (day == null) return false;
        if (from != null && day.isBefore(from)) return false;
        return to == null || !day.isAfter(to);
    }

    private void apply(FarmerRepresentative r, FarmerRepresentativeRequest request) {
        r.setName(trim(request.getName()));
        r.setMobileNumber(trim(request.getMobileNumber()));
        r.setAddress(trim(request.getAddress()));
        r.setRelationshipId(request.getRelationshipId());
        r.setAuthorisedFrom(request.getAuthorisedFrom() == null ? today() : request.getAuthorisedFrom());
        r.setAuthorisedTo(request.getAuthorisedTo());
        String type = blankToNull(request.getIdProofType());
        String number = blankToNull(request.getIdProofNumber());
        if (type == null) {
            r.setIdProofType(null);
            r.setIdProofNumber(null);
            return;
        }
        boolean keepStoredAadhaar = AADHAAR.equals(type) && number != null && ReelerRepresentativeService.isMasked(number)
                && AADHAAR.equals(r.getIdProofType()) && r.getIdProofNumber() != null;
        r.setIdProofType(type);
        if (keepStoredAadhaar) return; // unchanged masked value sent back from the edit form
        if (AADHAAR.equals(type)) {
            r.setIdProofNumber(number == null ? null : ReelerRepresentativeService.maskAadhaar(number));
        } else {
            r.setIdProofNumber(number == null ? null : number.toUpperCase());
        }
    }

    /** Null when fine, otherwise the message. {@code existing} is the row being edited (null for add). */
    static String validateIdProof(FarmerRepresentativeRequest request, FarmerRepresentative existing) {
        String type = blankToNull(request.getIdProofType());
        String number = blankToNull(request.getIdProofNumber());
        if (type == null) return null;
        if (number == null) return "Enter the ID proof number";
        if (AADHAAR.equals(type)) {
            boolean keepsMasked = ReelerRepresentativeService.isMasked(number) && existing != null
                    && AADHAAR.equals(existing.getIdProofType()) && number.equals(existing.getIdProofNumber());
            if (!keepsMasked && !number.replaceAll("\\s", "").matches("\\d{12}")) {
                return "Enter a valid 12-digit Aadhaar number";
            }
        }
        return null;
    }

    static String validatePeriod(FarmerRepresentativeRequest request) {
        LocalDate from = request.getAuthorisedFrom();
        LocalDate to = request.getAuthorisedTo();
        if (from != null && to != null && to.isBefore(from)) {
            return "Authorised till date cannot be before the authorised from date";
        }
        return null;
    }

    private boolean mobileTaken(Long farmerId, String mobile, Long exceptId) {
        return repository.findByFarmerIdAndActiveOrderByFarmerRepresentativeIdAsc(farmerId, true).stream()
                .anyMatch(x -> mobile != null && mobile.equals(x.getMobileNumber()) && !Objects.equals(x.getFarmerRepresentativeId(), exceptId));
    }

    private Map<Long, String> relationshipNames(List<FarmerRepresentative> rows) {
        List<Long> ids = rows.stream().map(FarmerRepresentative::getRelationshipId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> m = new HashMap<>();
        if (ids.isEmpty()) return m;
        try {
            for (Object[] o : repository.relationshipNames(ids)) {
                if (o[0] != null) m.put(((Number) o[0]).longValue(), o[1] == null ? null : o[1].toString());
            }
        } catch (RuntimeException e) {
            log.warn("Relationship names could not be loaded for farmer agents", e);
        }
        return m;
    }

    private static FarmerRepresentativeResponse toResponse(FarmerRepresentative r, Map<Long, String> relationNames, LocalDate today) {
        return FarmerRepresentativeResponse.builder()
                .farmerRepresentativeId(r.getFarmerRepresentativeId())
                .farmerId(r.getFarmerId())
                .name(r.getName())
                .mobileNumber(r.getMobileNumber())
                .address(r.getAddress())
                .relationshipId(r.getRelationshipId())
                .relationshipName(r.getRelationshipId() == null ? null : relationNames.get(r.getRelationshipId()))
                .idProofType(r.getIdProofType())
                .idProofNumber(r.getIdProofNumber())
                .authorisedFrom(r.getAuthorisedFrom())
                .authorisedTo(r.getAuthorisedTo())
                .authorisedToday(isAuthorisedOn(r.getAuthorisedFrom(), r.getAuthorisedTo(), today))
                .error(false)
                .build();
    }

    private static FarmerRepresentativeResponse error(String message) {
        return FarmerRepresentativeResponse.builder().error(true).error_description(message).build();
    }

    private static LocalDate today() {
        return LocalDate.now(IST);
    }

    private static LocalDate date(Object o) {
        if (o == null) return null;
        if (o instanceof LocalDate d) return d;
        if (o instanceof java.sql.Date d) return d.toLocalDate();
        if (o instanceof java.sql.Timestamp t) return t.toLocalDateTime().toLocalDate();
        return LocalDate.parse(o.toString().substring(0, 10));
    }

    private static Long num(Object o) {
        return o instanceof Number n ? n.longValue() : null;
    }

    private static String str(Object o) {
        return o == null ? null : o.toString().trim();
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
