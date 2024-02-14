package com.sericulture.registration.model.api.farmerType;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class FarmerTypeRequest extends RequestBody {
    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Farmer type name must contain only letters and numbers")
    @Schema(name = "farmerTypeName", example = "Margin farmer")
    String farmerTypeName;

    @Pattern(regexp = "^[a-zA-Z0-9\\s\\u0C80-\\u0CFF]*$", message = "Farmer type name in kannada must contain only letters and numbers")
    @Schema(name = "farmerTypeNameInKannada", example = "ಭಾಷೆ")
    String farmerTypeNameInKannada;
}
