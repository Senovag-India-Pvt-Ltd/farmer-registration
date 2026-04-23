package com.sericulture.registration.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity(name = "market_master")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MarketMaster extends  BaseEntity implements Serializable {
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
    private BigDecimal boxWeight;

    @Column(name = "lot_weight")
    private Long lotWeight;

    @Column(name = "STATE_ID")
    private Long stateId;

    @Column(name = "DISTRICT_ID")
    private Long districtId;

    @Column(name = "TALUK_ID")
    private Long talukId;

    @Column(name = "ISSUE_BID_SLIP_START_TIME")
    private LocalTime issueBidSlipStartTime;


    @Column(name = "ISSUE_BID_SLIP_END_TIME")
    private LocalTime issueBidSlipEndTime;


    @Column(name = "AUCTION_1_START_TIME")
    private LocalTime auction1StartTime;


    @Column(name = "AUCTION_2_START_TIME")
    private LocalTime auction2StartTime;


    @Column(name = "AUCTION_3_START_TIME")
    private LocalTime auction3StartTime;


    @Column(name = "AUCTION_1_END_TIME")
    private LocalTime auction1EndTime;


    @Column(name = "AUCTION_2_END_TIME")
    private LocalTime auction2EndTime;


    @Column(name = "AUCTION_3_END_TIME")
    private LocalTime auction3EndTime;

    @Column(name = "AUCTION1_ACCEPT_START_TIME")
    private LocalTime auctionAcceptance1StartTime;

    @Column(name = "AUCTION2_ACCEPT_START_TIME")
    private LocalTime auctionAcceptance2StartTime;

    @Column(name = "AUCTION3_ACCEPT_START_TIME")
    private LocalTime auctionAcceptance3StartTime;

    @Column(name = "AUCTION1_ACCEPT_END_TIME")
    private LocalTime auctionAcceptance1EndTime;

    @Column(name = "AUCTION2_ACCEPT_END_TIME")
    private LocalTime auctionAcceptance2EndTime;

    @Column(name = "AUCTION3_ACCEPT_END_TIME")
    private LocalTime auctionAcceptance3EndTime;

    @Column(name = "SERIAL_NUMBER_PREFIX")
    private String serialNumberPrefix;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "market_type_master_id")
    private Long marketTypeMasterId;

    @Column(name = "releer_minimum_balance")
    private Long reelerMinimumBalance;

    @Column(name = "market_lat")
    private BigDecimal marketLatitude;

    @Column(name = "market_longitude")
    private BigDecimal marketLongitude;

    @Column(name = "radius")
    private BigDecimal radius;

    @Column(name = "market_name_in_kannada")
    private String marketNameInKannada;

    @Column(name = "snorkel_request_path")
    private String snorkelRequestPath;

    @Column(name = "snorkel_response_path")
    private String snorkelResponsePath;

    @Column(name = "client_code")
    private String clientCode;

    @Column(name = "weighment_triplet_generation", columnDefinition = "TINYINT")
    private Boolean weighmentTripletGeneration;

    @Column(name = "bid_amount_flag", columnDefinition = "TINYINT")
    private Boolean bidAmountFlag;

    @Column(name = "division_master_id")
    private Long divisionMasterId;

    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name="cocoon_age")
    private Long cocoonAge;

    @Column(name = "is_test", columnDefinition = "TINYINT")
    private Boolean isTest;

    @Column(name = "required_base_price" ,columnDefinition = "TINYINT")
    private Boolean requiredBasePrice;

}
