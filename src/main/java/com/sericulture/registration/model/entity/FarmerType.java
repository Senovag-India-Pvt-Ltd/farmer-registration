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
public class FarmerType extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FARMER_TYPE_SEQ")
    @SequenceGenerator(name = "FARMER_TYPE_SEQ", sequenceName = "FARMER_TYPE_SEQ", allocationSize = 1)
    @Column(name = "FARMER_TYPE_ID")
    private Long farmerTypeId;


    @Size(min = 2, max = 250, message = "Farmer type name should be more than 1 characters.")
    @Column(name = "NAME")
    private String name;

}

