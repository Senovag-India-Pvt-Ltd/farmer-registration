package com.sericulture.registration.service;

import com.sericulture.registration.model.api.reelerRepresentative.ReelerRepresentativeRequest;
import com.sericulture.registration.model.api.reelerRepresentative.ReelerRepresentativeResponse;
import com.sericulture.registration.model.entity.ReelerRepresentative;
import com.sericulture.registration.repository.ReelerRepresentativeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** SR-013: reeler representatives, configurable maximum enforced by the backend. */
@ExtendWith(MockitoExtension.class)
class ReelerRepresentativeServiceTest {

    @Mock
    ReelerRepresentativeRepository repository;

    static ReelerRepresentativeRequest request(String mobile) {
        ReelerRepresentativeRequest r = new ReelerRepresentativeRequest();
        r.setReelerId(10L);
        r.setName(" Ramesh K ");
        r.setMobileNumber(mobile);
        r.setAddress("Ramanagara");
        return r;
    }

    static ReelerRepresentative existing(long id, String mobile) {
        ReelerRepresentative e = new ReelerRepresentative();
        e.setReelerRepresentativeId(id);
        e.setReelerId(10L);
        e.setName("Existing");
        e.setMobileNumber(mobile);
        e.setAddress("Addr");
        e.setActive(true);
        return e;
    }

    private void reelerIsActive() {
        when(repository.lockActiveReeler(10L)).thenReturn(List.of(10L));
    }

    @Test
    void add_belowTheMaximum_saves() {
        ReelerRepresentativeService service = new ReelerRepresentativeService(repository, 3);
        reelerIsActive();
        when(repository.countByReelerIdAndActive(10L, true)).thenReturn(2L);
        when(repository.findByReelerIdAndActiveOrderByReelerRepresentativeIdAsc(10L, true)).thenReturn(new ArrayList<>());
        when(repository.save(any())).thenAnswer(i -> { ReelerRepresentative r = i.getArgument(0); r.setReelerRepresentativeId(5L); return r; });

        ReelerRepresentativeResponse res = service.add(request("9876543210"));

        assertFalse(res.getError());
        assertEquals(5L, res.getReelerRepresentativeId());
        assertEquals("Ramesh K", res.getName());
    }

    @Test
    void add_atTheMaximum_isRefused_andNothingSaved() {
        ReelerRepresentativeService service = new ReelerRepresentativeService(repository, 3);
        reelerIsActive();
        when(repository.countByReelerIdAndActive(10L, true)).thenReturn(3L);

        ReelerRepresentativeResponse res = service.add(request("9876543210"));

        assertTrue(res.getError());
        assertEquals("Maximum 3 representatives allowed for a reeler", res.getError_description());
        verify(repository, never()).save(any());
    }

    @Test
    void add_limitFollowsConfiguration_twoPerSrs() {
        ReelerRepresentativeService service = new ReelerRepresentativeService(repository, 2);
        reelerIsActive();
        when(repository.countByReelerIdAndActive(10L, true)).thenReturn(2L);

        ReelerRepresentativeResponse res = service.add(request("9876543210"));

        assertTrue(res.getError());
        assertEquals("Maximum 2 representatives allowed for a reeler", res.getError_description());
        assertEquals(2, service.getMaxRepresentatives());
    }

    @Test
    void add_unknownOrInactiveReeler_isRefused() {
        ReelerRepresentativeService service = new ReelerRepresentativeService(repository, 3);
        when(repository.lockActiveReeler(10L)).thenReturn(List.of());

        ReelerRepresentativeResponse res = service.add(request("9876543210"));

        assertTrue(res.getError());
        assertEquals("Reeler not found or inactive", res.getError_description());
        verify(repository, never()).save(any());
    }

    @Test
    void add_sameMobileTwiceForOneReeler_isRefused() {
        ReelerRepresentativeService service = new ReelerRepresentativeService(repository, 3);
        reelerIsActive();
        when(repository.countByReelerIdAndActive(10L, true)).thenReturn(1L);
        when(repository.findByReelerIdAndActiveOrderByReelerRepresentativeIdAsc(10L, true)).thenReturn(List.of(existing(1L, "9876543210")));

        ReelerRepresentativeResponse res = service.add(request("9876543210"));

        assertTrue(res.getError());
        verify(repository, never()).save(any());
    }

