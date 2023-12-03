package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Caste {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CASTE_SEQ")
    @SequenceGenerator(name = "CASTE_SEQ", sequenceName = "CASTE_SEQ", allocationSize = 1)
    @Column(name = "CASTE_ID")
    private Long casteId;


    @Size(min = 2, max = 250 , message = "Title should be more than 1 characters.")
    @Column(name = "CASTE_TITLE",unique = true)
    private String title;

    @Column(name = "CASTE_CODE",unique = true)
    private String code;
}
