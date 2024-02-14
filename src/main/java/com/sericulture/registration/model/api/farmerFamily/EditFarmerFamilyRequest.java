package com.sericulture.registration.model.api.farmerFamily;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class EditFarmerFamilyRequest extends RequestBody {
    @Schema(name = "farmerFamilyId", example = "1")
    Long farmerFamilyId;

    @Schema(name = "farmerId", example = "1")
    Long farmerId;

    @Schema(name = "relationshipId", example = "1")
    Long relationshipId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Farmer family name must contain only letters and numbers")
    @Schema(name = "farmerFamilyName", example = "Latha", required = true)
    String farmerFamilyName;
}