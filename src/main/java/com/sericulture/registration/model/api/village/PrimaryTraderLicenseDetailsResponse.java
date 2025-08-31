package com.sericulture.registration.model.api.village;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sericulture.registration.model.api.ResponseBody;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrimaryTraderLicenseDetailsResponse extends ResponseBody {

    private int serialNumber;               // For pagination serial #
    private Long traderLicenseId;
    private String arnNumber;
    private Long traderTypeMasterId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fatherName;
    private Long stateId;
    private Long districtId;
    private String districtName;
    private String address;
    private String premisesDescription;
    private Date applicationDate;      // ✅ Correct: LocalDate (not java.util.Date)
    private String applicationNumber;
    private String traderLicenseNumber;
    private String representativeDetails;
    private Double licenseFee;
    private String silkType;
    private String licenseChallanNumber;
    private String godownDetails;
    private String silkExchangeMahajar;
    private Long licenseNumberSequence;     // ✅ Correct: should be Long (not String)
    private String traderTypeMasterName;
    private String stateName;
    private String marketMasterName;
    private Long marketMasterId;
    private Double walletAmount;
    private String mobileNumber;
    private String virtualAccountNumber;
    private String ifscCode;
    private String branchName;

    // Optional extra fields if you need error info
    private Boolean error;
    private String errorDescription;

    private List<Long> traderLicenseIds;   // If needed for bulk ops
}
