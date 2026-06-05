package com.sericulture.registration.model.dto.fruitsApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Payload sent to the FRUITS GetCropSurveyDataByYear API.
 * Field names (FarmerID, YearCode, SeasonCode, UserName, UserPassword) match the FRUITS API document exactly.
 */
@Data
public class GetCropSurveyRequest {
    @JsonProperty("FarmerID")
    private String farmerID;

    @JsonProperty("YearCode")
    private Integer yearCode;

    @JsonProperty("SeasonCode")
    private Integer seasonCode;

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("UserPassword")
    private String userPassword;

    public GetCropSurveyRequest() {
    }

    public GetCropSurveyRequest(String farmerID, Integer yearCode, Integer seasonCode, String userName, String userPassword) {
        this.farmerID = farmerID;
        this.yearCode = yearCode;
        this.seasonCode = seasonCode;
        this.userName = userName;
        this.userPassword = userPassword;
    }
}
