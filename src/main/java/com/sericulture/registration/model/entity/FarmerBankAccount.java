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
public class FarmerBankAccount extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FARMER_BANK_ACCOUNT_SEQ")
    @SequenceGenerator(name = "FARMER_BANK_ACCOUNT_SEQ", sequenceName = "FARMER_BANK_ACCOUNT_SEQ", allocationSize = 1)
    @Column(name = "FARMER_BANK_ACCOUNT_ID")
    private Long farmerBankAccountId;

    @Size(min = 2, max = 250, message = "FARMER_BANK_ACCOUNT name should be more than 1 characters.")
    @Column(name = "FARMER_BANK_NAME", unique = true)
    private String farmerBankName;

    @Column(name = "FARMER_BANK_ACCOUNT_NUMBER", unique = true)
    private String farmerBankAccountNumber;

    @Column(name = "FARMER_BANK_BRANCH_NAME", unique = true)
    private String farmerBankBranchName;

    @Column(name = "FARMER_BANK_IFSC_CODE", unique = true)
    private String farmerBankIfscCode;

    @Column(name = "FARMER_ID")
    private Long farmerId;
}