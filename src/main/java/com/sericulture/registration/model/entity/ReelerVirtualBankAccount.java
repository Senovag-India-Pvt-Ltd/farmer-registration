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
public class ReelerVirtualBankAccount extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REELER_VIRTUAL_BANK_ACCOUNT_SEQ")
    @SequenceGenerator(name = "REELER_VIRTUAL_BANK_ACCOUNT_SEQ", sequenceName = "REELER_VIRTUAL_BANK_ACCOUNT_SEQ", allocationSize = 1)
    @Column(name = "REELER_VIRTUAL_BANK_ACCOUNT_ID")
    private Long reelerVirtualBankAccountId;

    @Column(name = "reeler_id")
    private Long reelerId;

    @Column(name = "virtual_account_number")
    private String virtualAccountNumber;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "market_master_id")
    private Long marketMasterId;
}
