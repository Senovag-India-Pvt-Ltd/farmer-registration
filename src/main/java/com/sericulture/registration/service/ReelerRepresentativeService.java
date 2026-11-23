package com.sericulture.registration.service;

import com.sericulture.registration.model.api.reelerRepresentative.ReelerRepresentativeRequest;
import com.sericulture.registration.model.api.reelerRepresentative.ReelerRepresentativeResponse;
import com.sericulture.registration.model.entity.ReelerRepresentative;
import com.sericulture.registration.repository.ReelerRepresentativeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Reeler representatives / agents (SR-013). A reeler may have at most {@code REELER_MAX_REPRESENTATIVES} active
 * representatives (default 3 per RFP; the SRS says 2, so the number is configurable). The limit is enforced here,
 * with the reeler row locked so parallel adds cannot exceed it. Aadhaar numbers are stored masked.
 */
@Service
@Slf4j
public class ReelerRepresentativeService {

    public static final String AADHAAR = "AADHAAR";

    private final ReelerRepresentativeRepository repository;
    private final int maxRepresentatives;

    public ReelerRepresentativeService(ReelerRepresentativeRepository repository,
                                       @Value("${REELER_MAX_REPRESENTATIVES:3}") int maxRepresentatives) {
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

    public Map<String, Object> getByReelerId(Long reelerId) {
        List<ReelerRepresentative> rows = reelerId == null ? List.of()
                : repository.findByReelerIdAndActiveOrderByReelerRepresentativeIdAsc(reelerId, true);
        Map<Long, String> relationNames = relationshipNames(rows);
        List<ReelerRepresentativeResponse> list = rows.stream().map(r -> toResponse(r, relationNames)).collect(Collectors.toList());
        Map<String, Object> m = new HashMap<>();
        m.put("reelerRepresentative", list);
        m.put("totalItems", list.size());
        m.put("maxRepresentatives", maxRepresentatives);
        return m;
    }

    @Transactional
    public ReelerRepresentativeResponse add(ReelerRepresentativeRequest request) {
        if (repository.lockActiveReeler(request.getReelerId()).isEmpty()) {
            return error("Reeler not found or inactive");
        }
        long active = repository.countByReelerIdAndActive(request.getReelerId(), true);
        if (active >= maxRepresentatives) {
            return error("Maximum " + maxRepresentatives + " representative" + (maxRepresentatives == 1 ? "" : "s") + " allowed for a reeler");
        }
        String mobile = trim(request.getMobileNumber());
        if (mobileTaken(request.getReelerId(), mobile, null)) {
            return error("A representative with this mobile number is already added for this reeler");
        }
        String idError = validateIdProof(request, null);
        if (idError != null) return error(idError);

        ReelerRepresentative r = new ReelerRepresentative();
        r.setReelerId(request.getReelerId());
        apply(r, request);
        r.setActive(true);
        ReelerRepresentative saved = repository.save(r);
        return toResponse(saved, relationshipNames(List.of(saved)));
    }

    @Transactional
    public ReelerRepresentativeResponse edit(ReelerRepresentativeRequest request) {
        if (request.getReelerRepresentativeId() == null) {
            return error("reelerRepresentativeId is required");
        }
        ReelerRepresentative r = repository.findByReelerRepresentativeIdAndActive(request.getReelerRepresentativeId(), true).orElse(null);
        if (r == null || !Objects.equals(r.getReelerId(), request.getReelerId())) {
            return error("Representative not found");
        }
        String mobile = trim(request.getMobileNumber());
        if (mobileTaken(r.getReelerId(), mobile, r.getReelerRepresentativeId())) {
            return error("A representative with this mobile number is already added for this reeler");
        }
        String idError = validateIdProof(request, r);
        if (idError != null) return error(idError);

        apply(r, request);
        ReelerRepresentative saved = repository.save(r);
        return toResponse(saved, relationshipNames(List.of(saved)));
    }

    @Transactional
    public ReelerRepresentativeResponse delete(Long reelerRepresentativeId) {
        ReelerRepresentative r = reelerRepresentativeId == null ? null
                : repository.findByReelerRepresentativeIdAndActive(reelerRepresentativeId, true).orElse(null);
        if (r == null) {
            return error("Representative not found");
        }
        r.setActive(false);
        repository.save(r);
        ReelerRepresentativeResponse res = toResponse(r, Map.of());
        res.setError(false);
        return res;
    }

    // ------------------------------------------------------------------ helpers

    private void apply(ReelerRepresentative r, ReelerRepresentativeRequest request) {
        r.setName(trim(request.getName()));
        r.setMobileNumber(trim(request.getMobileNumber()));
        r.setAddress(trim(request.getAddress()));
        r.setRelationshipId(request.getRelationshipId());
        String type = blankToNull(request.getIdProofType());
        String number = blankToNull(request.getIdProofNumber());
        if (type == null) {
            r.setIdProofType(null);
            r.setIdProofNumber(null);
            return;
        }
        r.setIdProofType(type);
        if (AADHAAR.equals(type)) {
            // An unchanged masked value sent back from the edit form keeps the stored (masked) number.
            if (number != null && isMasked(number) && AADHAAR.equals(r.getIdProofType()) && r.getIdProofNumber() != null) {
                return;
            }
            r.setIdProofNumber(number == null ? null : maskAadhaar(number));
        } else {
            r.setIdProofNumber(number == null ? null : number.toUpperCase());
        }
    }

    /** Null when fine, otherwise the message. {@code existing} is the row being edited (null for add). */
    static String validateIdProof(ReelerRepresentativeRequest request, ReelerRepresentative existing) {
        String type = blankToNull(request.getIdProofType());
        String number = blankToNull(request.getIdProofNumber());
        if (type == null) return null;
        if (number == null) return "Enter the ID proof number";
        if (AADHAAR.equals(type)) {
            boolean keepsMasked = isMasked(number) && existing != null && AADHAAR.equals(existing.getIdProofType())
                    && number.equals(existing.getIdProofNumber());
            if (!keepsMasked && !number.replaceAll("\\s", "").matches("\\d{12}")) {
                return "Enter a valid 12-digit Aadhaar number";
            }
        }
        return null;
    }

    static String maskAadhaar(String number) {
        String digits = number.replaceAll("\\D", "");
        String last4 = digits.length() >= 4 ? digits.substring(digits.length() - 4) : digits;
        return "XXXXXXXX" + last4;
    }

    static boolean isMasked(String number) {
        return number != null && number.toUpperCase().startsWith("XXXX");
    }

    private boolean mobileTaken(Long reelerId, String mobile, Long exceptId) {
        return repository.findByReelerIdAndActiveOrderByReelerRepresentativeIdAsc(reelerId, true).stream()
                .anyMatch(x -> mobile != null && mobile.equals(x.getMobileNumber()) && !Objects.equals(x.getReelerRepresentativeId(), exceptId));
    }

    private Map<Long, String> relationshipNames(List<ReelerRepresentative> rows) {
        List<Long> ids = rows.stream().map(ReelerRepresentative::getRelationshipId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> m = new HashMap<>();
        if (ids.isEmpty()) return m;
        try {
            for (Object[] o : repository.relationshipNames(ids)) {
                if (o[0] != null) m.put(((Number) o[0]).longValue(), o[1] == null ? null : o[1].toString());
            }
        } catch (RuntimeException e) {
            log.warn("Relationship names could not be loaded for reeler representatives", e);
        }
        return m;
    }

    private static ReelerRepresentativeResponse toResponse(ReelerRepresentative r, Map<Long, String> relationNames) {
        return ReelerRepresentativeResponse.builder()
                .reelerRepresentativeId(r.getReelerRepresentativeId())
                .reelerId(r.getReelerId())
                .name(r.getName())
                .mobileNumber(r.getMobileNumber())
                .address(r.getAddress())
                .relationshipId(r.getRelationshipId())
                .relationshipName(r.getRelationshipId() == null ? null : relationNames.get(r.getRelationshipId()))
                .idProofType(r.getIdProofType())
                .idProofNumber(r.getIdProofNumber())
                .error(false)
                .build();
    }

    private static ReelerRepresentativeResponse error(String message) {
        return ReelerRepresentativeResponse.builder().error(true).error_description(message).build();
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
