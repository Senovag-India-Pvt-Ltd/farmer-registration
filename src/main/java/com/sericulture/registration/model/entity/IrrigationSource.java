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
public class IrrigationSource extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "irrigation_source_SEQ")
    @SequenceGenerator(name = "irrigation_source_SEQ", sequenceName = "irrigation_source_SEQ", allocationSize = 1)
    @Column(name = "irrigation_source_ID")
    private Long irrigationSourceId;

    @Size(min = 2, max = 250, message = "IrrigationSource name should be more than 1 characters.")
    @Column(name = "irrigation_source_Name", unique = true)
    private String irrigationSourceName;

    @Column(name = "irrigation_source_name_in_kannada")
    private String irrigationSourceNameInKannada;
}
