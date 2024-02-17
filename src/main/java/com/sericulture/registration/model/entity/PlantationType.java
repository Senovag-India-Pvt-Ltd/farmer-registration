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
public class PlantationType extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plantation_type_seq")
    @SequenceGenerator(name = "plantation_type_seq", sequenceName = "plantation_type_seq", allocationSize = 1)
    @Column(name = "plantation_type_id")
    private Long plantationTypeId;

    @Size(min = 2, max = 250, message = "Plantation Type variety name should be more than 1 characters.")
    @Column(name = " plantation_type_name", unique = true)
    private String plantationTypeName;

    @Column(name = "plantation_type_name_in_kannada")
    private String plantationTypeNameInKannada;
}
