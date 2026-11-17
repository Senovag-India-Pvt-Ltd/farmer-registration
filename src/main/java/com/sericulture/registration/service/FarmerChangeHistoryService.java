package com.sericulture.registration.service;

import com.sericulture.registration.model.entity.Farmer;
import com.sericulture.registration.model.entity.FarmerChangeHistory;
import com.sericulture.registration.repository.FarmerChangeHistoryRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Keeps the original FRUITS values of a farmer registered through Seva Sindhu and records who changed which detail
 * later, so the imported information is never lost when an authorised user corrects it.
 */
@Service
public class FarmerChangeHistoryService {

    /** Recorded as changed, never with its value. */
    private static final String MASKED = "Aadhaar number";

    private final FarmerChangeHistoryRepository repository;

    public FarmerChangeHistoryService(FarmerChangeHistoryRepository repository) {
        this.repository = repository;
    }

    /** The audited details of a farmer, by display label. */
    public Map<String, String> snapshot(Farmer f) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("FRUITS ID", s(f.getFruitsId()));
        m.put("First name", s(f.getFirstName()));
        m.put("Middle name", s(f.getMiddleName()));
        m.put("Last name", s(f.getLastName()));
        m.put("Name (Kannada)", s(f.getNameKan()));
        m.put("Father name", s(f.getFatherName()));
        m.put("Father name (Kannada)", s(f.getFatherNameKan()));
        m.put("Date of birth", f.getDob() == null ? null : new SimpleDateFormat("yyyy-MM-dd").format(f.getDob()));
        m.put("Gender", s(f.getGenderId()));
        m.put("Caste", s(f.getCasteId()));
        m.put("Mobile number", s(f.getMobileNumber()));
        m.put("Email", s(f.getEmail()));
        m.put(MASKED, s(f.getAadhaarNumber()));
        m.put("Total land holding", s(f.getTotalLandHolding()));
        m.put("Passbook number", s(f.getPassbookNumber()));
        m.put("Farmer type", s(f.getFarmerTypeId()));
        m.put("Minority", s(f.getMinority()));
        m.put("RD number", s(f.getRdNumber()));
        m.put("Caste status", s(f.getCasteStatus()));
        m.put("Gender status", s(f.getGenderStatus()));
        return m;
    }

    /** Original values imported from FRUITS at Seva Sindhu registration. */
    public void recordImport(Farmer farmer) {
        if (farmer == null) return;
        Date now = new Date();
        List<FarmerChangeHistory> rows = new ArrayList<>();
        snapshot(farmer).forEach((field, value) -> {
            if (value != null) rows.add(row(farmer.getFarmerId(), FarmerChangeHistory.TYPE_FRUITS_IMPORT, field, null, mask(field, value), now));
        });
        repository.saveAll(rows);
    }

    /** Every audited detail that differs between before and after. */
    public void recordEdit(Long farmerId, Map<String, String> before, Map<String, String> after) {
        Date now = new Date();
        List<FarmerChangeHistory> rows = new ArrayList<>();
        for (Map.Entry<String, String> e : after.entrySet()) {
            String old = before.get(e.getKey());
            if (!Objects.equals(old, e.getValue())) {
                rows.add(row(farmerId, FarmerChangeHistory.TYPE_EDIT, e.getKey(), mask(e.getKey(), old), mask(e.getKey(), e.getValue()), now));
            }
        }
        if (!rows.isEmpty()) repository.saveAll(rows);
    }

    public List<FarmerChangeHistory> history(Long farmerId) {
        return repository.findByFarmerIdOrderByChangedDateDescIdDesc(farmerId);
    }

    private static FarmerChangeHistory row(Long farmerId, String type, String field, String oldValue, String newValue, Date when) {
        FarmerChangeHistory h = new FarmerChangeHistory();
        h.setFarmerId(farmerId);
        h.setChangeType(type);
        h.setFieldName(field);
        h.setOldValue(cut(oldValue));
        h.setNewValue(cut(newValue));
        h.setChangedBy(currentUser());
        h.setChangedDate(when);
        return h;
    }

    private static String mask(String field, String value) {
        if (value == null) return null;
        if (!MASKED.equals(field)) return value;
        return value.length() <= 4 ? "XXXX" : "XXXXXXXX" + value.substring(value.length() - 4);
    }

    static String currentUser() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a == null || a.getName() == null ? "SYSTEM" : a.getName();
    }

    private static String cut(String v) {
        return v == null ? null : (v.length() > 500 ? v.substring(0, 500) : v);
    }

    private static String s(Object o) {
        if (o == null) return null;
        String v = o.toString().trim();
        return v.isEmpty() ? null : v;
    }
}
