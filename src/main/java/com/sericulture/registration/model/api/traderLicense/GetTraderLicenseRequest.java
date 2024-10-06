package com.sericulture.registration.model.api.traderLicense;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class GetTraderLicenseRequest extends RequestBody {
    @Schema(name = "traderLicenseId", example = "1")
    Long traderLicenseId;

    @Schema(name = "traderLicenseNumber", example = "2")
    String traderLicenseNumber;

    @Schema(name = "mobileNumber", example = "7788990066")
    String mobileNumber;

    @Schema(name = "marketId", example = "1")
    Long marketId;

}