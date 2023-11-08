package com.sericulture.registration.model.api.farmerBankAccount;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(name = "farmerBankName", example = "SBI")
    String farmerBankName;

    @Schema(name = "farmerBankAccountNumber", example = "123")
    String farmerBankAccountNumber;

    @Schema(name = "farmerBankBranchName", example = "Bengaluru")
    String farmerBankBranchName;

    @Schema(name = "farmerBankIfscCode", example = "SBI0005463")
    String farmerBankIfscCode;
}