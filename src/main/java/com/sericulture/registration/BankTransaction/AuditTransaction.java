package com.sericulture.registration.BankTransaction;

import com.sericulture.registration.model.entity.BaseEntity;
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
public class AuditTransaction extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUDIT_TRANSACTION_SEQ")
    @SequenceGenerator(name = "AUDIT_TRANSACTION_SEQ", sequenceName = "AUDIT_TRANSACTION_SEQ", allocationSize = 1)
    @Column(name = "AUDIT_TRANSACTION_ID")
    private Long id;


    @Column(name = "AUDIT_REQUEST_BODY")
    private String requestBody;


    @Column(name = "AUDIT_REQUEST_HEADERS")
    private String requestHeaders;


    @Column(name = "AUDIT_RESPONSE")
    private String responseBody;

    @Temporal(TemporalType.DATE)
    @Column(name = "COMMENTS")
    private String comments;

}
