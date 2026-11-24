package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmerRepresentative.FarmerRepresentativeRequest;
import com.sericulture.registration.model.api.farmerRepresentative.FarmerRepresentativeResponse;
import com.sericulture.registration.model.entity.FarmerRepresentative;
import com.sericulture.registration.repository.FarmerRepresentativeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/** SR-005: authorised agents of a farmer - maximum, duplicates, Aadhaar masking, authorised period, search. */
@ExtendWith(MockitoExtension.class)
class FarmerRepresentativeServiceTest {

    @Mock
    FarmerRepresentativeRepository repository;

    static FarmerRepresentativeRequest request(String mobile) {
        FarmerRepresentativeRequest r = new FarmerRepresentativeRequest();
        r.setFarmerId(10L);
        r.setName(" Ramesh K ");
        r.setMobileNumber(mobile);
        r.setAddress("Ramanagara");
        return r;
    }

    static FarmerRepresentative existing(long id, String mobile) {
        FarmerRepresentative e = new FarmerRepresentative();
        e.setFarmerRepresentativeId(id);
        e.setFarmerId(10L);
        e.setName("Existing");
        e.setMobileNumber(mobile);
        e.setAddress("Addr");
        e.setAuthorisedFrom(LocalDate.of(2026, 1, 1));
        e.setActive(true);
        return e;
    }

    private FarmerRepresentativeService service() {
        return new FarmerRepresentativeService(repository, 3);
    }

    private void farmerIsActive() {
        when(repository.lockActiveFarmer(10L)).thenReturn(List.of(10L));
    }

    private void savesWithId() {
        when(repository.save(any())).thenAnswer(i -> { FarmerRepresentative r = i.getArgument(0); if (r.getFarmerRepresentativeId() == null) r.setFarmerRepresentativeId(5L); return r; });
    }

    @Test
    void add_belowTheMaximum_savesTrimmedAndDefaultsFromToToday() {
        farmerIsActive();
        when(repository.countByFarmerIdAndActive(10L, true)).thenReturn(2L);
        when(repository.findByFarmerIdAndActiveOrderByFarmerRepresentativeIdAsc(10L, true)).thenReturn(new ArrayList<>());
        savesWithId();

        FarmerRepresentativeResponse res = service().add(request("9876543210"));

        assertFalse(res.getError());
        assertEquals(5L, res.getFarmerRepresentativeId());
        assertEquals("Ramesh K", res.getName());
        assertNotNull(res.getAuthorisedFrom());
        assertNull(res.getAuthorisedTo());
        assertTrue(res.getAuthorisedToday());
    }

    @Test
    void add_atTheMaximum_isRefused() {
        farmerIsActive();
        when(repository.countByFarmerIdAndActive(10L, true)).thenReturn(3L);

        FarmerRepresentativeResponse res = service().add(request("9876543210"));

        assertTrue(res.getError());
        assertEquals("Maximum 3 agents allowed for a farmer", res.getError_description());
        verify(repository, never()).save(any());
    }

    @Test
    void add_unknownOrInactiveFarmer_isRefused() {
        when(repository.lockActiveFarmer(10L)).thenReturn(List.of());

        FarmerRepresentativeResponse res = service().add(request("9876543210"));

        assertTrue(res.getError());
        assertEquals("Farmer not found or inactive", res.getError_description());
        verify(repository, never()).save(any());
    }

    @Test
    void add_sameMobileTwiceForAFarmer_isRefused() {
        farmerIsActive();
        when(repository.countByFarmerIdAndActive(10L, true)).thenReturn(1L);
        when(repository.findByFarmerIdAndActiveOrderByFarmerRepresentativeIdAsc(10L, true)).thenReturn(List.of(existing(1, "9876543210")));

        FarmerRepresentativeResponse res = service().add(request("9876543210"));

        assertTrue(res.getError());
        verify(repository, never()).save(any());
    }

    @Test
    void add_aadhaarIsStoredMasked() {
        farmerIsActive();
        when(repository.countByFarmerIdAndActive(10L, true)).thenReturn(0L);
        when(repository.findByFarmerIdAndActiveOrderByFarmerRepresentativeIdAsc(10L, true)).thenReturn(new ArrayList<>());
        savesWithId();
        FarmerRepresentativeRequest req = request("9876543210");
        req.setIdProofType("AADHAAR");
        req.setIdProofNumber("1234 5678 9012");

        service().add(req);

        ArgumentCaptor<FarmerRepresentative> saved = ArgumentCaptor.forClass(FarmerRepresentative.class);
        verify(repository).save(saved.capture());
        assertEquals("XXXXXXXX9012", saved.getValue().getIdProofNumber());
    }

