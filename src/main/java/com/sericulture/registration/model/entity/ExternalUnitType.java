package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity(name = "external_unit_type_master")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExternalUnitType extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "external_unit_type_seq")
    @SequenceGenerator(name = "external_unit_type_seq", sequenceName = "external_unit_type_seq", allocationSize = 1)
    @Column(name = "external_unit_type_id")
    private Long externalUnitTypeId;


    @Size(min = 2, max = 250, message = "External Unit Type name should be more than 1 characters.")
    @Column(name = "external_unit_type_name", unique = true)
    private String externalUnitTypeName;
}
