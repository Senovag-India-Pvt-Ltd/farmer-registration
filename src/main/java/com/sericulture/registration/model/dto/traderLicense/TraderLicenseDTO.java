package com.sericulture.registration.model.dto.traderLicense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraderLicenseDTO {
    private Long traderLicenseId;
    private String arnNumber;
    private Long traderTypeMasterId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fatherName;
    private Long stateId;
    private Long districtId;
    private String address;
    private String premisesDescription;
    private Date applicationDate;
    private String applicationNumber;
    private String traderLicenseNumber;
    private String representativeDetails;
    private Double licenseFee;
    private String licenseChallanNumber;
    private String godownDetails;
    private String silkExchangeMahajar;
    private Long licenseNumberSequence;
    private String traderTypeMasterName;
    private String stateName;
    private String districtName;
}
