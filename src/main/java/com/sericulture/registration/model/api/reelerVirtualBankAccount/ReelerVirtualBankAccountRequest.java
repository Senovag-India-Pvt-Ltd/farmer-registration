package com.sericulture.registration.model.api.reelerVirtualBankAccount;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ReelerVirtualBankAccountRequest extends RequestBody {
    @Schema(name = "reelerId", example = "1")
    Long reelerId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Virtual account number must contain only letters and numbers")
    @Schema(name = "virtualAccountNumber", example = "3654643675")
    String virtualAccountNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Branch name must contain only letters and numbers")
    @Schema(name = "branchName", example = "Bengaluru")
    String branchName;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "IFSC code must contain only letters and numbers")
    @Schema(name = "ifscCode", example = "SBI00457")
    String ifscCode;

    @Schema(name = "marketMasterId", example = "1")
    Long marketMasterId;
}
