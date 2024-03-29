package com.sericulture.registration.model.api.farmerAddress;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class FarmerAddressRequest extends RequestBody {
    @Schema(name = "farmerId", example = "1", required = true)
    Long farmerId;

    @Schema(name = "stateId", example = "1", required = true)
    Long stateId;

    @Schema(name = "districtId", example = "1", required = true)
    Long districtId;

    @Schema(name = "talukId", example = "1", required = true)
    Long talukId;

    @Schema(name = "hobliId", example = "1", required = true)
    Long hobliId;

    @Schema(name = "villageId", example = "1", required = true)
    Long villageId;

    @Schema(name = "addressText", example = "Bengaluru", required = true)
    String addressText;

    @Schema(name = "pincode", example = "566434", required = true)
    String pincode;

    @Schema(name = "defaultAddress", example = "0")
    Boolean defaultAddress;
}