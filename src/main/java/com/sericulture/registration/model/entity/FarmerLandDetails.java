package com.sericulture.registration.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FarmerLandDetails extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FARMER_LAND_DETAILS_SEQ")
    @SequenceGenerator(name = "FARMER_LAND_DETAILS_SEQ", sequenceName = "FARMER_LAND_DETAILS_SEQ", allocationSize = 1)
    @Column(name = "FARMER_LAND_DETAILS_ID")
    private Long farmerLandDetailsId;

    @Column(name = "FARMER_ID")
    private Long farmerId;

    @Column(name = "category_number")
    private String categoryNumber;

    @Column(name = "land_ownership_id")
    private Long landOwnershipId;

    @Column(name = "soil_type_id")
    private Long soilTypeId;

    @Column(name = "hissa")
    private String hissa;

    @Column(name = "mulberry_source_id")
    private Long mulberrySourceId;

    @Column(name = "mulberry_area")
    private String mulberryArea;

    @Column(name = "mulberry_variety_id")
    private Long mulberryVarietyId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "plantation_date")
    private Date plantationDate;

    @Column(name = "spacing")
    private String spacing;

    @Column(name = "plantation_type_id")
    private Long plantationTypeId;

    @Column(name = "irrigation_source_id")
    private Long irrigationSourceId;

    @Column(name = "irrigation_type_id")
    private Long irrigationTypeId;

    @Column(name = "rearing_house_details")
    private String rearingHouseDetails;

    @Column(name = "roof_type_id")
    private Long roofTypeId;

    @Column(name = "silk_worm_variety_id")
    private Long silkWormVarietyId;

    @Column(name = "rearing_capacity_crops")
    private String rearingCapacityCrops;

    @Column(name = "rearing_capacity_dlf")
    private String rearingCapacityDlf;

    @Column(name = "subsidy_availed", columnDefinition = "TINYINT")
    private Boolean subsidyAvailed;

    @Column(name = "subsidy_master_id")
    private Long subsidyId;

    @Column(name = "loan_details")
    private String loanDetails;

    @Column(name = "equipment_details")
    private String equipmentDetails;

    @Column(name = "gps_lat")
    private String gpsLat;

    @Column(name = "gps_lng")
    private String gpsLng;

    @Column(name = "survey_number")
    private String surveyNumber;

    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "taluk_id")
    private Long talukId;

    @Column(name = "hobli_id")
    private Long hobliId;

    @Column(name = "village_id")
    private Long villageId;

    @Column(name = "address")
    private String address;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "sur_noc")
    private String surNoc;

    @Column(name = "name_score")
    private Long nameScore;

    @Column(name = "owner_no")
    private Long ownerNo;

    @Column(name = "main_owner_no")
    private Long mainOwnerNo;

    @Column(name = "acre")
    private Long acre;

    @Column(name = "gunta")
    private Long gunta;

    @Column(name = "f_gunta")
    private Double fGunta;

    @Column(name = "land_code")
    private Long landCode;
}