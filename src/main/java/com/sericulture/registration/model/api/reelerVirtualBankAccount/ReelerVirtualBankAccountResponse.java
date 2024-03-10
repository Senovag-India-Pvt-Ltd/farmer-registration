package com.sericulture.registration.model.api.reelerVirtualBankAccount;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReelerVirtualBankAccountResponse {
    @Schema(name = "reelerVirtualBankAccountId", example = "1")
    Long reelerVirtualBankAccountId;

    @Schema(name = "reelerId", example = "1")
    Long reelerId;

    @Schema(name = "virtualAccountNumber", example = "3654643675")
    String virtualAccountNumber;

    @Schema(name = "branchName", example = "Bengaluru")
    String branchName;

    @Schema(name = "ifscCode", example = "SBI00457")
    String ifscCode;

    @Schema(name = "marketMasterId", example = "1")
    Long marketMasterId;

    @Schema(name = "marketMasterName", example = "1")
    String marketMasterName;

    @Schema(name = "reelerName", example = "Reeler name")
    String reelerName;

    @Schema(name = "error", example = "true")
    Boolean error;

    @Schema(name = "error_description", example = "Username or password is incorrect")
    String error_description;
}