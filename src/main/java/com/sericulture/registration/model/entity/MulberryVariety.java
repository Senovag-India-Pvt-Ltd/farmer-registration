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
public class MulberryVariety extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mulberry_variety_seq")
    @SequenceGenerator(name = "mulberry_variety_seq", sequenceName = "mulberry_variety_seq", allocationSize = 1)
    @Column(name = "mulberry_variety_id")
    private Long mulberryVarietyId;


    @Size(min = 2, max = 250, message = "Mulberry variety variety name should be more than 1 characters.")
    @Column(name = "mulberry_variety_name", unique = true)
    private String mulberryVarietyName;
}
