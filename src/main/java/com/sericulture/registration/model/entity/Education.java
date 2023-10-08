package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Education extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EDUCATION_SEQ")
    @SequenceGenerator(name = "EDUCATION_SEQ", sequenceName = "EDUCATION_SEQ", allocationSize = 1)
    @Column(name = "EDUCATION_ID")
    private Long id;

    @Column(name = "EDUCATION_NAME")
    private String name;

    @Column(name = "EDUCATION_CODE")
    private String code;
}
