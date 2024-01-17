package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity(name = "machine_type_master")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MachineType extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "machine_type_seq")
    @SequenceGenerator(name = "machine_type_seq", sequenceName = "machine_type_seq", allocationSize = 1)
    @Column(name = "machine_type_id")
    private Long machineTypeId;

    @Size(min = 2, max = 250, message = "Machine type name should be more than 1 characters.")
    @Column(name = "machine_type_name", unique = true)
    private String machineTypeName;

    @Column(name = "machine_type_name_in_kannada")
    private String machineTypeNameInKannada;

}