    @Test
    void add_aadhaar_isStoredMasked() {
        ReelerRepresentativeService service = new ReelerRepresentativeService(repository, 3);
        reelerIsActive();
        when(repository.countByReelerIdAndActive(10L, true)).thenReturn(0L);
        when(repository.findByReelerIdAndActiveOrderByReelerRepresentativeIdAsc(10L, true)).thenReturn(new ArrayList<>());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        ReelerRepresentativeRequest req = request("9876543210");
        req.setIdProofType("AADHAAR");
        req.setIdProofNumber("1234 5678 9012");

        service.add(req);

        ArgumentCaptor<ReelerRepresentative> c = ArgumentCaptor.forClass(ReelerRepresentative.class);
        verify(repository).save(c.capture());
        assertEquals("XXXXXXXX9012", c.getValue().getIdProofNumber());
    }

    @Test
    void add_invalidAadhaar_isRefused() {
        ReelerRepresentativeService service = new ReelerRepresentativeService(repository, 3);
        reelerIsActive();
        when(repository.countByReelerIdAndActive(10L, true)).thenReturn(0L);
        when(repository.findByReelerIdAndActiveOrderByReelerRepresentativeIdAsc(10L, true)).thenReturn(new ArrayList<>());
        ReelerRepresentativeRequest req = request("9876543210");
        req.setIdProofType("AADHAAR");
        req.setIdProofNumber("12345");

        ReelerRepresentativeResponse res = service.add(req);

        assertTrue(res.getError());
        assertEquals("Enter a valid 12-digit Aadhaar number", res.getError_description());
    }

    @Test
    void edit_keepsMaskedAadhaar_whenSentBackUnchanged() {
        ReelerRepresentativeService service = new ReelerRepresentativeService(repository, 3);
        ReelerRepresentative row = existing(1L, "9876543210");
        row.setIdProofType("AADHAAR");
        row.setIdProofNumber("XXXXXXXX9012");
        when(repository.findByReelerRepresentativeIdAndActive(1L, true)).thenReturn(Optional.of(row));
        when(repository.findByReelerIdAndActiveOrderByReelerRepresentativeIdAsc(10L, true)).thenReturn(List.of(row));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        ReelerRepresentativeRequest req = request("9876543210");
        req.setReelerRepresentativeId(1L);
        req.setName("Ramesh Kumar");
        req.setIdProofType("AADHAAR");
        req.setIdProofNumber("XXXXXXXX9012");

        ReelerRepresentativeResponse res = service.edit(req);

        assertFalse(res.getError());
        assertEquals("Ramesh Kumar", res.getName());
        assertEquals("XXXXXXXX9012", res.getIdProofNumber());
    }

    @Test
    void edit_ofAnotherReelersRepresentative_isRefused() {
        ReelerRepresentativeService service = new ReelerRepresentativeService(repository, 3);
        ReelerRepresentative row = existing(1L, "9876543210");
        row.setReelerId(99L);
        when(repository.findByReelerRepresentativeIdAndActive(1L, true)).thenReturn(Optional.of(row));
        ReelerRepresentativeRequest req = request("9876543210");
        req.setReelerRepresentativeId(1L);

        ReelerRepresentativeResponse res = service.edit(req);

        assertTrue(res.getError());
        verify(repository, never()).save(any());
    }

    @Test
    void delete_deactivates_soAnotherCanBeAdded() {
        ReelerRepresentativeService service = new ReelerRepresentativeService(repository, 3);
        ReelerRepresentative row = existing(1L, "9876543210");
        when(repository.findByReelerRepresentativeIdAndActive(1L, true)).thenReturn(Optional.of(row));

        ReelerRepresentativeResponse res = service.delete(1L);

        assertFalse(res.getError());
        assertFalse(row.getActive());
        verify(repository).save(row);
    }

    @Test
    void configuredMaximum_isAtLeastOne() {
        assertEquals(1, new ReelerRepresentativeService(repository, 0).getMaxRepresentatives());
    }
}
