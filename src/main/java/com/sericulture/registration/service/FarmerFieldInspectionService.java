package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmerFieldInspection.FarmerFieldInspectionRequest;
import com.sericulture.registration.model.entity.FarmerFieldInspection;
import com.sericulture.registration.repository.FarmerFieldInspectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * SR-007: GPS-tagged field inspections of a farmer's farm / rearing house. Every inspection is tied to one active
 * land record of the same farmer, so the coordinates stay with the correct farm / rearing-house record. The land
 * record (including its own gps_lat / gps_lng) is never modified. An optional photo is stored in S3.
 */
@Service
@Slf4j
public class FarmerFieldInspectionService {

    /** Generous Karnataka bounding box (state spans ~11.5-18.5 N, ~74.0-78.6 E). */
    static final BigDecimal MIN_LAT = new BigDecimal("11.0"), MAX_LAT = new BigDecimal("19.0");
    static final BigDecimal MIN_LNG = new BigDecimal("73.5"), MAX_LNG = new BigDecimal("79.0");
    static final long MAX_PHOTO_BYTES = 4L * 1024 * 1024;
    static final Map<String, String> PHOTO_TYPES = Map.of("image/jpeg", "jpg", "image/png", "png", "image/webp", "webp");
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private final FarmerFieldInspectionRepository repository;
    private final S3Service s3Service;

    public FarmerFieldInspectionService(FarmerFieldInspectionRepository repository, S3Service s3Service) {
        this.repository = repository;
        this.s3Service = s3Service;
    }

    @Transactional
    public Map<String, Object> add(FarmerFieldInspectionRequest r, MultipartFile photo) {
        if (repository.activeFarmer(r.getFarmerId()).isEmpty()) return error("Farmer not found or inactive");
        List<Object[]> land = repository.activeLand(r.getFarmerLandDetailsId());
        if (land.isEmpty()) return error("Land record not found or inactive");
        if (!Objects.equals(num(land.get(0)[0]), r.getFarmerId())) {
            return error("This land record does not belong to the selected farmer");
        }
        if (r.getInspectionDate().isAfter(LocalDate.now(IST))) return error("Inspection date cannot be in the future");
        String coordError = validateKarnataka(r.getLatitude(), r.getLongitude());
        if (coordError != null) return error(coordError);

        boolean hasPhoto = photo != null && !photo.isEmpty();
        String ext = null;
        if (hasPhoto) {
            ext = PHOTO_TYPES.get(photo.getContentType());
            if (ext == null) return error("Photo must be a JPG, PNG or WEBP image");
            if (photo.getSize() > MAX_PHOTO_BYTES) return error("Photo must be 4 MB or smaller");
        }

        FarmerFieldInspection i = new FarmerFieldInspection();
        i.setFarmerId(r.getFarmerId());
        i.setFarmerLandDetailsId(r.getFarmerLandDetailsId());
        i.setSiteType(r.getSiteType());
        i.setInspectionDate(r.getInspectionDate());
        i.setLatitude(r.getLatitude().setScale(6, RoundingMode.HALF_UP));
        i.setLongitude(r.getLongitude().setScale(6, RoundingMode.HALF_UP));
        i.setGpsAccuracyM(r.getGpsAccuracyM() == null ? null : r.getGpsAccuracyM().setScale(2, RoundingMode.HALF_UP));
        i.setGpsSource(r.getGpsSource());
        i.setGpsCapturedAt(r.getGpsCapturedAt() == null ? null : new Date(r.getGpsCapturedAt()));
        i.setObservations(blankToNull(r.getObservations()));
        i.setInspectedBy(currentUser());
        i.setActive(true);

        if (hasPhoto) {
            String key = "farmer-field-inspection/" + r.getFarmerId() + "/" + UUID.randomUUID() + "." + ext;
            try {
                s3Service.uploadFile(key, photo.getSize(), photo.getContentType(), photo.getInputStream());
            } catch (Exception e) {
                log.error("SR-007: photo upload failed for farmer {}", r.getFarmerId(), e);
                return error("Photo could not be uploaded. Try again, or save the inspection without a photo.");
            }
            i.setPhotoS3Key(key);
            i.setPhotoContentType(photo.getContentType());
        }

        FarmerFieldInspection saved = repository.save(i);
        log.info("SR-007: field inspection {} saved for farmer {} land {} ({})", saved.getFarmerFieldInspectionId(),
                saved.getFarmerId(), saved.getFarmerLandDetailsId(), saved.getSiteType());
        Map<String, Object> out = view(saved, landLatLng(land.get(0)));
        out.put("error", false);
        return out;
    }

