package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@Where(clause = "active=1")
public class Education extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EDUCATION_SEQ")
    @SequenceGenerator(name = "EDUCATION_SEQ", sequenceName = "EDUCATION_SEQ", allocationSize = 1)
    @Column(name = "EDUCATION_ID")
    private Long educationId;


    @Size(min = 2, max = 250 , message = "Name should be more than 1 characters.")
    @Column(name = "EDUCATION_NAME")
    private String name;

    @Column(name = "EDUCATION_CODE")
    private String code;

    @Column(name = "education_name_in_kannada")
    private String educationNameInKannada;
}
