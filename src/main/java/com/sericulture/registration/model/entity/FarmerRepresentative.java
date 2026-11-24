package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Authorised representative / agent of a farmer (SR-005). The number of active agents per farmer is capped by
 * FARMER_MAX_REPRESENTATIVES (see FarmerRepresentativeService). Table: db-scripts/ddl/farmer_representative.sql
 */
@Entity
@Table(name = "farmer_representative")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FarmerRepresentative extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FARMER_REPRESENTATIVE_SEQ")
    @SequenceGenerator(name = "FARMER_REPRESENTATIVE_SEQ", sequenceName = "FARMER_REPRESENTATIVE_SEQ", allocationSize = 1)
    @Column(name = "farmer_representative_id")
    private Long farmerRepresentativeId;

    @Column(name = "farmer_id", nullable = false)
    private Long farmerId;

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

    @Column(name = "authorised_from", nullable = false)
    private LocalDate authorisedFrom;

    @Column(name = "authorised_to")
    private LocalDate authorisedTo;
}
