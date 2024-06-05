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
public class FarmerFamilyRequest extends RequestBody {
    @Schema(name = "farmerId", example = "1", required = true)
    Long farmerId;

    @Schema(name = "relationshipId", example = "1", required = true)
    Long relationshipId;

   // @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Farmer family name must contain only letters and numbers")
    @Schema(name = "farmerFamilyName", example = "Latha", required = true)
    String farmerFamilyName;
}