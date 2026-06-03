package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
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
public class FarmerVirtualBankAccount extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FARMER_VIRTUAL_BANK_ACCOUNT_SEQ")
    @SequenceGenerator(name = "FARMER_VIRTUAL_BANK_ACCOUNT_SEQ", sequenceName = "FARMER_VIRTUAL_BANK_ACCOUNT_SEQ", allocationSize = 1)
    @Column(name = "FARMER_VIRTUAL_BANK_ACCOUNT_ID")
    private Long farmerVirtualBankAccountId;

    @Column(name = "farmer_id")
    private Long farmerId;

    @Column(name = "virtual_account_number")
    private String virtualAccountNumber;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "market_master_id")
    private Long marketMasterId;

    @Column(name = "is_locked")
    private Boolean isLocked;
}