package com.sericulture.registration.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrimaryReelerDetailsResponse extends ResponseBody{
    private int serialNumber;
    private String reelerId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fruitsId;
    private String reelerLicenseNumber;
    private String reelerNumber;
    private String fatherName;
    private String passbookNumber;
    private String epicNumber;
    private String rationCardNumber;
    private String dob;
    private String districtName;
    private String talukName;
    private String hobliName;
    private String villageName;
    private String reelerBankName;
    private String reelerBankAccountNumber;
    private String reelerBankBranchName;
    private String reelerBankIfscCode;
    private Long reelerMobileNumber;
}
