package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Authorised representative / agent of a reeler (SR-013). The number of active representatives per reeler is
 * capped by REELER_MAX_REPRESENTATIVES (see ReelerRepresentativeService). Table: db-scripts/ddl/reeler_representative.sql
 */
@Entity
@Table(name = "reeler_representative")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReelerRepresentative extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REELER_REPRESENTATIVE_SEQ")
    @SequenceGenerator(name = "REELER_REPRESENTATIVE_SEQ", sequenceName = "REELER_REPRESENTATIVE_SEQ", allocationSize = 1)
    @Column(name = "reeler_representative_id")
    private Long reelerRepresentativeId;

    @Column(name = "reeler_id", nullable = false)
    private Long reelerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "relationship_id")
    private Long relationshipId;

    @Column(name = "id_proof_type")
    private String idProofType;

    @Column(name = "id_proof_number")
    private String idProofNumber;
}
