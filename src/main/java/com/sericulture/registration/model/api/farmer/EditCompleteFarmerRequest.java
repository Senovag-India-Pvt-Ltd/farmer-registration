package com.sericulture.registration.model.api.farmer;

import com.sericulture.registration.model.api.RequestBody;
import com.sericulture.registration.model.api.farmerAddress.EditFarmerAddressRequest;
import com.sericulture.registration.model.api.farmerBankAccount.EditFarmerBankAccountRequest;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountRequest;
import com.sericulture.registration.model.api.farmerFamily.EditFarmerFamilyRequest;
import com.sericulture.registration.model.api.farmerFamily.FarmerFamilyRequest;
import com.sericulture.registration.model.api.farmerLandDetails.EditFarmerLandDetailsRequest;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsRequest;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class EditCompleteFarmerRequest extends RequestBody {
    EditFarmerRequest editFarmerRequest;

    EditFarmerBankAccountRequest editFarmerBankAccountRequest;

    EditFarmerAddressRequest editFarmerAddressRequest;

    List<EditFarmerFamilyRequest> editFarmerFamilyRequests;

    List<EditFarmerLandDetailsRequest> editFarmerLandDetailsRequests;
}