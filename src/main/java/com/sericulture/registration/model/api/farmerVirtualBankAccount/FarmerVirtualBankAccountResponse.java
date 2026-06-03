package com.sericulture.registration.model.api.farmerVirtualBankAccount;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class FarmerVirtualBankAccountResponse {
    @Schema(name = "farmerVirtualBankAccountId", example = "1")
    Long farmerVirtualBankAccountId;

    @Schema(name = "farmerId", example = "1")
    Long farmerId;

    @Schema(name = "virtualAccountNumber", example = "3654643675")
    String virtualAccountNumber;

    @Schema(name = "branchName", example = "Bengaluru")
    String branchName;

    @Schema(name = "ifscCode", example = "SBI00457")
    String ifscCode;

    @Schema(name = "marketMasterId", example = "1")
    Long marketMasterId;

    @Schema(name = "marketMasterName", example = "Market name")
    String marketMasterName;

    @Schema(name = "firstName", example = "Farmer first name")
    String firstName;

    @Schema(name = "farmerNumber", example = "Farmer number")
    String farmerNumber;

    @Schema(name = "fruitsId", example = "Fruits ID")
    String fruitsId;

    @Schema(name = "mobileNumber", example = "Mobile number")
    String mobileNumber;

    @Schema(name = "isLocked", example = "false")
    Boolean isLocked;

    @Schema(name = "error", example = "true")
    Boolean error;

    @Schema(name = "error_description", example = "Username or password is incorrect")
    String error_description;
}