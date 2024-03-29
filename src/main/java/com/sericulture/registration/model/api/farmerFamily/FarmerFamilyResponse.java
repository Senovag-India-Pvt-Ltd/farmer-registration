package com.sericulture.registration.model.api.farmerFamily;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class FarmerFamilyResponse {

    @Schema(name="farmerFamilyId", example = "1")
    int farmerFamilyId;

    @Schema(name="farmerId", example = "1")
    int farmerId;

    @Schema(name="relationshipId", example = "1")
    int relationshipId;

    @Schema(name = "farmerFamilyName", example = "Latha")
    String farmerFamilyName;

    @Schema(name="relationshipName", example = "1")
    String relationshipName;

    @Schema(name = "error", example = "true")
    Boolean error;

    @Schema(name = "error_description", example = "Username or password is incorrect")
    String error_description;
}