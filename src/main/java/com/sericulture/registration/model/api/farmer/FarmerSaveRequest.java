package com.sericulture.registration.model.api.farmer;

import com.sericulture.registration.model.api.RequestBody;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressRequest;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountRequest;
import com.sericulture.registration.model.api.farmerFamily.FarmerFamilyRequest;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsRequest;
import com.sericulture.registration.model.entity.FarmerBankAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;
import java.util.List;


@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class FarmerSaveRequest extends RequestBody {
    FarmerRequest farmerRequest;

    FarmerBankAccountRequest farmerBankAccountRequest;

    List <FarmerAddressRequest> farmerAddressRequests;

    List<FarmerFamilyRequest> farmerFamilyRequestList;

    List<FarmerLandDetailsRequest> farmerLandDetailsRequests;
}