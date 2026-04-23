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
public class EuVirtualBankAccount extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eu_virtual_bank_account_seq")
    @SequenceGenerator(name = "eu_virtual_bank_account_seq", sequenceName = "eu_virtual_bank_account_seq", allocationSize = 1)
    @Column(name = "eu_virtual_bank_account_id")
    private Long id;

    @Column(name = "eu_id")
    private Long euId;

    @Column(name = "virtual_account_number")
    private String virtualAccountNumber;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "market_master_id")
    private Long marketMasterId;

    @Column(name = "lock")
    private Boolean lock;

    @Column(name = "active")
    private Boolean active;
}
