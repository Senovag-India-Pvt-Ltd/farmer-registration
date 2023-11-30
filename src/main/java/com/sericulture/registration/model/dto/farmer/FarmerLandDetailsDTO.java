package com.sericulture.registration.model.dto.farmer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerLandDetailsDTO {
    private Long farmerLandDetailsId;
    private Long farmerId;
    private String categoryNumber;
    private Long landOwnershipId;
    private Long soilTypeId;
    private String hissa;
    private Long mulberrySourceId;
    private String mulberryArea;
    private Long mulberryVarietyId;
    private Date plantationDate;
    private String spacing;
    private Long plantationTypeId;
    private Long irrigationSourceId;
    private Long irrigationTypeId;
    private String rearingHouseDetails;
    private Long roofTypeId;
    private Long silkWormVarietyId;
    private String rearingCapacityCrops;
    private String rearingCapacityDlf;
    private Boolean subsidyAvailed;
    private Long subsidyId;
    private String loanDetails;
    private String equipmentDetails;
    private String gpsLat;
    private String gpsLng;
    private String surveyNumber;
    private Long stateId;
    private Long districtId;
    private Long talukId;
    private Long hobliId;
    private Long villageId;
    private String address;
    private String pincode;
    private String ownerName;
    private String surNoc;
    private Long nameScore;
    private Long ownerNo;
    private Long mainOwnerNo;
    private Long acre;
    private Long gunta;
    private Double fGunta;
    private String landOwnershipName;
    private String soilTypeName;
    private String mulberrySourceName;
    private String mulberryVarietyName;
    private String plantationTypeName;
    private String irrigationSourceName;
    private String irrigationTypeName;
    private String roofTypeName;
    private String silkWormVarietyName;
    private String subsidyName;
    private String stateName;
    private String districtName;
    private String talukName;
    private String hobliName;
    private String villageName;
}
