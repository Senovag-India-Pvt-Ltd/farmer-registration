package com.sericulture.registration.model.api.farmerType;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class EditFarmerTypeRequest extends RequestBody {
    @Schema(name = "farmerTypeId", example = "1")
    Long farmerTypeId;

    @Schema(name = "name", example = "Margin farmer")
    String farmerTypeName;
}