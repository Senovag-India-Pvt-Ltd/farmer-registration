package com.sericulture.registration.model.api.farmer;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class GetFarmerRequest extends RequestBody {
    @Schema(name = "fruitsId", example = "2")
    String fruitsId;
}