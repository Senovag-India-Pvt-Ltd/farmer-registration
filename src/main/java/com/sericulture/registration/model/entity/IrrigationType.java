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
public class IrrigationType extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "irrigation_type_seq")
    @SequenceGenerator(name = "irrigation_type_seq", sequenceName = "irrigation_type_seq", allocationSize = 1)
    @Column(name = "irrigation_type_id")
    private Long irrigationTypeId;


    @Size(min = 2, max = 250, message = "IrrigationType name should be more than 1 characters.")
    @Column(name = "irrigation_type_Name", unique = true)
    private String irrigationTypeName;
}
