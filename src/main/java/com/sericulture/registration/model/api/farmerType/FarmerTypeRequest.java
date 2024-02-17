package com.sericulture.registration.model.api.farmerType;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class FarmerTypeRequest extends RequestBody {
    @Schema(name = "farmerTypeName", example = "Margin farmer")
    String farmerTypeName;

    @Schema(name = "farmerTypeNameInKannada", example = "ಭಾಷೆ")
    String farmerTypeNameInKannada;
}
