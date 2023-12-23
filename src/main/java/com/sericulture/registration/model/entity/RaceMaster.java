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
public class RaceMaster extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "race_seq")
    @SequenceGenerator(name = "race_seq", sequenceName = "race_seq", allocationSize = 1)
    @Column(name = "race_id")
    private Long raceMasterId;


    @Size(min = 2, max = 250, message = "Race name should be more than 1 characters.")
    @Column(name = "race_name", unique = true)
    private String raceMasterName;
}
