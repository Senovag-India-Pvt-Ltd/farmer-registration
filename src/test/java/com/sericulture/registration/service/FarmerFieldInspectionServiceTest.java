package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmerFieldInspection.FarmerFieldInspectionRequest;
import com.sericulture.registration.model.entity.FarmerFieldInspection;
import com.sericulture.registration.repository.FarmerFieldInspectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** SR-007: field inspections stay with the correct farmer's land record; coordinates and photo checks. */
@ExtendWith(MockitoExtension.class)
class FarmerFieldInspectionServiceTest {

    @Mock
    FarmerFieldInspectionRepository repository;
    @Mock
    S3Service s3Service;

    private FarmerFieldInspectionService service() {
        return new FarmerFieldInspectionService(repository, s3Service);
    }

    static FarmerFieldInspectionRequest request() {
        FarmerFieldInspectionRequest r = new FarmerFieldInspectionRequest();
        r.setFarmerId(10L);
        r.setFarmerLandDetailsId(100L);
        r.setSiteType("FARM");
        r.setInspectionDate(LocalDate.of(2026, 11, 20));
        r.setLatitude(new BigDecimal("13.3045123"));
        r.setLongitude(new BigDecimal("76.9123456"));
        r.setGpsAccuracyM(new BigDecimal("12.345"));
        r.setGpsSource("DEVICE");
        r.setObservations("  Garden healthy  ");
        return r;
    }

    private static List<Object[]> rows(Object[]... r) {
        List<Object[]> l = new ArrayList<>();
        for (Object[] o : r) l.add(o);
        return l;
    }

    private void farmer10WithLand100(String landLat, String landLng) {
        when(repository.activeFarmer(10L)).thenReturn(List.of(10L));
        when(repository.activeLand(100L)).thenReturn(rows(new Object[]{10L, landLat, landLng}));
    }

    private void savesWithId() {
        when(repository.save(any())).thenAnswer(i -> { FarmerFieldInspection f = i.getArgument(0); f.setFarmerFieldInspectionId(1L); return f; });
    }

    @Test
    void add_landOfTheSameFarmer_isSavedOnThatLandRecord() {
        farmer10WithLand100(null, null);
        savesWithId();

        Map<String, Object> out = service().add(request(), null);

        assertEquals(false, out.get("error"));
        ArgumentCaptor<FarmerFieldInspection> c = ArgumentCaptor.forClass(FarmerFieldInspection.class);
        verify(repository).save(c.capture());
        FarmerFieldInspection saved = c.getValue();
        assertEquals(10L, saved.getFarmerId());
        assertEquals(100L, saved.getFarmerLandDetailsId());
        assertEquals("FARM", saved.getSiteType());
        assertEquals(new BigDecimal("13.304512"), saved.getLatitude());
        assertEquals(new BigDecimal("76.912346"), saved.getLongitude());
        assertEquals(new BigDecimal("12.35"), saved.getGpsAccuracyM());
        assertEquals("Garden healthy", saved.getObservations());
        assertNull(saved.getPhotoS3Key());
        verifyNoInteractions(s3Service);
    }

    @Test
    void add_landOfAnotherFarmer_isRefused() {
        when(repository.activeFarmer(10L)).thenReturn(List.of(10L));
        when(repository.activeLand(100L)).thenReturn(rows(new Object[]{99L, null, null}));

        Map<String, Object> out = service().add(request(), null);

        assertEquals(true, out.get("error"));
        assertEquals("This land record does not belong to the selected farmer", out.get("error_description"));
        verify(repository, never()).save(any());
    }

    @Test
    void add_inactiveFarmer_isRefused() {
        when(repository.activeFarmer(10L)).thenReturn(List.of());

        assertEquals(true, service().add(request(), null).get("error"));
        verify(repository, never()).save(any());
    }

    @Test
    void add_missingLand_isRefused() {
        when(repository.activeFarmer(10L)).thenReturn(List.of(10L));
        when(repository.activeLand(100L)).thenReturn(new ArrayList<>());

        assertEquals(true, service().add(request(), null).get("error"));
    }

    @Test
    void add_outsideKarnataka_isRefused() {
        farmer10WithLand100(null, null);
        FarmerFieldInspectionRequest r = request();
        r.setLatitude(new BigDecimal("28.6139")); // Delhi
        r.setLongitude(new BigDecimal("77.2090"));

        Map<String, Object> out = service().add(r, null);

        assertEquals(true, out.get("error"));
        assertTrue(out.get("error_description").toString().contains("outside Karnataka"));
        verify(repository, never()).save(any());
    }

    @Test
    void add_futureDate_isRefused() {
        farmer10WithLand100(null, null);
        FarmerFieldInspectionRequest r = request();
        r.setInspectionDate(LocalDate.now().plusDays(3));

        assertEquals(true, service().add(r, null).get("error"));
    }

