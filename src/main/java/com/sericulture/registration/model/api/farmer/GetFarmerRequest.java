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

    @Schema(name = "farmerNumber", example = "2")
    String farmerNumber;

    @Schema(name = "mobileNumber", example = "7788990066")
    String mobileNumber;
}