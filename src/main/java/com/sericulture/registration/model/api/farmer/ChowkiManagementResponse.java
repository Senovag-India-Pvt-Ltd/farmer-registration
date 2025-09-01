package com.sericulture.registration.model.api.farmer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChowkiManagementResponse {

    private int chowkiId;

    private String farmerName;

    private String fatherName;

    private String fruitsId;

    private String dflsSource;

    private String raceName;

    private Long numbersOfDfls;

    private String lotNumberRsp;

    private String lotNumberCrc;

    private String villageName;

    private String districtName;

    private String stateName;

    private String talukName;

    private String hobliName;

    private String tscName;

    private String soldAfter1stOr2ndMould;

    private Float ratePer100Dfls;

    private Float price;

    private Date hatchingDate;
    private String hatchingInspectionDate;
    private Date dispatchDate;
    private Long farmerId;
    private int isVerified;
    private int isSaleTracked;
    private String receiptNo;
    private int saleAndDisposalId;
    private String ratePerDFls;
    private Long raceId;
    private String expectedHatchingDate;
    private String auctionDate;
    private String cocoonsQuantity;
    private String dateOfDisposal;
    private String nameAndAddressOfTheFarm;
    private int serialNumber;
    private String hatchingDateForReport;
    private String dispatchDateForReport;

}
