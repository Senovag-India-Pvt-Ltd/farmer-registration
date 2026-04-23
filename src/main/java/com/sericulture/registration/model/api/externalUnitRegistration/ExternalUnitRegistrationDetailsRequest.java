package com.sericulture.registration.model.api.externalUnitRegistration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ExternalUnitRegistrationDetailsRequest {

    @Schema(name = "virtualAccountNumber", example = "3654643675")
    String virtualAccountNumber;

    @Schema(name = "branchName", example = "Bengaluru")
    String branchName;

    @Schema(name = "ifscCode", example = "SBI00457")
    String ifscCode;

    @Schema(name = "marketMasterId", example = "1")
    Long marketMasterId;

    @Schema(name = "lock", example = "1")
    Boolean lock;

    @Schema(name = "marketMasterName", example = "test")
    String marketMasterName;

    @Schema(name = "active", example = "1")
    Boolean deleted;
    @Column(name = "eu_virtual_bank_account_id")
    private Long id;
}