    @Test
    void add_withPhoto_uploadsAndKeepsTheKey() throws Exception {
        farmer10WithLand100(null, null);
        savesWithId();
        MockMultipartFile photo = new MockMultipartFile("photo", "farm.jpg", "image/jpeg", new byte[]{1, 2, 3});

        Map<String, Object> out = service().add(request(), photo);

        assertEquals(true, out.get("hasPhoto"));
        verify(s3Service).uploadFile(startsWith("farmer-field-inspection/10/"), eq(3L), eq("image/jpeg"), any(InputStream.class));
    }

    @Test
    void add_photoNotAnImage_isRefusedBeforeUpload() {
        farmer10WithLand100(null, null);
        MockMultipartFile pdf = new MockMultipartFile("photo", "a.pdf", "application/pdf", new byte[]{1});

        assertEquals(true, service().add(request(), pdf).get("error"));
        verifyNoInteractions(s3Service);
        verify(repository, never()).save(any());
    }

    @Test
    void add_photoUploadFails_nothingIsSaved() throws Exception {
        farmer10WithLand100(null, null);
        doThrow(new RuntimeException("S3 down")).when(s3Service).uploadFile(anyString(), anyLong(), anyString(), any());
        MockMultipartFile photo = new MockMultipartFile("photo", "farm.png", "image/png", new byte[]{1});

        Map<String, Object> out = service().add(request(), photo);

        assertEquals(true, out.get("error"));
        verify(repository, never()).save(any());
    }

    @Test
    void add_reportsDistanceFromTheLandsRecordedGps() {
        farmer10WithLand100("13.304512", "76.912346");
        savesWithId();

        Map<String, Object> out = service().add(request(), null);

        assertEquals(0L, out.get("distanceFromLandGpsM"));
    }

    @Test
    void getByFarmer_groupsInspectionsUnderTheirOwnLand() {
        when(repository.lands(10L)).thenReturn(rows(
                new Object[]{100L, "22", "5", 1, 10, "0", "1", "300", "100", null, null, "Hebbur", "GOVINDAIAH"},
                new Object[]{101L, "33", "2", 0, 20, "0", "0.5", null, null, "13.3", "76.9", "Hebbur", "GOVINDAIAH"}));
        FarmerFieldInspection a = new FarmerFieldInspection();
        a.setFarmerFieldInspectionId(1L); a.setFarmerLandDetailsId(101L); a.setSiteType("FARM");
        a.setLatitude(new BigDecimal("13.300000")); a.setLongitude(new BigDecimal("76.900000"));
        FarmerFieldInspection b = new FarmerFieldInspection();
        b.setFarmerFieldInspectionId(2L); b.setFarmerLandDetailsId(100L); b.setSiteType("REARING_HOUSE");
        b.setLatitude(new BigDecimal("13.310000")); b.setLongitude(new BigDecimal("76.910000"));
        FarmerFieldInspection orphan = new FarmerFieldInspection();
        orphan.setFarmerFieldInspectionId(3L); orphan.setFarmerLandDetailsId(999L);
        when(repository.findByFarmerIdAndActiveOrderByInspectionDateDescFarmerFieldInspectionIdDesc(10L, true)).thenReturn(List.of(a, b, orphan));

        Map<String, Object> out = service().getByFarmer(10L);

        assertEquals(2, out.get("totalInspections"));
        @SuppressWarnings("unchecked") List<Map<String, Object>> lands = (List<Map<String, Object>>) out.get("lands");
        @SuppressWarnings("unchecked") List<Map<String, Object>> land100 = (List<Map<String, Object>>) lands.get(0).get("inspections");
        @SuppressWarnings("unchecked") List<Map<String, Object>> land101 = (List<Map<String, Object>>) lands.get(1).get("inspections");
        assertEquals(1, land100.size());
        assertEquals("REARING_HOUSE", land100.get(0).get("siteType"));
        assertEquals(1, land101.size());
        assertEquals(0L, land101.get(0).get("distanceFromLandGpsM"));
    }

    @Test
    void parseLatLng_ignoresJunkInTheFreeTextLandColumns() {
        assertNull(FarmerFieldInspectionService.parseLatLng("abc", "76.9"));
        assertNull(FarmerFieldInspectionService.parseLatLng("0", "0"));
        assertNull(FarmerFieldInspectionService.parseLatLng(null, "76.9"));
        assertNotNull(FarmerFieldInspectionService.parseLatLng(" 13.3 ", "76.9"));
    }

    @Test
    void distanceMetres_isRoughlyRight() {
        long d = FarmerFieldInspectionService.distanceMetres(new BigDecimal("13.0"), new BigDecimal("77.0"),
                new BigDecimal("13.01"), new BigDecimal("77.0"));
        assertTrue(d > 1100 && d < 1120, "about 1.11 km, was " + d);
    }
}
