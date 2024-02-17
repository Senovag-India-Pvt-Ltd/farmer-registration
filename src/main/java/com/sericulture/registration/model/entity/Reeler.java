package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Reeler extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REELER_SEQ")
    @SequenceGenerator(name = "REELER_SEQ", sequenceName = "REELER_SEQ", allocationSize = 1)
    @Column(name = "REELER_ID")
    private Long reelerId;

    @Column(name = "name")
    private String reelerName;

    @Column(name = "ward_number")
    private String wardNumber;

    @Column(name = "passbook_number")
    private String passbookNumber;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "education_id")
    private Long educationId;

    @Column(name = "reeling_unit_boundary")
    private String reelingUnitBoundary;

    @Column(name = "DOB")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dob;

    @Column(name = "ration_card")
    private String rationCard;

    @Column(name = "machine_type_id")
    private Long machineTypeId;

    @Column(name = "gender")
    private int gender;

    @Column(name = "date_of_machine_installation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfMachineInstallation;

    @Column(name = "electricity_rr_number")
    private String electricityRrNumber;

    @Column(name = "caste_id")
    private Long casteId;

    @Column(name = "revenue_document")
    private String revenueDocument;

    @Column(name = "number_of_basins")
    private int numberOfBasins;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "recipient_id")
    private String recipientId;

    @Column(name = "mahajar_details")
    private String mahajarDetails;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "representative_name_address")
    private String representativeNameAddress;

    @Column(name = "loan_details")
    private String loanDetails;

    @Column(name = "assign_to_inspect_id")
    private Long assignToInspectId;

    @Column(name = "gps_lat")
    private String gpsLat;

    @Column(name = "gps_lng")
    private String gpsLng;

    @Column(name = "inspection_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inspectionDate;

    @Column(name = "arn_number")
    private String arnNumber;

    @Column(name = "chakbandi_lat")
    private String chakbandiLat;

    @Column(name = "chakbandi_lng")
    private String chakbandiLng;

    @Column(name = "address")
    private String address;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "taluk_id")
    private Long talukId;

    @Column(name = "hobli_id")
    private Long hobliId;

    @Column(name = "village_id")
    private Long villageId;

    @Column(name = "license_receipt_number")
    private String licenseReceiptNumber;

    @Column(name = "license_expiry_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date licenseExpiryDate;

    @Column(name = "receipt_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date receiptDate;

    @Column(name = "function_of_unit")
    private int functionOfUnit;

    @Column(name = "reeling_license_number")
    private String reelingLicenseNumber;

    @Column(name = "fee_amount")
    private Double feeAmount;

    @Column(name = "member_loan_details")
    private String memberLoanDetails;

    @Column(name = "mahajar_east")
    private String mahajarEast;

    @Column(name = "mahajar_west")
    private String mahajarWest;

    @Column(name = "mahajar_north")
    private String mahajarNorth;

    @Column(name = "mahajar_south")
    private String mahajarSouth;

    @Column(name = "mahajar_north_east")
    private String mahajarNorthEast;

    @Column(name = "mahajar_north_west")
    private String mahajarNorthWest;

    @Column(name = "mahajar_south_east")
    private String mahajarSouthEast;

    @Column(name = "mahajar_south_west")
    private String mahajarSouthWest;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "status") // 0 - Pending, 1 - Active
    private int status;

    @Column(name = "license_renewal_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date licenseRenewalDate;

    @Column(name = "fruits_id")
    private String fruitsId;

    @Column(name = "is_activated") //
    private int isActivated;

    @Column(name = "wallet_amount")
    private Double walletAmount;

    @Column(name = "reeler_number")
    private String reelerNumber;

    @Column(name = "reeler_type_master_id")
    private int reelerTypeMasterId;
}