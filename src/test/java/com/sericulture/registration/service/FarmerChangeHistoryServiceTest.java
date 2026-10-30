package com.sericulture.registration.service;

import com.sericulture.registration.model.entity.Farmer;
import com.sericulture.registration.model.entity.FarmerChangeHistory;
import com.sericulture.registration.repository.FarmerChangeHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Audit of a Seva Sindhu farmer's details: original FRUITS values and later edits. */
@ExtendWith(MockitoExtension.class)
class FarmerChangeHistoryServiceTest {

    @Mock
    FarmerChangeHistoryRepository repository;

    static Farmer farmer() {
        Farmer f = new Farmer();
        f.setFarmerId(9L);
        f.setFruitsId("FID2206000080056");
        f.setFirstName("SIDDARAJU");
        f.setMobileNumber("9036441488");
        f.setAadhaarNumber("123456789012");
        return f;
    }

    @SuppressWarnings("unchecked")
    private List<FarmerChangeHistory> saved() {
        ArgumentCaptor<Iterable<FarmerChangeHistory>> c = ArgumentCaptor.forClass(Iterable.class);
        verify(repository).saveAll(c.capture());
        List<FarmerChangeHistory> out = new ArrayList<>();
        c.getValue().forEach(out::add);
        return out;
    }

    @Test
    void import_keepsTheOriginalValues_withAadhaarMasked() {
        new FarmerChangeHistoryService(repository).recordImport(farmer());
        List<FarmerChangeHistory> rows = saved();
        assertEquals(4, rows.size(), "only details that have a value");
        assertTrue(rows.stream().allMatch(r -> FarmerChangeHistory.TYPE_FRUITS_IMPORT.equals(r.getChangeType()) && r.getOldValue() == null));
        FarmerChangeHistory aadhaar = rows.stream().filter(r -> r.getFieldName().equals("Aadhaar number")).findFirst().orElseThrow();
        assertEquals("XXXXXXXX9012", aadhaar.getNewValue());
        assertEquals("SYSTEM", aadhaar.getChangedBy());
    }

    @Test
    void edit_recordsOnlyChangedDetails_withOldAndNewValue() {
        FarmerChangeHistoryService s = new FarmerChangeHistoryService(repository);
        Farmer f = farmer();
        var before = s.snapshot(f);
        f.setMobileNumber("9876543210");
        f.setLastName("S R");
        s.recordEdit(9L, before, s.snapshot(f));
        List<FarmerChangeHistory> rows = saved();
        assertEquals(2, rows.size());
        FarmerChangeHistory mobile = rows.stream().filter(r -> r.getFieldName().equals("Mobile number")).findFirst().orElseThrow();
        assertEquals("9036441488", mobile.getOldValue());
        assertEquals("9876543210", mobile.getNewValue());
        assertEquals(FarmerChangeHistory.TYPE_EDIT, mobile.getChangeType());
    }

    @Test
    void edit_withoutChanges_writesNothing() {
        FarmerChangeHistoryService s = new FarmerChangeHistoryService(repository);
        var snap = s.snapshot(farmer());
        s.recordEdit(9L, snap, s.snapshot(farmer()));
        verifyNoInteractions(repository);
    }
}
