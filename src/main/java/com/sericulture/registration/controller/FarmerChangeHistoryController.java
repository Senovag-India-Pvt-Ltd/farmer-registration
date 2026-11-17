package com.sericulture.registration.controller;

import com.sericulture.registration.model.entity.FarmerChangeHistory;
import com.sericulture.registration.service.FarmerChangeHistoryService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Change history of a farmer's details (original FRUITS values of a Seva Sindhu registration and later edits).
 * Not under /v1/dashboard, so it needs a logged-in department user.
 */
@RestController
@RequestMapping("/v1/farmer-change-history")
public class FarmerChangeHistoryController {

    private final FarmerChangeHistoryService service;

    public FarmerChangeHistoryController(FarmerChangeHistoryService service) {
        this.service = service;
    }

    @GetMapping("/{farmerId}")
    public ResponseEntity<?> history(@PathVariable Long farmerId) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (FarmerChangeHistory h : service.history(farmerId)) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("changeType", h.getChangeType());
            m.put("fieldName", h.getFieldName());
            m.put("oldValue", h.getOldValue());
            m.put("newValue", h.getNewValue());
            m.put("changedBy", h.getChangedBy());
            m.put("changedDate", h.getChangedDate());
            rows.add(m);
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(Map.of("content", rows));
    }
}
