package com.sericulture.registration.model.api.reeler;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UpdateReelerLicenseRequest extends RequestBody {
    @Schema(name = "reelerId", example = "1")
    Long reelerId;

    @Schema(name = "status", example = "0")
    int status;

    @Schema(name = "feeAmount", example = "15.67")
    double feeAmount;

    @Schema(name = "licenseRenewalDate", example = "2023-11-03 16:27:35.907")
    Date licenseRenewalDate;

    @Schema(name = "licenseExpiryDate", example = "2023-11-03 16:27:35.907")
    Date licenseExpiryDate;

}