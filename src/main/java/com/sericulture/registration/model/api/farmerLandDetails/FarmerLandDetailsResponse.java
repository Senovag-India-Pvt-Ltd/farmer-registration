package com.sericulture.registration.model.api.farmerLandDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class FarmerLandDetailsResponse {

    @Schema(name="farmerLandDetailsId", example = "1")
    int farmerLandDetailsId;

    @Schema(name = "farmerId", example = "1", required = true)
    Long farmerId;

    @Schema(name = "categoryNumber", example = "1")
    private String categoryNumber;

    @Schema(name = "landOwnershipId", example = "1")
    private Long landOwnershipId;

    @Schema(name = "soilTypeId", example = "1")
    private Long soilTypeId;

    @Schema(name = "hissa", example = "test")
    private String hissa;

    @Schema(name = "mulberrySourceId", example = "1")
    private Long mulberrySourceId;

    @Schema(name = "mulberryArea", example = "Test")
    private String mulberryArea;

    @Schema(name = "mulberryVarietyId", example = "1")
    private Long mulberryVarietyId;

    @Temporal(TemporalType.TIMESTAMP)
    @Schema(name = "plantationDate", example = "2023-05-23")
    private Date plantationDate;

    @Schema(name = "spacing", example = ",")
    private String spacing;

    @Schema(name = "plantationTypeId", example = "1")
    private Long plantationTypeId;

    @Schema(name = "irrigationSourceId", example = "1")
    private Long irrigationSourceId;

    @Schema(name = "irrigationTypeId", example = "1")
    private Long irrigationTypeId;

    @Schema(name = "rearingHouseDetails", example = "Test")
    private String rearingHouseDetails;

    @Schema(name = "roofTypeId", example = "1")
    private Long roofTypeId;

    @Schema(name = "silkWormVarietyId", example = "1")
    private Long silkWormVarietyId;

    @Schema(name = "rearingCapacityCrops", example = "test")
    private String rearingCapacityCrops;

    @Schema(name = "rearingCapacityDlf", example = "test")
    private String rearingCapacityDlf;

    @Schema(name = "subsidyAvailed", example = "1")
    private Boolean subsidyAvailed;

    @Schema(name = "subsidyId", example = "1")
    private Long subsidyId;

    @Schema(name = "loanDetails", example = "test")
    private String loanDetails;

    @Schema(name = "equipmentDetails", example = "test")
    private String equipmentDetails;

    @Schema(name = "gpsLat", example = "132.44")
    private String gpsLat;

    @Schema(name = "gpsLng", example = "145.33")
    private String gpsLng;

    @Schema(name = "surveyNumber", example = "13543")
    private String surveyNumber;

    @Schema(name = "stateId", example = "1")
    private Long stateId;

    @Schema(name = "districtId", example = "1")
    private Long districtId;

    @Schema(name = "talukId", example = "1")
    private Long talukId;

    @Schema(name = "hobliId", example = "1")
    private Long hobliId;

    @Schema(name = "villageId", example = "1")
    private Long villageId;

    @Schema(name = "address", example = "TTH")
    private String address;

    @Schema(name = "pincode", example = "135345")
    private String pincode;

    @Schema(name = "ownerName", example = "Test")
    private String ownerName;

    @Schema(name = "surNoc", example = "135345")
    private String surNoc;

    @Schema(name = "nameScore", example = "1")
    private Long nameScore;

    @Schema(name = "ownerNo", example = "1")
    private Long ownerNo;

    @Schema(name = "mainOwnerNo", example = "1")
    private Long mainOwnerNo;

    @Schema(name = "acre", example = "1")
    private Long acre;

    @Schema(name = "gunta", example = "1")
    private Long gunta;

    @Schema(name = "fGunta", example = "1")
    private Double fGunta;

    @Schema(name="landOwnershipName", example = "karnaa")
    private String landOwnershipName;

    @Schema(name="soilTypeName", example = "Udupi")
    private String soilTypeName;

    @Schema(name="mulberrySourceName", example = "kundapurr")
    private String mulberrySourceName;

    @Schema(name = "mulberryVarietyName", example = "Kasaba")
    private String mulberryVarietyName;

    @Schema(name = "plantationTypeName", example = "Hodla")
    private String plantationTypeName;

    @Schema(name="irrigationSourceName", example = "karnaa")
    private String irrigationSourceName;

    @Schema(name="irrigationTypeName", example = "Udupi")
    private String irrigationTypeName;

    @Schema(name="roofTypeName", example = "kundapurr")
    private String roofTypeName;

    @Schema(name = "silkWormVarietyName", example = "Kasaba")
    private String silkWormVarietyName;

    @Schema(name = "subsidyName", example = "Hodla")
    private String subsidyName;

    @Schema(name="stateName", example = "karnaa")
    private String stateName;

    @Schema(name="districtName", example = "Udupi")
    private String districtName;

    @Schema(name="talukName", example = "kundapurr")
    private String talukName;

    @Schema(name = "hobliName", example = "Kasaba")
    private String hobliName;

    @Schema(name = "villageName", example = "Hodla")
    private String villageName;

    @Schema(name = "landCode", example = "1")
    private Long landCode;

    @Schema(name = "error", example = "true")
    Boolean error;

    @Schema(name = "error_description", example = "Username or password is incorrect")
    String error_description;
}