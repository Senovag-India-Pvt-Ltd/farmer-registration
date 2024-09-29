package com.sericulture.registration.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sericulture.registration.model.api.farmer.FarmerResponse;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsResponse;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetFruitsDetailsResponse {

    private List<FarmerResponse> farmerResponses;
    private List<FarmerLandDetailsResponse> farmerLandDetailsResponses;

}
