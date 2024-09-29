package com.sericulture.registration.service;


import com.sericulture.registration.helper.Util;
import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.CheckInspectionStatusRequest;
import com.sericulture.registration.model.api.GetFruitsDetailsResponse;
import com.sericulture.registration.model.api.farmer.FarmerResponse;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsResponse;
import com.sericulture.registration.repository.FarmerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GetFruitsDetailsService {

    @Autowired
    FarmerRepository farmerRepository;

    public ResponseEntity<?> getInspectionFarmerDetailsByFruitsId(CheckInspectionStatusRequest checkInspectionStatusRequest) {
        ResponseWrapper<List<GetFruitsDetailsResponse>> rw = ResponseWrapper.createWrapper(List.class);

        List<GetFruitsDetailsResponse> getFruitsDetailsResponseList = new ArrayList<>();
        List<Object[]> fruitsResponse = farmerRepository.getFruitsDetails(checkInspectionStatusRequest.getFruitsId());

        GetFruitsDetailsResponse getFruitsDetailsResponse = new GetFruitsDetailsResponse();

        getFruitsDetailsResponse.setFarmerResponses(convertToFruitsDetailsResponses(fruitsResponse));

        getFruitsDetailsResponseList.add(getFruitsDetailsResponse);
        rw.setContent(getFruitsDetailsResponseList);

        return ResponseEntity.ok(rw);
    }

    private List<FarmerResponse> convertToFruitsDetailsResponses(List<Object[]> fruitsDetails) {
        List<FarmerResponse> responses = new ArrayList<>();
        for (Object[] arr : fruitsDetails) {
            FarmerResponse response = FarmerResponse.builder()
                    .fullName(Util.objectToString(arr[0]))
                    .address(Util.objectToString(arr[1]))
                    .farmerId(Util.objectToLong(arr[2]))
                    .fruitsId(Util.objectToString(arr[3]))
                    .build();
            responses.add(response);
        }
        return responses;
    }

    public ResponseEntity<?> getInspectionFarmerLandDetailsByFruitsId(CheckInspectionStatusRequest checkInspectionStatusRequest) {
        ResponseWrapper<List<GetFruitsDetailsResponse>> rw = ResponseWrapper.createWrapper(List.class);

        List<GetFruitsDetailsResponse> getFruitsDetailsResponseList = new ArrayList<>();
        List<Object[]> landDetails = farmerRepository.getFarmerLandDetails(checkInspectionStatusRequest.getFruitsId());

        GetFruitsDetailsResponse getFruitsDetailsResponse = new GetFruitsDetailsResponse();

        getFruitsDetailsResponse.setFarmerLandDetailsResponses(convertToFarmerLandDetailsResponses(landDetails));

        getFruitsDetailsResponseList.add(getFruitsDetailsResponse);
        rw.setContent(getFruitsDetailsResponseList);

        return ResponseEntity.ok(rw);
    }

    private List<FarmerLandDetailsResponse> convertToFarmerLandDetailsResponses(List<Object[]> landDetails) {
        List<FarmerLandDetailsResponse> responses = new ArrayList<>();
        for (Object[] arr : landDetails) {
            FarmerLandDetailsResponse response = FarmerLandDetailsResponse.builder()
                    .farmerLandDetailsId(Util.objectToInteger(arr[0]))
                    .surveyNumber(Util.objectToString(arr[1]))
                    .villageName(Util.objectToString(arr[2]))
                    .build();
            responses.add(response);
        }
        return responses;
    }

}
