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
public class TscMaster extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tsc_master_seq")
    @SequenceGenerator(name = "tsc_master_seq", sequenceName = "tsc_master_seq", allocationSize = 1)
    @Column(name = "tsc_master_id")
    private Long tscMasterId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "taluk_id")
    private Long talukId;

    @Column(name = "address")
    private String address;

    @Size(min = 2, max = 250, message = "Tsc Name should be more than 1 characters.")
    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "name_in_kannada", unique = true)
    private String nameInKannada;
}
