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
public class TraderLicense extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRADER_LICENSE_SEQ")
    @SequenceGenerator(name = "TRADER_LICENSE_SEQ", sequenceName = "TRADER_LICENSE_SEQ", allocationSize = 1)
    @Column(name = "TRADER_LICENSE_ID")
    private Long traderLicenseId;

    @Column(name = "arn_number")
    private String arnNumber;

    @Column(name = "trader_type_id")
    private Long traderTypeMasterId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "address")
    private String address;

    @Column(name = "premises_description")
    private String premisesDescription;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "application_date")
    private Date applicationDate;

    @Column(name = "application_number")
    private String applicationNumber;

    @Column(name = "trader_license_number")
    private String traderLicenseNumber;

    @Column(name = "representative_details")
    private String representativeDetails;

    @Column(name = "license_fee")
    private Double licenseFee;

    @Column(name = "license_challan_number")
    private String licenseChallanNumber;

    @Column(name = "godown_details")
    private String godownDetails;

    @Column(name = "silk_exchange_mahajar")
    private String silkExchangeMahajar;

    @Column(name = "license_number_sequence")
    private Long licenseNumberSequence;
}