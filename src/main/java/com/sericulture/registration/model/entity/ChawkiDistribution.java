package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class ChawkiDistribution extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chawki_distribution_seq")
    @SequenceGenerator(name = "chawki_distribution_seq", sequenceName = "chawki_distribution_seq", allocationSize = 1)
    Integer chowkiId;

    @Column(name = "user_master_id")
    private Long userMasterId;

    @Column(name = "farmer_name")
    private String farmerName;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "fruits_id")
    private String fruitsId;

    @Column(name = "source_of_dfls")
    private String dflsSource;

    @Column(name = "numbers_of_dfls")
    private Long numbersOfDfls;

    @Column(name = "lot_numbers_of_the_rsp")
    private String lotNumberRsp;

    @Column(name = "lot_numbers_crc")
    private String lotNumberCrc;

    @Column(name = "race_of_dfls")
    private Integer raceOfDfls;

    @Column(name = "village")
    private Integer village;

    @Column(name = "district")
    private Integer district;

    @Column(name = "state")
    private Integer state;

    @Column(name = "tsc")
    private Integer tsc;

    @Column(name = "hobli")
    private Integer hobli;

    @Column(name = "taluk")
    private Integer taluk;

    @Column(name = "sold_after_1st_or_2nd_mould")
    private String soldAfter1stOr2ndMould;

    @Column(name = "rate_per_100_dfls")
    private Float ratePer100Dfls;

    @Column(name = "price")
    private Float price;

    @Column(name = "dispatch_date")
    private LocalDate dispatchDate;

    @Column(name = "hatching_date")
    private LocalDate hatchingDate;

    @Column(name = "receipt_no")
    private String receiptNo;
}
