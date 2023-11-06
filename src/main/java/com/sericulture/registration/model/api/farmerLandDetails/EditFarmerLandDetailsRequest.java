package com.sericulture.registration.model.api.farmerLandDetails;

import com.sericulture.registration.model.api.RequestBody;
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
public class EditFarmerLandDetailsRequest extends RequestBody {
    @Schema(name = "farmerLandDetailsId", example = "1")
    Long farmerLandDetailsId;

    @Schema(name = "farmerId", example = "1", required = true)
    Long farmerId;

    @Schema(name = "categoryNumber", example = "1")
    private String categoryNumber;

    @Schema(name = "landOwnerShipId", example = "1")
    private Long landOwnerShipId;

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

    @Schema(name = "subsidyMasterId", example = "1")
    private Long subsidyMasterId;

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
}