    @Test
    void add_invalidAadhaar_isRefused() {
        farmerIsActive();
        when(repository.countByFarmerIdAndActive(10L, true)).thenReturn(0L);
        when(repository.findByFarmerIdAndActiveOrderByFarmerRepresentativeIdAsc(10L, true)).thenReturn(new ArrayList<>());
        FarmerRepresentativeRequest req = request("9876543210");
        req.setIdProofType("AADHAAR");
        req.setIdProofNumber("12345");

        FarmerRepresentativeResponse res = service().add(req);

        assertTrue(res.getError());
        assertEquals("Enter a valid 12-digit Aadhaar number", res.getError_description());
    }

    @Test
    void add_tillDateBeforeFromDate_isRefused() {
        farmerIsActive();
        when(repository.countByFarmerIdAndActive(10L, true)).thenReturn(0L);
        when(repository.findByFarmerIdAndActiveOrderByFarmerRepresentativeIdAsc(10L, true)).thenReturn(new ArrayList<>());
        FarmerRepresentativeRequest req = request("9876543210");
        req.setAuthorisedFrom(LocalDate.of(2026, 12, 1));
        req.setAuthorisedTo(LocalDate.of(2026, 11, 1));

        FarmerRepresentativeResponse res = service().add(req);

        assertTrue(res.getError());
        verify(repository, never()).save(any());
    }

    @Test
    void edit_agentOfAnotherFarmer_isRefused() {
        FarmerRepresentative other = existing(7, "9000000000");
        other.setFarmerId(99L);
        when(repository.findByFarmerRepresentativeIdAndActive(7L, true)).thenReturn(Optional.of(other));
        FarmerRepresentativeRequest req = request("9876543210");
        req.setFarmerRepresentativeId(7L);

        FarmerRepresentativeResponse res = service().edit(req);

        assertTrue(res.getError());
        assertEquals("Agent not found", res.getError_description());
    }

    @Test
    void edit_keepsStoredMaskedAadhaarAndFromDate() {
        FarmerRepresentative e = existing(7, "9876543210");
        e.setIdProofType("AADHAAR");
        e.setIdProofNumber("XXXXXXXX9012");
        when(repository.findByFarmerRepresentativeIdAndActive(7L, true)).thenReturn(Optional.of(e));
        when(repository.findByFarmerIdAndActiveOrderByFarmerRepresentativeIdAsc(10L, true)).thenReturn(List.of(e));
        savesWithId();
        FarmerRepresentativeRequest req = request("9876543210");
        req.setFarmerRepresentativeId(7L);
        req.setIdProofType("AADHAAR");
        req.setIdProofNumber("XXXXXXXX9012");

        FarmerRepresentativeResponse res = service().edit(req);

        assertFalse(res.getError());
        assertEquals("XXXXXXXX9012", res.getIdProofNumber());
        assertEquals(LocalDate.of(2026, 1, 1), res.getAuthorisedFrom());
    }

    @Test
    void delete_deactivates() {
        FarmerRepresentative e = existing(7, "9876543210");
        when(repository.findByFarmerRepresentativeIdAndActive(7L, true)).thenReturn(Optional.of(e));

        FarmerRepresentativeResponse res = service().delete(7L);

        assertFalse(res.getError());
        assertFalse(e.getActive());
        verify(repository).save(e);
    }

    @Test
    void isAuthorisedOn_respectsThePeriod() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 12, 31);
        assertTrue(FarmerRepresentativeService.isAuthorisedOn(from, to, LocalDate.of(2026, 6, 1)));
        assertTrue(FarmerRepresentativeService.isAuthorisedOn(from, to, to));
        assertFalse(FarmerRepresentativeService.isAuthorisedOn(from, to, LocalDate.of(2027, 1, 1)));
        assertFalse(FarmerRepresentativeService.isAuthorisedOn(from, to, LocalDate.of(2025, 12, 31)));
        assertTrue(FarmerRepresentativeService.isAuthorisedOn(from, null, LocalDate.of(2030, 1, 1)));
    }

    @Test
    void search_byMobile_passesTenDigitsAndNoName() {
        when(repository.search(anyString(), anyString())).thenReturn(new ArrayList<>());

        Map<String, Object> out = service().search("+91 98765-43210");

        assertEquals(false, out.get("error"));
        verify(repository).search("9876543210", "");
    }

    @Test
    void search_byName_needsThreeLetters() {
        Map<String, Object> out = service().search("ra");

        assertEquals(true, out.get("error"));
        verify(repository, never()).search(anyString(), anyString());
    }
}
