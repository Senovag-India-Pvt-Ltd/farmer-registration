package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FarmerAddress extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FARMER_ADDRESS_SEQ")
    @SequenceGenerator(name = "FARMER_ADDRESS_SEQ", sequenceName = "FARMER_ADDRESS_SEQ", allocationSize = 1)
    @Column(name = "FARMER_ADDRESS_ID")
    private Long farmerAddressId;

    @Column(name = "FARMER_ID")
    private Long farmerId;

    @Column(name = "STATE_ID")
    private Long stateId;

    @Column(name = "DISTRICT_ID")
    private Long districtId;

    @Column(name = "TALUK_ID")
    private Long talukId;

    @Column(name = "HOBLI_ID")
    private Long hobliId;

    @Column(name = "VILLAGE_ID")
    private Long villageId;

    @Column(name = "ADDRESS_TEXT")
    private String addressText;

    @Column(name = "PINCODE")
    private String pincode;

    @Column(name = "DEFAULT_ADDRESS", columnDefinition = "TINYINT")
    private Boolean defaultAddress;

    @Column(name = "district_name")
    private String district;

    @Column(name = "taluk_name")
    private String taluk;

    @Column(name = "village_name")
    private String village;

}