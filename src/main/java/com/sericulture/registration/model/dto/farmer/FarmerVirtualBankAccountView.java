package com.sericulture.registration.model.dto.farmer;

public interface FarmerVirtualBankAccountView {
    Long getFarmerVirtualBankAccountId();
    Long getFarmerId();
    String getVirtualAccountNumber();
    String getBranchName();
    String getIfscCode();
    Long getMarketMasterId();
    String getFirstName();
    String getFruitsId();
    String getFarmerNumber();
    String getMobileNumber();
    String getMarketMasterName();
    Boolean getIsLocked();
}