    /** The farmer's land records, each with its recorded GPS and its field inspections (latest first). */
    public Map<String, Object> getByFarmer(Long farmerId) {
        Map<Long, Map<String, Object>> lands = new LinkedHashMap<>();
        Map<Long, BigDecimal[]> landGps = new HashMap<>();
        for (Object[] o : repository.lands(farmerId)) {
            Long id = num(o[0]);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("farmerLandDetailsId", id);
            m.put("surveyNumber", str(o[1]));
            m.put("hissa", str(o[2]));
            m.put("acre", num(o[3]));
            m.put("gunta", num(o[4]));
            m.put("fGunta", str(o[5]));
            m.put("mulberryArea", str(o[6]));
            m.put("rearingHouseDetails", str(o[7]));
            m.put("rearingCapacityDlf", str(o[8]));
            m.put("gpsLat", str(o[9]));
            m.put("gpsLng", str(o[10]));
            m.put("villageName", str(o[11]));
            m.put("ownerName", str(o[12]));
            m.put("inspections", new ArrayList<Map<String, Object>>());
            lands.put(id, m);
            landGps.put(id, parseLatLng(o[9], o[10]));
        }
        int count = 0;
        for (FarmerFieldInspection i : repository.findByFarmerIdAndActiveOrderByInspectionDateDescFarmerFieldInspectionIdDesc(farmerId, true)) {
            Map<String, Object> land = lands.get(i.getFarmerLandDetailsId());
            if (land == null) continue; // land record was removed later: its inspections are not shown against another farm
            @SuppressWarnings("unchecked") List<Map<String, Object>> list = (List<Map<String, Object>>) land.get("inspections");
            list.add(view(i, landGps.get(i.getFarmerLandDetailsId())));
            count++;
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("farmerId", farmerId);
        out.put("lands", new ArrayList<>(lands.values()));
        out.put("totalInspections", count);
        return out;
    }

    @Transactional
    public Map<String, Object> delete(Long id) {
        FarmerFieldInspection i = id == null ? null : repository.findByFarmerFieldInspectionIdAndActive(id, true).orElse(null);
        if (i == null) return error("Inspection not found");
        i.setActive(false);
        repository.save(i);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("error", false);
        out.put("farmerFieldInspectionId", id);
        return out;
    }

    /** Photo bytes and content type, or null when the inspection has no photo. */
    public Object[] photo(Long id) throws Exception {
        FarmerFieldInspection i = repository.findByFarmerFieldInspectionIdAndActive(id, true).orElse(null);
        if (i == null || i.getPhotoS3Key() == null) return null;
        ByteArrayOutputStream bytes = s3Service.downloadFile(i.getPhotoS3Key());
        return new Object[]{bytes.toByteArray(), i.getPhotoContentType() == null ? "image/jpeg" : i.getPhotoContentType()};
    }

    // ------------------------------------------------------------------ helpers

    static String validateKarnataka(BigDecimal lat, BigDecimal lng) {
        if (lat == null || lng == null) return "Latitude and longitude are required";
        if (lat.compareTo(MIN_LAT) < 0 || lat.compareTo(MAX_LAT) > 0 || lng.compareTo(MIN_LNG) < 0 || lng.compareTo(MAX_LNG) > 0) {
            return "The location " + lat.toPlainString() + ", " + lng.toPlainString() + " is outside Karnataka. Check the coordinates.";
        }
        return null;
    }

    /** Great-circle distance in metres. */
    static long distanceMetres(BigDecimal lat1, BigDecimal lng1, BigDecimal lat2, BigDecimal lng2) {
        double r = 6_371_000d;
        double p1 = Math.toRadians(lat1.doubleValue()), p2 = Math.toRadians(lat2.doubleValue());
        double dp = p2 - p1, dl = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());
        double a = Math.sin(dp / 2) * Math.sin(dp / 2) + Math.cos(p1) * Math.cos(p2) * Math.sin(dl / 2) * Math.sin(dl / 2);
        return Math.round(2 * r * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }

    /** Land GPS columns are free text: null unless both parse to a valid coordinate. */
    static BigDecimal[] parseLatLng(Object lat, Object lng) {
        try {
            if (lat == null || lng == null) return null;
            BigDecimal a = new BigDecimal(lat.toString().trim()), b = new BigDecimal(lng.toString().trim());
            if (a.abs().compareTo(new BigDecimal("90")) > 0 || b.abs().compareTo(new BigDecimal("180")) > 0) return null;
            if (a.signum() == 0 && b.signum() == 0) return null;
            return new BigDecimal[]{a, b};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static BigDecimal[] landLatLng(Object[] landRow) {
        return parseLatLng(landRow[1], landRow[2]);
    }

    private static Map<String, Object> view(FarmerFieldInspection i, BigDecimal[] landGps) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("farmerFieldInspectionId", i.getFarmerFieldInspectionId());
        m.put("farmerId", i.getFarmerId());
        m.put("farmerLandDetailsId", i.getFarmerLandDetailsId());
        m.put("siteType", i.getSiteType());
        m.put("inspectionDate", i.getInspectionDate());
        m.put("latitude", i.getLatitude());
        m.put("longitude", i.getLongitude());
        m.put("gpsAccuracyM", i.getGpsAccuracyM());
        m.put("gpsSource", i.getGpsSource());
        m.put("gpsCapturedAt", i.getGpsCapturedAt());
        m.put("observations", i.getObservations());
        m.put("hasPhoto", i.getPhotoS3Key() != null);
        m.put("inspectedBy", i.getInspectedBy());
        m.put("distanceFromLandGpsM", landGps == null || i.getLatitude() == null ? null
                : distanceMetres(landGps[0], landGps[1], i.getLatitude(), i.getLongitude()));
        return m;
    }

    private static String currentUser() {
        try {
            Authentication a = SecurityContextHolder.getContext().getAuthentication();
            return a == null ? null : a.getName();
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static Map<String, Object> error(String message) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("error", true);
        m.put("error_description", message);
        return m;
    }

    private static Long num(Object o) {
        if (o instanceof Number n) return n.longValue();
        if (o == null) return null;
        try {
            return Long.valueOf(o.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String str(Object o) {
        return o == null ? null : o.toString().trim();
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
