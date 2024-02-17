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
public class ReelerLicenseTransaction extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REELER_LICENSE_TRANSACTION_SEQ")
    @SequenceGenerator(name = "REELER_LICENSE_TRANSACTION_SEQ", sequenceName = "REELER_LICENSE_TRANSACTION_SEQ", allocationSize = 1)
    @Column(name = "REELER_LICENSE_TRANSACTION_ID")
    private Long reelerLicenseTransactionId;

    @Column(name = "REELER_ID")
    private Long reelerId;

    @Column(name = "FEE_AMOUNT")
    private Double feeAmount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "renewed_date")
    private Date renewedDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiration_date")
    private Date expirationDate;
}