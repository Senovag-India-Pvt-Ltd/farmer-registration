package com.sericulture.registration.sevasindhu;

import com.sericulture.registration.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity(name = "sc_sevasindhu_integration_log")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SevaSindhuIntegrationLog extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sc_sevasindhu_integration_log_seq")
    @SequenceGenerator(name = "sc_sevasindhu_integration_log_seq", sequenceName = "sc_sevasindhu_integration_log_seq", allocationSize = 1)
    @Column(name = "sc_sevasindhu_integration_log_id")
    private Long scSevasindhuIntegrationLogId;

    @Column(name = "seva_sindhu_reference")
    private String sevaSindhuReference;

    @Column(name = "endpoint_name")
    private String endpointName;

    @Column(name = "request_payload", columnDefinition = "VARCHAR(MAX)")
    private String requestPayload;

    @Column(name = "response_payload", columnDefinition = "VARCHAR(MAX)")
    private String responsePayload;

    @Column(name = "status")
    private String status;
}
