package com.sericulture.registration.model.api.reelerLicenseTransaction;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ReelerLicenseTransactionRequest extends RequestBody {
    @Schema(name = "reelerId", example = "1")
    Long reelerId;

    @Schema(name = "feeAmount", example = "13.56")
    Double feeAmount;

    @Schema(name = "renewedDate", example = "2023-11-03 16:27:35.907")
    Date renewedDate;

    @Schema(name = "expirationDate", example = "2023-11-03 16:27:35.907")
    Date expirationDate;
}