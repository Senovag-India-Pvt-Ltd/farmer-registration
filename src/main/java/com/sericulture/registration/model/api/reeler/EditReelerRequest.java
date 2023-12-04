package com.sericulture.registration.model.api.reeler;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class EditReelerRequest extends RequestBody {
    @Schema(name = "reelerId", example = "1")
    Long reelerId;

    @Schema(name = "name", example = "Test")
    String reelerName;

    @Schema(name = "wardNumber", example = "Test")
    String wardNumber;

    @Schema(name = "passbookNumber", example = "Test")
    String passbookNumber;

    @Schema(name = "fatherName", example = "Test")
    String fatherName;

    @Schema(name = "educationId", example = "1")
    Long educationId;

    @Schema(name = "reelingUnitBoundary", example = "Test")
    String reelingUnitBoundary;

    @Schema(name = "dob", example = "2023-11-03 16:27:35.907")
    Date dob;

    @Schema(name = "rationCard", example = "Test")
    String rationCard;

    @Schema(name = "machineTypeId", example = "1")
    Long machineTypeId;

    @Schema(name = "gender", example = "1")
    int gender;

    @Schema(name = "dateOfMachineInstallation", example = "2023-11-03 16:27:35.907")
    Date dateOfMachineInstallation;

    @Schema(name = "electricityRrNumber", example = "Test")
    String electricityRrNumber;

    @Schema(name = "casteId", example = "1")
    Long casteId;

    @Schema(name = "revenueDocument", example = "Test")
    String revenueDocument;

    @Schema(name = "numberOfBasins", example = "1")
    int numberOfBasins;

    @Schema(name = "mobileNumber", example = "675687787")
    String mobileNumber;

    @Schema(name = "recipientId", example = "675687787")
    String recipientId;

    @Schema(name = "mahajarDetails", example = "Test")
    String mahajarDetails;

    @Schema(name = "emailId", example = "Test@xyz.com")
    String emailId;

    @Schema(name = "emailId", example = "Test")
    String representativeNameAddress;

    @Schema(name = "loanDetails", example = "Test")
    String loanDetails;

    @Schema(name = "assignToInspectId", example = "1")
    Long assignToInspectId;

    @Schema(name = "gpsLat", example = "123.454")
    String gpsLat;

    @Schema(name = "gpsLng", example = "34.565")
    String gpsLng;

    @Schema(name = "inspectionDate", example = "2023-11-03 16:27:35.907")
    Date inspectionDate;

    @Schema(name = "arnNumber", example = "34")
    String arnNumber;

    @Schema(name = "chakbandiLat", example = "123.454")
    String chakbandiLat;

    @Schema(name = "chakbandiLng", example = "34.565")
    String chakbandiLng;

    @Schema(name = "address", example = "Test")
    String address;

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

    @Schema(name = "licenseReceiptNumber", example = "34534")
    String licenseReceiptNumber;

    @Schema(name = "licenseExpiryDate", example = "2023-11-03 16:27:35.907")
    Date licenseExpiryDate;

    @Schema(name = "receiptDate", example = "2023-11-03 16:27:35.907")
    Date receiptDate;

    @Schema(name = "functionOfUnit", example = "1")
    int functionOfUnit;

    @Schema(name = "reelingLicenseNumber", example = "34534")
    String reelingLicenseNumber;

    @Schema(name = "feeAmount", example = "15.67")
    double feeAmount;

    @Schema(name = "memberLoanDetails", example = "34534")
    String memberLoanDetails;

    @Schema(name = "mahajarEast", example = "34534")
    String mahajarEast;

    @Schema(name = "mahajarWest", example = "34534")
    String mahajarWest;

    @Schema(name = "mahajarNorth", example = "34534")
    String mahajarNorth;

    @Schema(name = "mahajarSouth", example = "34534")
    String mahajarSouth;

    @Schema(name = "mahajarNorthEast", example = "34534")
    String mahajarNorthEast;

    @Schema(name = "mahajarNorthWest", example = "34534")
    String mahajarNorthWest;

    @Schema(name = "mahajarSouthEast", example = "34534")
    String mahajarSouthEast;

    @Schema(name = "mahajarSouthWest", example = "34534")
    String mahajarSouthWest;

    @Schema(name = "bankName", example = "SBI")
    String bankName;

    @Schema(name = "bankAccountNumber", example = "34575464534")
    String bankAccountNumber;

    @Schema(name = "branchName", example = "Bengaluru")
    String branchName;

    @Schema(name = "ifscCode", example = "SBI3748")
    String ifscCode;

    @Schema(name = "status", example = "1")
    int status;

    @Schema(name = "licenseRenewalDate", example = "2023-11-03 16:27:35.907")
    Date licenseRenewalDate;

    @Schema(name = "fruitsId", example = "35")
    String fruitsId;
}