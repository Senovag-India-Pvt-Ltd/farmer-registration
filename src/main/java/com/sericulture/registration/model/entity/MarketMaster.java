package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity(name = "market_master")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MarketMaster extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "market_master_seq")
    @SequenceGenerator(name = "market_master_seq", sequenceName = "market_master_seq", allocationSize = 1)
    @Column(name = "market_master_id")
    private Long marketMasterId;

    @Size(min = 2, max = 250, message = "Market name should be more than 1 characters.")
    @Column(name = "market_name", unique = true)
    private String marketMasterName;

    @Size(min = 2, max = 250, message = "Market address should be more than 1 characters.")
    @Column(name = "market_address", unique = true)
    private String marketMasterAddress;

    @Column(name = "box_weight")
    private Long boxWeight;

    @Column(name = "lot_weight")
    private Long lotWeight;

    @Column(name = "STATE_ID")
    private Long stateId;

    @Column(name = "DISTRICT_ID")
    private Long districtId;

    @Column(name = "TALUK_ID")
    private Long talukId;
}
