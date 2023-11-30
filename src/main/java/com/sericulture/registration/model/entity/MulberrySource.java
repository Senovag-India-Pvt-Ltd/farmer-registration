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
public class MulberrySource extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mulberry_source_seq")
    @SequenceGenerator(name = "mulberry_source_seq", sequenceName = "mulberry_source_seq", allocationSize = 1)
    @Column(name = "mulberry_source_id")
    private Long mulberrySourceId;


    @Size(min = 2, max = 250, message = "Mulberry source variety name should be more than 1 characters.")
    @Column(name = "mulberry_source_name", unique = true)
    private String mulberrySourceName;
}
