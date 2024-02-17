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

public class ReelerTypeMaster extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "releer_type_master_seq")
    @SequenceGenerator(name = "releer_type_master_seq", sequenceName = "releer_type_master_seq", allocationSize = 1)
    @Column(name = "reeler_type_master_id")
    private Long reelerTypeMasterId;


    @Size(min = 2, max = 250, message = "reelerTypeMaster name should be more than 1 characters.")
    @Column(name = "reeler_type_master_name", unique = true)
    private String reelerTypeMasterName;

    @Column(name = "no_of_device_allowed")
    private Long noOfDeviceAllowed;

    @Column(name = "reeler_type_name_in_kannada")
    private String reelerTypeNameInKannada;
}
