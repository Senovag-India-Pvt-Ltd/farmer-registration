package com.sericulture.registration.model.dto.farmer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FarmerDTO {
    private Long farmerId;
    private String farmerNumber;
    private String fruitsId;
    private String firstName;
    private String middleName;
    private String lastName;
    private Date dob;
    private Long genderId;
    private Long casteId;
    private Long tscMasterId;
    private Boolean differentlyAbled;
    private String email;
    private String mobileNumber;
    private String epicNumber;
    private String rationCardNumber;
    private String totalLandHolding;
    private String passbookNumber;
    private Long landCategoryId;
    private Long educationId;
    private Long representativeId;
    private String khazaneRecipientId;
    private String photoPath;
    private Long farmerTypeId;
    private String minority;
    private String rdNumber;
    private String casteStatus;
    private String genderStatus;
    private String fatherNameKan;
    private String fatherName;
    private String nameKan;
    private String title;
    private String landCategoryName;
    private String farmerTypeName;
    private String tscName;
    private String name;
    private Boolean isOtherStateFarmer;
    private String farmerBankAccountNumber;




}
