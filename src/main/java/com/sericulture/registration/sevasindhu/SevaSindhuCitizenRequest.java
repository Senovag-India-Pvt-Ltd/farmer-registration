package com.sericulture.registration.sevasindhu;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * The Seva Sindhu request row created by dbt (URL 1). Both services use the same database; this module only
 * reads the citizen session and links the registered farmer. Only the columns this module needs are mapped
 * (the Seva Sindhu Token is deliberately not mapped here). Table: dbt db-scripts/ddl/V007__sc_sevasindhu_request.sql
 */
@Entity(name = "SevaSindhuCitizenRequest")
@Table(name = "sc_sevasindhu_request")
@NoArgsConstructor
@Getter
@Setter
public class SevaSindhuCitizenRequest {

    public static final String STATUS_RECEIVED = "RECEIVED";
    public static final String STATUS_FARMER_REGISTERED = "FARMER_REGISTERED";
    public static final String STATUS_SUBMITTED = "SUBMITTED";

    @Id
    @Column(name = "sc_sevasindhu_request_id")
    private Long id;

    @Column(name = "edcs_application_ref_no", insertable = false, updatable = false)
    private String edcsApplicationRefNo;

    @Column(name = "assisted_mode", columnDefinition = "TINYINT", insertable = false, updatable = false)
    private Boolean assistedMode;

    @Column(name = "mobile_number", insertable = false, updatable = false)
    private String mobileNumber;

    @Column(name = "service_id", insertable = false, updatable = false)
    private String serviceId;

    @Column(name = "request_status")
    private String requestStatus;

    @Column(name = "farmer_id")
    private Long farmerId;

    @Column(name = "fruits_id")
    private String fruitsId;

    @Column(name = "session_hash", insertable = false, updatable = false)
    private String sessionHash;

    @Column(name = "session_expires_at")
    private Date sessionExpiresAt;

    @Column(name = "MODIFIED_DATE")
    private Date modifiedDate;

    @Column(name = "ACTIVE", columnDefinition = "TINYINT", insertable = false, updatable = false)
    private Boolean active;
}
