package com.sericulture.registration.model.api.farmerType;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class FarmerTypeResponse extends RequestBody {
    @Schema(name = "farmerTypeId", example = "1")
    Long farmerTypeId;

    @Schema(name = "farmerTypeName", example = "Margin farmer")
    String farmerTypeName;

    @Schema(name = "farmerTypeNameInKannada", example = "ಭಾಷೆ")
    String farmerTypeNameInKannada;

    @Schema(name = "error", example = "true")
    Boolean error;

    @Schema(name = "error_description", example = "Username or password is incorrect")
    String error_description;
}