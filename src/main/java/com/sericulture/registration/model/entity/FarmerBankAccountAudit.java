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
public class FarmerBankAccountAudit extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "farmer_bank_account_audit_seq")
    @SequenceGenerator(name = "farmer_bank_account_audit_seq", sequenceName = "farmer_bank_account_audit_seq", allocationSize = 1)

    @Column(name = "farmer_bank_account_audit_id")
    private Long farmerBankAccountAuditId;

    @Column(name = "FARMER_BANK_ACCOUNT_ID")
    private Long farmerBankAccountId;

    @Size(min = 2, max = 250, message = "FARMER_BANK_ACCOUNT name should be more than 1 characters.")
    @Column(name = "FARMER_BANK_NAME")
    private String farmerBankName;

    @Column(name = "FARMER_BANK_ACCOUNT_NUMBER")
    private String farmerBankAccountNumber;

    @Column(name = "FARMER_BANK_BRANCH_NAME")
    private String farmerBankBranchName;

    @Column(name = "FARMER_BANK_IFSC_CODE")
    private String farmerBankIfscCode;

    @Column(name = "FARMER_ID")
    private Long farmerId;

    @Column(name = "reason_master_id")
    private Long reasonMasterId;

    @Column(name = "remark")
    private String remark;

}
