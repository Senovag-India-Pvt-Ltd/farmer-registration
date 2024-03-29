package com.sericulture.registration.model.api.farmerAddress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class FarmerAddressResponse {

    @Schema(name="farmerAddressId", example = "1")
    int farmerAddressId;

    @Schema(name = "farmerId", example = "1", required = true)
    Long farmerId;

    @Schema(name = "stateId", example = "1", required = true)
    Long stateId;

    @Schema(name = "districtId", example = "1", required = true)
    Long districtId;

    @Schema(name = "talukId", example = "1", required = true)
    Long talukId;

    @Schema(name = "hobliId", example = "1", required = true)
    Long hobliId;

    @Schema(name = "villageId", example = "1", required = true)
    Long villageId;

    @Schema(name = "addressText", example = "Bengaluru", required = true)
    String addressText;

    @Schema(name = "pincode", example = "566434", required = true)
    String pincode;

    @Schema(name = "defaultAddress", example = "0")
    Boolean defaultAddress;

    @Schema(name="stateName", example = "karnaa")
    String stateName;

    @Schema(name="districtName", example = "Udupi")
    String districtName;

    @Schema(name="talukName", example = "kundapurr")
    String talukName;

    @Schema(name = "hobliName", example = "Kasaba")
    String hobliName;

    @Schema(name = "villageName", example = "Hodla")
    String villageName;

    @Schema(name = "error", example = "true")
    Boolean error;

    @Schema(name = "error_description", example = "Username or password is incorrect")
    String error_description;
}