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
public class LandCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "land_category_seq")
    @SequenceGenerator(name = "land_category_seq", sequenceName = "land_category_seq", allocationSize = 1)
    @Column(name = "land_category_id")
    private Long landCategoryId;

    @Size(min = 2, max = 250, message = "Land Category name should be more than 1 characters.")
    @Column(name = "land_category_name", unique = true)
    private String landCategoryName;

    @Column(name = "land_category_name_in_kannada")
    private String landCategoryNameInKannada;
}
