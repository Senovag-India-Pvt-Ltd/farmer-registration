package com.sericulture.registration.model.dto.farmer;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerAddressDTO {
    private Long farmerAddressId;
    private Long farmerId;
    private String addressText;
    private String pincode;
    private Boolean defaultAddress;
    private Long stateId;
    private Long districtId;
    private Long talukId;
    private Long hobliId;
    private Long villageId;
    private String taluk;
    private String district;
    private String village;
    private String stateName;
    private String districtName;
    private String talukName;
    private String hobliName;
    private String villageName;

}
