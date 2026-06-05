package com.sericulture.registration.model.dto.fruitsApi;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Request body received by the backend for the Get-Crop-Survey-Data endpoint.
 * Credentials are added server-side, never by the caller.
 */
@Data
public class CropSurveyDTO {
    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Farmer id must contain only letters and numbers")
    private String farmerId;

    @NotNull(message = "Year code is mandatory")
    private Integer yearCode;

    @NotNull(message = "Season code is mandatory")
    private Integer seasonCode;

    public CropSurveyDTO() {
    }

    public CropSurveyDTO(String farmerId, Integer yearCode, Integer seasonCode) {
        this.farmerId = farmerId;
        this.yearCode = yearCode;
        this.seasonCode = seasonCode;
    }
}
