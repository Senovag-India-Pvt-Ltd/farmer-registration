package com.sericulture.registration.model.dto.fruitsApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Payload sent to the FRUITS GetFarmerByAadharHash API.
 * Field names (HashofAadhaar, UserName, UserPassword) match the FRUITS API document exactly.
 */
@Data
public class GetFruitsAadharRequest {
    @JsonProperty("HashofAadhaar")
    private String hashOfAadhaar;

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("UserPassword")
    private String userPassword;

    public GetFruitsAadharRequest() {
    }

    public GetFruitsAadharRequest(String hashOfAadhaar, String userName, String userPassword) {
        this.hashOfAadhaar = hashOfAadhaar;
        this.userName = userName;
        this.userPassword = userPassword;
    }
}
