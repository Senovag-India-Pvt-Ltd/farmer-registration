package com.sericulture.registration.model.api.reeler;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ReelerRequest extends RequestBody {

    @Schema(name = "tscMasterId", example = "1")
    Long tscMasterId;

    @Pattern(regexp = "^[a-zA-Z0-9._@ ]*$", message = "Reeler name must contain only letters and numbers")
    @Schema(name = "reelerName", example = "Test")
    String reelerName;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Ward number must contain only letters and numbers")
    @Schema(name = "wardNumber", example = "Test")
    String wardNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Passbook number must contain only letters and numbers")
    @Schema(name = "passbookNumber", example = "Test")
    String passbookNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Father name must contain only letters and numbers")
    @Schema(name = "fatherName", example = "Test")
    String fatherName;

    @Schema(name = "educationId", example = "1")
    Long educationId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Reeling unit boundary must contain only letters and numbers")
    @Schema(name = "reelingUnitBoundary", example = "Test")
    String reelingUnitBoundary;

    @Schema(name = "dob", example = "2023-11-03 16:27:35.907")
    Date dob;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Ration card must contain only letters and numbers")
    @Schema(name = "rationCard", example = "Test")
    String rationCard;

    @Schema(name = "machineTypeId", example = "1")
    Long machineTypeId;

    @Schema(name = "gender", example = "1")
    int gender;

    @Schema(name = "dateOfMachineInstallation", example = "2023-11-03 16:27:35.907")
    Date dateOfMachineInstallation;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Electricity RR number must contain only letters and numbers")
    @Schema(name = "electricityRrNumber", example = "Test")
    String electricityRrNumber;

    @Schema(name = "casteId", example = "1")
    Long casteId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Revenue document must contain only letters and numbers")
    @Schema(name = "revenueDocument", example = "Test")
    String revenueDocument;

    @Schema(name = "numberOfBasins", example = "1")
    int numberOfBasins;

    @Pattern(regexp = "^[+0-9\\s]*$", message = "Mobile number must contain only numbers")
    @Schema(name = "mobileNumber", example = "675687787")
    String mobileNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Recepient Id must contain only letters and numbers")
    @Schema(name = "recipientId", example = "675687787")
    String recipientId;

//    @Pattern(regexp = "^[a-zA-Z0-9/-_. \\\\s]*$", message = "Mahajar details document should not contain special characters")
    @Pattern(regexp = "^[a-zA-Z0-9-/_.\\s]*$", message = "Mahajar Document must contain only letters and numbers")
    @Schema(name = "mahajarDetails", example = "Test")
    String mahajarDetails;

    @Pattern(regexp = "^[a-zA-Z0-9.@\\s]*$", message = "Email id must contain only letters and numbers")
    @Schema(name = "emailId", example = "Test@xyz.com")
    String emailId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "representativeNameAddress must contain only letters and numbers")
    @Schema(name = "representativeNameAddress", example = "Test")
    String representativeNameAddress;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Loan details must contain only letters and numbers")
    @Schema(name = "loanDetails", example = "Test")
    String loanDetails;

    @Schema(name = "assignToInspectId", example = "1")
    Long assignToInspectId;

    @Pattern(regexp = "^[a-zA-Z0-9.\\s]*$", message = "GPS lat must contain only letters and numbers")
    @Schema(name = "gpsLat", example = "123.454")
    String gpsLat;

    @Pattern(regexp = "^[a-zA-Z0-9.\\s]*$", message = "GPS Lng must contain only letters and numbers")
    @Schema(name = "gpsLng", example = "34.565")
    String gpsLng;

    @Schema(name = "inspectionDate", example = "2023-11-03 16:27:35.907")
    Date inspectionDate;

    @Pattern(regexp = "^[a-zA-Z0-9-/\\s]*$", message = "ARN Number must contain only letters and numbers")
    @Schema(name = "arnNumber", example = "34")
    String arnNumber;

    @Pattern(regexp = "^[a-zA-Z0-9.\\s]*$", message = "ChakBandi Lat must contain only letters and numbers")
    @Schema(name = "chakbandiLat", example = "123.454")
    String chakbandiLat;

    @Pattern(regexp = "^[a-zA-Z0-9.\\s]*$", message = "Chakbandi lng must contain only letters and numbers")
    @Schema(name = "chakbandiLng", example = "34.565")
    String chakbandiLng;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Address must contain only letters and numbers")
    @Schema(name = "address", example = "Test")
    String address;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Pincode must contain only letters and numbers")
    @Schema(name = "pincode", example = "324735")
    String pincode;

    @Schema(name = "stateId", example = "1")
    Long stateId;

    @Schema(name = "districtId", example = "1")
    Long districtId;

    @Schema(name = "talukId", example = "1")
    Long talukId;

    @Schema(name = "hobliId", example = "1")
    Long hobliId;

    @Schema(name = "villageId", example = "1")
    Long villageId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "License receipt number must contain only letters and numbers")
    @Schema(name = "licenseReceiptNumber", example = "34534")
    String licenseReceiptNumber;

    @Schema(name = "licenseExpiryDate", example = "2023-11-03 16:27:35.907")
    Date licenseExpiryDate;

    @Schema(name = "receiptDate", example = "2023-11-03 16:27:35.907")
    Date receiptDate;

    @Schema(name = "functionOfUnit", example = "1")
    int functionOfUnit;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Reeling license number must contain only letters and numbers")
    @Schema(name = "reelingLicenseNumber", example = "34534")
    String reelingLicenseNumber;

    @Schema(name = "feeAmount", example = "15.67")
    double feeAmount;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Member loan detials must contain only letters and numbers")
    @Schema(name = "memberLoanDetails", example = "34534")
    String memberLoanDetails;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Mahajar east must contain only letters and numbers")
    @Schema(name = "mahajarEast", example = "34534")
    String mahajarEast;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Mahajar west must contain only letters and numbers")
    @Schema(name = "mahajarWest", example = "34534")
    String mahajarWest;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Mahajar north must contain only letters and numbers")
    @Schema(name = "mahajarNorth", example = "34534")
    String mahajarNorth;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Mahajar south must contain only letters and numbers")
    @Schema(name = "mahajarSouth", example = "34534")
    String mahajarSouth;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Mahajar north east must contain only letters and numbers")
    @Schema(name = "mahajarNorthEast", example = "34534")
    String mahajarNorthEast;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Mahajar north west must contain only letters and numbers")
    @Schema(name = "mahajarNorthWest", example = "34534")
    String mahajarNorthWest;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Mahajar south east must contain only letters and numbers")
    @Schema(name = "mahajarSouthEast", example = "34534")
    String mahajarSouthEast;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Mahajar south west must contain only letters and numbers")
    @Schema(name = "mahajarSouthWest", example = "34534")
    String mahajarSouthWest;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Bank Name must contain only letters and numbers")
    @Schema(name = "bankName", example = "SBI")
    String bankName;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Bank account number must contain only letters and numbers")
    @Schema(name = "bankAccountNumber", example = "34575464534")
    String bankAccountNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Branch name must contain only letters and numbers")
    @Schema(name = "branchName", example = "Bengaluru")
    String branchName;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "IFSC code must contain only letters and numbers")
    @Schema(name = "ifscCode", example = "SBI3748")
    String ifscCode;

    @Schema(name = "status", example = "1")
    int status;

    @Schema(name = "licenseRenewalDate", example = "2023-11-03 16:27:35.907")
    Date licenseRenewalDate;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Fruits id must contain only letters and numbers")
    @Schema(name = "fruitsId", example = "35")
    String fruitsId;

    @Schema(name = "isActivated", example = "0")
    int isActivated;

    @Schema(name = "walletAmount", example = "15.67")
    double walletAmount;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Reeler number must contain only letters and numbers")
    @Schema(name = "reelerNumber", example = "3674637")
    String reelerNumber;

    @Schema(name = "reelerTypeMasterId", example = "1")
    Long reelerTypeMasterId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s\\u0C80-\\u0CFF]*$", message = "Reeler name in kannada must contain only letters and numbers")
    @Schema(name = "reelerNameKannada", example = "3674637")
    String reelerNameKannada;

    @Schema(name = "transferReelerId", example = "1")
    int transferReelerId;

    @Schema(name = "inspectorId", example = "1")
    Long inspectorId;
}