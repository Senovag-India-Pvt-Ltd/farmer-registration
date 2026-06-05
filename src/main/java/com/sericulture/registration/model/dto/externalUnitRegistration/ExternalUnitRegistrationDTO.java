package com.sericulture.registration.model.dto.externalUnitRegistration;

import com.sericulture.registration.model.api.externalUnitRegistration.ExternalUnitRegistrationDetailsRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalUnitRegistrationDTO {

    private Long externalUnitRegistrationId;
    private Long externalUnitTypeId;;
    private String name;
    private String address;
    private String licenseNumber;
    private String externalUnitNumber;
    private String organisationName;
    private Long raceMasterId;
    private String capacity;
    private String externalUnitTypeName;
//    private String virtualAccountNumber;
//    private String ifscCode;
//    private String branchName;
    private String marketMasterName;
    private String lotNumberNomenclature;
    private String raceMasterName;
    private Long tscMasterId;
    private Long districtId;
    private Long talukId;
    private String nameKan;

    private String bankName;
    private String bankAccountNumber;
    private String bankBranchName;
    private String bankIfscCode;

}
