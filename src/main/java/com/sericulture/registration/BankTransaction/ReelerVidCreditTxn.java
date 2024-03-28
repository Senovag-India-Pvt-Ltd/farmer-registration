package com.sericulture.registration.BankTransaction;

import com.sericulture.registration.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "REELER_VID_CREDIT_TXN")
public class ReelerVidCreditTxn extends BaseEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REELER_VID_CREDIT_TXN_SEQ")
    @SequenceGenerator(name = "REELER_VID_CREDIT_TXN_SEQ", sequenceName = "REELER_VID_CREDIT_TXN_SEQ", allocationSize = 1)
    @Column(name = "REELER_VID_CREDIT_TXN_ID")
    private Long reelerVidCreditTxnId;
    private String alertSequenceNo;
    private String remitterName;
    private String remitterAccount;
    private String remitterBank;
    private String userReferenceNumber;
    private String virtualAccount;
    private Double amount;
    private String mnemonicCode;
    private LocalDateTime transactionDate;
    private LocalDate valueDate;
    private String ifscCode;
    private Integer chequeNo;
    private String transactionDescription;
    private String accountNumber;
    private String debitCredit;


}
