package com.sericulture.registration.model.dto.reeler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReelerVirtualBankAccountDTO {
    private Long reelerVirtualBankAccountId;
    private Long reelerId;
    private String virtualAccountNumber;
    private String branchName;
    private String ifscCode;
    private Long marketMasterId;
    private String reelerName;
    private String reelingLicenseNumber;
    private String reelerNumber;
    private String mobileNumber;
    private String marketMasterName;
}
