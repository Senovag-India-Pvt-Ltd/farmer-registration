package com.sericulture.registration.model.dto.reeler;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReelerDTO {

    private Long reelerId;
    private String reelerName;
    private String wardNumber;
    private String passbookNumber;
    private String fatherName;
    private Long educationId;
    private String reelingUnitBoundary;
    private Date dob;
    private String rationCard;
    private Long machineTypeId;
    private int gender;
    private Date dateOfMachineInstallation;
    private String electricityRrNumber;
    private Long casteId;
    private String revenueDocument;
    private int numberOfBasins;
    private String mobileNumber;
    private String recipientId;
    private String mahajarDetails;
    private String emailId;
    private String representativeNameAddress;
    private String loanDetails;
    private Long assignToInspectId;
    private String gpsLat;
    private String gpsLng;
    private Date inspectionDate;
    private String arnNumber;
    private String chakbandiLat;
    private String chakbandiLng;
    private String address;
    private String pincode;
    private Long stateId;
    private Long districtId;
    private Long talukId;
    private Long hobliId;
    private Long villageId;
    private String licenseReceiptNumber;
    private Date licenseExpiryDate;
    private Date receiptDate;
    private int functionOfUnit;
    private String reelingLicenseNumber;
    private Double feeAmount;
    private String memberLoanDetails;
    private String mahajarEast;
    private String mahajarWest;
    private String mahajarNorth;
    private String mahajarSouth;
    private String mahajarNorthEast;
    private String mahajarNorthWest;
    private String mahajarSouthEast;
    private String mahajarSouthWest;
    private String bankName;
    private String bankAccountNumber;
    private String branchName;
    private String ifscCode;
    private int status;
    private Date licenseRenewalDate;
    private String fruitsId;
    private String title;
    private String name;
    private String machineTypeName;
    private String stateName;
    private String districtName;
    private String talukName;
    private String hobliName;
    private String villageName;
    private int isActivated;
    private Double walletAmount;
    private String reelerNumber;
    private int reelerTypeMasterId;
    private String reelerTypeMasterName;
}
