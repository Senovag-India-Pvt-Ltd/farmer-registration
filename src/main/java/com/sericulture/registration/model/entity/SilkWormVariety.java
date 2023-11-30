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
public class SilkWormVariety extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "silk_worm_variety_seq")
    @SequenceGenerator(name = "silk_worm_variety_seq", sequenceName = "silk_worm_variety_seq", allocationSize = 1)
    @Column(name = "silk_worm_variety_id")
    private Long silkWormVarietyId;


    @Size(min = 2, max = 250, message = "Silk worm variety name should be more than 1 characters.")
    @Column(name = "silk_worm_variety_name", unique = true)
    private String silkWormVarietyName;
}
