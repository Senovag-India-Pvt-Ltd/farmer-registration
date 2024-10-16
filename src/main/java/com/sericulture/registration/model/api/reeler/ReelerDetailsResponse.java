package com.sericulture.registration.model.api.reeler;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Builder

public class ReelerDetailsResponse {
    private Long reelerId;
    private String name;
    private String passbookNumber;
    private String fatherName;
    private LocalDate dob;
    private String gender;
    private String casteTitle;
    private String mobileNumber;
    private String arnNumber;
    private String stateName;
    private String districtName;
    private String talukName;
    private String hobliName;
    private String villageName;
    private String address;
    private String pincode;
    private String virtualAccountNumber;
    private String reelerLicenseNumber;
    private String reelerNumber;
    private String fruitsId;
}
