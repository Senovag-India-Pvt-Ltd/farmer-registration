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
public class LandOwnership extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "land_ownership_seq")
    @SequenceGenerator(name = "land_ownership_seq", sequenceName = "land_ownership_seq", allocationSize = 1)
    @Column(name = "land_ownership_id")
    private Long landOwnershipId;


    @Size(min = 2, max = 250, message = "LandOwnership name should be more than 1 characters.")
    @Column(name = " land_ownership_name", unique = true)
    private String landOwnershipName;

}
