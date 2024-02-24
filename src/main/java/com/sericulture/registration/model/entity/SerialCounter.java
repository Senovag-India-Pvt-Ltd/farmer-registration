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
public class SerialCounter extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serial_counter_seq")
    @SequenceGenerator(name = "serial_counter_seq", sequenceName = "serial_counter_seq", allocationSize = 1)
    @Column(name = "serial_counter_id")
    private Long serialCounterId;

    @Column(name = "reeler_counter_number")
    private Long reelerCounterNumber;

    @Column(name = "trader_counter_number")
    private Long traderCounterNumber;

    @Column(name = "reeler_license_renewal_counter_number")
    private Long reelerLicenseRenewalCounterNumber;

    @Column(name = "transfer_reeler_license_counter_number")
    private Long transferReelerLicenseCounterNumber;

    @Column(name = "other_state_farmer_counter")
    private Long otherStateFarmerCounter;
}
