package com.sericulture.registration.model.api.village;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class VillageResponse {

    @Schema(name="stateId", example = "1")
    int stateId;

    @Schema(name="districtId", example = "1")
    int districtId;

    @Schema(name="talukId", example = "1")
    int talukId;

    @Schema(name="hobliId", example = "1")
    int hobliId;

    @Schema(name="villageId", example = "1")
    int villageId;

    @Schema(name="stateName", example = "karnaa")
    String stateName;

    @Schema(name="districtName", example = "Udupi")
    String districtName;

    @Schema(name="talukName", example = "kundapurr")
    String talukName;

    @Schema(name = "hobliName", example = "Kasaba")
    String hobliName;

    @Schema(name = "villageName", example = "Hodala")
    String villageName;

    @Schema(name = "error", example = "0")
    String error;

    @Schema(name = "error_description", example = "Incorrect caste")
    String error_description;
}

