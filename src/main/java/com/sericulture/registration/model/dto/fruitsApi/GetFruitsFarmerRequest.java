package com.sericulture.registration.model.dto.fruitsApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Payload sent to the FRUITS GetFarmerByFID API.
 * Field names (FarmerId, UserName, UserPassword) match the FRUITS API document exactly.
 * Kept separate from {@link FruitsFarmerDTO} (the request body received from the UI,
 * which uses lowercase "farmerId" and must never carry credentials).
 */
@Data
public class GetFruitsFarmerRequest {
    @JsonProperty("FarmerId")
    private String farmerId;

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("UserPassword")
    private String userPassword;

    public GetFruitsFarmerRequest() {
    }

    public GetFruitsFarmerRequest(String farmerId, String userName, String userPassword) {
        this.farmerId = farmerId;
        this.userName = userName;
        this.userPassword = userPassword;
    }
}
