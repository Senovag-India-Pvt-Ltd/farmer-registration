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
public class SoilType extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "soil_type_seq")
    @SequenceGenerator(name = "soil_type_seq", sequenceName = "soil_type_seq", allocationSize = 1)
    @Column(name = "soil_type_id")
    private Long soilTypeId;


    @Size(min = 2, max = 250, message = "SoilType name should be more than 1 characters.")
    @Column(name = " soil_type_name", unique = true)
    private String soilTypeName;

    @Column(name = "soil_type_name_in_kannada")
    private String soilTypeNameInKannada;
}
