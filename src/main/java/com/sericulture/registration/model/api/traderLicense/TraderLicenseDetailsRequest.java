package com.sericulture.registration.model.api.traderLicense;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TraderLicenseDetailsRequest extends RequestBody {

    @Schema(name = "virtualAccountNumber", example = "3654643675")
    String virtualAccountNumber;

    @Schema(name = "branchName", example = "Bengaluru")
    String branchName;

    @Schema(name = "ifscCode", example = "SBI00457")
    String ifscCode;

    @Schema(name = "marketMasterId", example = "1")
    Long marketMasterId;
}
