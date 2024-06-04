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
public class Taluk extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TALUK_SEQ")
    @SequenceGenerator(name = "TALUK_SEQ", sequenceName = "TALUK_SEQ", allocationSize = 1)
    @Column(name = "TALUK_ID")
    private Long talukId;

    @Size(min = 2, max = 250, message = "TALUK name should be more than 1 characters.")
    @Column(name = "TALUK_NAME", unique = true)
    private String talukName;

    @Column(name = "STATE_ID")
    private Long stateId;

    @Column(name = "DISTRICT_ID")
    private Long districtId;

    @Column(name = "taluk_name_in_kannada")
    private String talukNameInKannada;

    @Column(name = "lg_taluk")
    private String lgTaluk;

    @Column(name = "code")
    private String talukCode;
}