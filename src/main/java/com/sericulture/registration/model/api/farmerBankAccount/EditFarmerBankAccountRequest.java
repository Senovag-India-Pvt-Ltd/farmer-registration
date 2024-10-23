package com.sericulture.registration.model.api.farmerBankAccount;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class EditFarmerBankAccountRequest extends RequestBody {
    @Schema(name="farmerBankAccountId", example = "1")
    Long farmerBankAccountId;

    @Schema(name="farmerId", example = "1")
    Long farmerId;

//    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Farmer bank name must contain only letters and numbers")
    @Schema(name = "farmerBankName", example = "SBI")
    String farmerBankName;

//    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Farmer bank account number must contain only letters and numbers")
    @Schema(name = "farmerBankAccountNumber", example = "123")
    String farmerBankAccountNumber;

//    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Farmer bank branch name must contain only letters and numbers")
    @Schema(name = "farmerBankBranchName", example = "Bengaluru")
    String farmerBankBranchName;

//    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Farmer bank ifsc code must contain only letters and numbers")
    @Schema(name = "farmerBankIfscCode", example = "SBI0005463")
    String farmerBankIfscCode;

//    @Pattern(regexp = "^[a-zA-Z0-9-/_.\\s]*$", message = "Account image path must contain only letters and numbers")
    @Schema(name = "accountImagePath", example = "/example.jpg")
    String accountImagePath;

    @Schema(name = "reasonMasterId", example = "reasonMasterId")
    Long reasonMasterId;

    @Schema(name = "remark", example = "remark")
    String remark;

    @Schema(name = "lock", example = "1")
    Boolean lock;
}