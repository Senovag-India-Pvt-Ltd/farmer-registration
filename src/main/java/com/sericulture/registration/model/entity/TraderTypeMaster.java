package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity(name = "trader_type_master")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TraderTypeMaster extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trader_type_seq")
    @SequenceGenerator(name = "trader_type_seq", sequenceName = "trader_type_seq", allocationSize = 1)
    @Column(name = "trader_type_id")
    private Long traderTypeMasterId;


    @Size(min = 2, max = 250, message = "Trader Type name should be more than 1 characters.")
    @Column(name = "trader_type_name", unique = true)
    private String traderTypeMasterName;

    @Column(name = "trader_type_name_in_kannada")
    private String traderTypeNameInKannada;
}
