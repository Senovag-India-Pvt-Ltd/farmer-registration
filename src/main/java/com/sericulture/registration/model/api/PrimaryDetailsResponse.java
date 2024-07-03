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
public class PrimaryDetailsResponse extends ResponseBody{
    private String farmerId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fruitsId;
    private String farmerNumber;
    private String fatherName;
    private String passbookNumber;
    private String epicNumber;
    private String rationCardNumber;
    private String dob;
    private String districtName;
    private String talukName;
    private String hobliName;
    private String villageName;
    private String farmerBankName;
    private String farmerBankAccountNumber;
    private String farmerBankBranchName;
    private String farmerBankIfscCode;
}
