package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FarmerVirtualBankAccountAudit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FARMER_VIRTUAL_BANK_ACCOUNT_AUDIT_SEQ")
    @SequenceGenerator(name = "FARMER_VIRTUAL_BANK_ACCOUNT_AUDIT_SEQ", sequenceName = "FARMER_VIRTUAL_BANK_ACCOUNT_AUDIT_SEQ", allocationSize = 1)
    @Column(name = "farmer_virtual_bank_account_audit_id")
    private Long farmerVirtualBankAccountAuditId;

    @Column(name = "farmer_virtual_bank_account_id")
    private Long farmerVirtualBankAccountId;

    @Column(name = "action")
    private String action;

    @Column(name = "action_by")
    private String actionBy;

    @Column(name = "action_date")
    private Date actionDate;

    @PrePersist
    public void prePersist() {
        this.actionDate = new Date();
        this.actionBy = SecurityContextHolder.getContext().getAuthentication().getName();
    }
}