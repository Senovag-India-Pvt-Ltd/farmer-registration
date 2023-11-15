package com.sericulture.registration.model.api.farmer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressResponse;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountResponse;
import com.sericulture.registration.model.api.farmerFamily.FarmerFamilyResponse;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsResponse;
import com.sericulture.registration.model.entity.FarmerAddress;
import com.sericulture.registration.model.entity.FarmerBankAccount;
import com.sericulture.registration.model.entity.FarmerFamily;
import com.sericulture.registration.model.entity.FarmerLandDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetFarmerResponse {
    @Schema(name = "farmerResponse")
    FarmerResponse farmerResponse;

    @Schema(name = "farmerFamilyList")
    List<FarmerFamily> farmerFamilyList;

    @Schema(name = "farmerAddressList")
    List<FarmerAddress> farmerAddressList;

    @Schema(name = "farmerLandDetailsList")
    List<FarmerLandDetails> farmerLandDetailsList;

    @Schema(name = "farmerBankAccount")
    FarmerBankAccount farmerBankAccount;
}