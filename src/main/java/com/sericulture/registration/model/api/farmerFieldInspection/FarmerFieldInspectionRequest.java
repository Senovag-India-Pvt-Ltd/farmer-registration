package com.sericulture.registration.model.api.farmerFieldInspection;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/** SR-007: one GPS-tagged field inspection (multipart form fields; the optional photo is a separate part). */
@Getter
@Setter
@NoArgsConstructor
public class FarmerFieldInspectionRequest {
    @NotNull(message = "farmerId is required")
    @Schema(example = "25670")
    Long farmerId;

    @NotNull(message = "Select the land / farm")
    @Schema(example = "109955")
    Long farmerLandDetailsId;

    @NotBlank(message = "Select farm or rearing house")
    @Pattern(regexp = "^(FARM|REARING_HOUSE)$", message = "Site must be FARM or REARING_HOUSE")
    @Schema(example = "FARM")
    String siteType;

    @NotNull(message = "Inspection date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(example = "2026-11-24")
    LocalDate inspectionDate;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90", message = "Latitude must be between -90 and 90")
    @Schema(example = "13.304512")
    BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180", message = "Longitude must be between -180 and 180")
    @Schema(example = "76.912345")
    BigDecimal longitude;

    @DecimalMin(value = "0", message = "GPS accuracy cannot be negative")
    @Schema(example = "12.5", description = "Accuracy reported by the device, in metres")
    BigDecimal gpsAccuracyM;

    @NotBlank(message = "GPS source is required")
    @Pattern(regexp = "^(DEVICE|MANUAL)$", message = "GPS source must be DEVICE or MANUAL")
    @Schema(example = "DEVICE")
    String gpsSource;

    @Schema(example = "1795500000000", description = "When the device captured the position (epoch milliseconds)")
    Long gpsCapturedAt;

    @Size(max = 1000, message = "Observations must be at most 1000 characters")
    @Schema(example = "Mulberry garden healthy; rearing house clean")
    String observations;
}
