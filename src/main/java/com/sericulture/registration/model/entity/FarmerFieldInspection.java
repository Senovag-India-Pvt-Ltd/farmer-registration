package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * GPS-tagged field inspection of a farmer's farm or rearing house (SR-007), always tied to one land record of the
 * same farmer. Table: db-scripts/ddl/farmer_field_inspection.sql
 */
@Entity
@Table(name = "farmer_field_inspection")
@Getter
@Setter
@NoArgsConstructor
public class FarmerFieldInspection extends BaseEntity implements Serializable {
    public static final String FARM = "FARM";
    public static final String REARING_HOUSE = "REARING_HOUSE";
    public static final String DEVICE = "DEVICE";
    public static final String MANUAL = "MANUAL";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FARMER_FIELD_INSPECTION_SEQ")
    @SequenceGenerator(name = "FARMER_FIELD_INSPECTION_SEQ", sequenceName = "FARMER_FIELD_INSPECTION_SEQ", allocationSize = 1)
    @Column(name = "farmer_field_inspection_id")
    private Long farmerFieldInspectionId;

    @Column(name = "farmer_id", nullable = false)
    private Long farmerId;

    @Column(name = "farmer_land_details_id", nullable = false)
    private Long farmerLandDetailsId;

    @Column(name = "site_type", nullable = false)
    private String siteType;

    @Column(name = "inspection_date", nullable = false)
    private LocalDate inspectionDate;

    @Column(name = "latitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "gps_accuracy_m", precision = 9, scale = 2)
    private BigDecimal gpsAccuracyM;

    @Column(name = "gps_source", nullable = false)
    private String gpsSource;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "gps_captured_at")
    private Date gpsCapturedAt;

    @Column(name = "observations")
    private String observations;

    @Column(name = "photo_s3_key")
    private String photoS3Key;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @Column(name = "inspected_by")
    private String inspectedBy;
}
