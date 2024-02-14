package com.sericulture.registration.model.api.farmerType;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class EditFarmerTypeRequest extends RequestBody {
    @Schema(name = "farmerTypeId", example = "1")
    Long farmerTypeId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Farmer type name must contain only letters and numbers")
    @Schema(name = "farmerTypeName", example = "Margin farmer")
    String farmerTypeName;

    @Pattern(regexp = "^[a-zA-Z0-9\\s\\u0C80-\\u0CFF]*$", message = "Farmer type name in kannada must contain only letters and numbers")
    @Schema(name = "farmerTypeNameInKannada", example = "ಭಾಷೆ")
    String farmerTypeNameInKannada;
}