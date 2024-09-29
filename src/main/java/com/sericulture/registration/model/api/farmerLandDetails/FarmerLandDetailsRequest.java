package com.sericulture.registration.model.api.farmerLandDetails;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class FarmerLandDetailsRequest extends RequestBody {
    @Schema(name = "farmerId", example = "1", required = true)
    Long farmerId;

    @Schema(name = "farmerLandDetailsId", example = "1", required = true)
    Long farmerLandDetailsId;

  //  @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Category number must contain only letters and numbers")
    @Schema(name = "categoryNumber", example = "1")
    private String categoryNumber;

    @Schema(name = "landOwnershipId", example = "1")
    private Long landOwnershipId;

    @Schema(name = "soilTypeId", example = "1")
    private Long soilTypeId;

//    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Hissa must contain only letters and numbers")
  //  @Pattern(regexp = "^[a-zA-Z0-9\\s.,/#*\"']*$", message = "Hissa must contain only letters and numbers")
    @Schema(name = "hissa", example = "test")
    private String hissa;

    @Schema(name = "mulberrySourceId", example = "1")
    private Long mulberrySourceId;

  //  @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Mulberry area must contain only letters and numbers")
    @Schema(name = "mulberryArea", example = "Test")
    private String mulberryArea;

    @Schema(name = "mulberryVarietyId", example = "1")
    private Long mulberryVarietyId;

    @Temporal(TemporalType.TIMESTAMP)
    @Schema(name = "plantationDate", example = "2023-05-23")
    private Date plantationDate;

  //  @Pattern(regexp = "^[a-zA-Z0-9\\s()+]*$", message = "Spacing must contain only letters and numbers")
    @Schema(name = "spacing", example = ",")
    private String spacing;

    @Schema(name = "plantationTypeId", example = "1")
    private Long plantationTypeId;

    @Schema(name = "irrigationSourceId", example = "1")
    private Long irrigationSourceId;

    @Schema(name = "irrigationTypeId", example = "1")
    private Long irrigationTypeId;

  //  @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Rearing house details must contain only letters and numbers")
    @Schema(name = "rearingHouseDetails", example = "Test")
    private String rearingHouseDetails;

    @Schema(name = "roofTypeId", example = "1")
    private Long roofTypeId;

    @Schema(name = "silkWormVarietyId", example = "1")
    private Long silkWormVarietyId;

   // @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Rearing capacity crops must contain only letters and numbers")
    @Schema(name = "rearingCapacityCrops", example = "test")
    private String rearingCapacityCrops;

   // @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Rearing capacity dlf must contain only letters and numbers")
    @Schema(name = "rearingCapacityDlf", example = "test")
    private String rearingCapacityDlf;

    @Schema(name = "subsidyAvailed", example = "1")
    private Boolean subsidyAvailed;

    @Schema(name = "subsidyId", example = "1")
    private Long subsidyId;

   // @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Loan details must contain only letters and numbers")
    @Schema(name = "loanDetails", example = "test")
    private String loanDetails;

   // @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Equipment details must contain only letters and numbers")
    @Schema(name = "equipmentDetails", example = "test")
    private String equipmentDetails;

   // @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "GPS lat must contain only letters and numbers")
    @Schema(name = "gpsLat", example = "132.44")
    private String gpsLat;

   // @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "GPS Lang must contain only letters and numbers")
    @Schema(name = "gpsLng", example = "145.33")
    private String gpsLng;

//    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Survey number must contain only letters and numbers")
   // @Pattern(regexp = "^[a-zA-Z0-9\\s.,/#*\"']*$", message = "Survey number must contain only letters and numbers")
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

//    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Address must contain only letters and numbers")
  //  @Pattern(regexp = "^[a-zA-Z0-9\\s.,/#*\"']*$", message = "Address must contain only letters and numbers")
    @Schema(name = "address", example = "TTH")
    private String address;

  //  @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Pincode must contain only letters and numbers")
    @Schema(name = "pincode", example = "135345")
    private String pincode;

//    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Owner name must contain only letters and numbers")
 //   @Pattern(regexp = "^[a-zA-Z0-9\\s\\u0C80-\\u0CFF.,/#*\"']*$", message = "Owner name must contain only letters and numbers")
    @Schema(name = "ownerName", example = "Test")
    private String ownerName;

//    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Sur noc must contain only letters and numbers")
 //   @Pattern(regexp = "^[a-zA-Z0-9\\s.,/#*\"']*$", message = "Sur noc must contain only letters and numbers")
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

    @Schema(name = "landCode", example = "1")
    private Long landCode;

    @Schema(name = "districtCode", example = "1")
    private Long districtCode;

    @Schema(name = "talukCode", example = "1")
    private Long talukCode;

    @Schema(name = "hobliCode", example = "1")
    private Long hobliCode;

    @Schema(name = "villageCode", example = "1")
    private Long villageCode;
}