package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity(name = "subsidy_master")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Subsidy extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subsidy_seq")
    @SequenceGenerator(name = "subsidy_seq", sequenceName = "subsidy_seq", allocationSize = 1)
    @Column(name = "subsidy_id")
    private Long subsidyId;


    @Size(min = 2, max = 250, message = "Subsidy name should be more than 1 characters.")
    @Column(name = " subsidy_name", unique = true)
    private String subsidyName;

    @Column(name = "subsidy_name_in_kannada")
    private String subsidyNameInKannada;

}
