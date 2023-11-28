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
public class State extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STATE_SEQ")
    @SequenceGenerator(name = "STATE_SEQ", sequenceName = "STATE_SEQ", allocationSize = 1)
    @Column(name = "STATE_ID")
    private Long stateId;


    @Size(min = 2, max = 250, message = "State name should be more than 1 characters.")
    @Column(name = "STATE_NAME", unique = true)
    private String stateName;

}
