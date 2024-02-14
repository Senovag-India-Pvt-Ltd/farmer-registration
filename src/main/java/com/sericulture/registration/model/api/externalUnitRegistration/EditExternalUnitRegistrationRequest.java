package com.sericulture.registration.model.api.externalUnitRegistration;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class EditExternalUnitRegistrationRequest extends RequestBody {
    @Schema(name = "externalUnitRegistrationId", example = "1")
    Long externalUnitRegistrationId;

    @Schema(name = "externalUnitTypeId", example = "1")
    Long externalUnitTypeId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Name must contain only letters and numbers")
    @Schema(name = "name", example = "Test")
    String name;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Address must contain only letters and numbers")
    @Schema(name = "address", example = "Test")
    String address;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "License number must contain only letters and numbers")
    @Schema(name = "licenseNumber", example = "435435")
    String licenseNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "External unit number must contain only letters and numbers")
    @Schema(name = "externalUnitNumber", example = "S5346")
    String externalUnitNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Organization name must contain only letters and numbers")
    @Schema(name = "organisationName", example = "Test")
    String organisationName;

    @Schema(name = "raceMasterId", example = "1")
    Long raceMasterId;
}