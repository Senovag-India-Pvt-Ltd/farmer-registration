package com.sericulture.registration.model.api.externalUnitRegistration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalUnitRegistrationResponse {
    @Schema(name = "externalUnitRegistrationId", example = "1")
    Long externalUnitRegistrationId;

    @Schema(name = "externalUnitTypeId", example = "1")
    Long externalUnitTypeId;

    @Schema(name = "name", example = "Test")
    String name;

    @Schema(name = "address", example = "Test")
    String address;

    @Schema(name = "licenseNumber", example = "435435")
    String licenseNumber;

    @Schema(name = "externalUnitNumber", example = "S5346")
    String externalUnitNumber;
}