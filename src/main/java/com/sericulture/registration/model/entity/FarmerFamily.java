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
public class FarmerFamily extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FARMER_FAMILY_SEQ")
    @SequenceGenerator(name = "FARMER_FAMILY_SEQ", sequenceName = "FARMER_FAMILY_SEQ", allocationSize = 1)
    @Column(name = "FARMER_FAMILY_ID")
    private Long farmerFamilyId;

    @Size(min = 2, max = 250, message = "FARMER_FAMILY name should be more than 1 characters.")
    @Column(name = "FARMER_FAMILY_NAME")
    private String farmerFamilyName;

    @Column(name = "RELATIONSHIP_ID")
    private Long relationshipId;

    @Column(name = "FARMER_ID")
    private Long farmerId;
}