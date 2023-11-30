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
public class RoofType extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roof_type_seq")
    @SequenceGenerator(name = "roof_type_seq", sequenceName = "roof_type_seq", allocationSize = 1)
    @Column(name = "roof_type_id")
    private Long roofTypeId;


    @Size(min = 2, max = 250, message = "Roof Type name should be more than 1 characters.")
    @Column(name = " roof_type_name", unique = true)
    private String roofTypeName;
}
