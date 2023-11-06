package com.sericulture.registration.model.api.farmerFamily;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(name = "farmerFamilyName", example = "Latha", required = true)
    String farmerFamilyName;
}