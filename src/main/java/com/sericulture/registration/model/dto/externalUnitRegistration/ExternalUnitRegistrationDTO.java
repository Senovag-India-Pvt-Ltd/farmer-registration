package com.sericulture.registration.model.dto.externalUnitRegistration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private String externalUnitTypeName;
    private String raceMasterName;
}
