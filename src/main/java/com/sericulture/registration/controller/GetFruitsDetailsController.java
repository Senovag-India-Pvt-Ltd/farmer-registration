package com.sericulture.registration.controller;

import com.sericulture.registration.model.api.CheckInspectionStatusRequest;
import com.sericulture.registration.service.GetFruitsDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/farmer-details/")
public class GetFruitsDetailsController {

    @Autowired
    GetFruitsDetailsService getFruitsDetailsService;


    @PostMapping("/getInspectionFarmerDetailsByFruitsId")
    public ResponseEntity<?> getInspectionFarmerDetailsByFruitsId(@RequestBody CheckInspectionStatusRequest checkInspectionStatusRequest){
        return getFruitsDetailsService.getInspectionFarmerDetailsByFruitsId(checkInspectionStatusRequest);

    }

    @PostMapping("/getInspectionFarmerLandDetailsByFruitsId")
    public ResponseEntity<?> getInspectionFarmerLandDetailsByFruitsId(@RequestBody CheckInspectionStatusRequest checkInspectionStatusRequest){
        return getFruitsDetailsService.getInspectionFarmerLandDetailsByFruitsId(checkInspectionStatusRequest);

    }
}
