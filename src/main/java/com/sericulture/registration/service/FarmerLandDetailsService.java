package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmerLandDetails.EditFarmerLandDetailsRequest;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsRequest;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsResponse;
import com.sericulture.registration.model.entity.FarmerLandDetails;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.FarmerLandDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FarmerLandDetailsService {

    @Autowired
    FarmerLandDetailsRepository farmerLandDetailsRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Transactional
    public FarmerLandDetailsResponse insertFarmerLandDetailsDetails(FarmerLandDetailsRequest farmerLandDetailsRequest){
        FarmerLandDetails farmerLandDetails = mapper.farmerLandDetailsObjectToEntity(farmerLandDetailsRequest,FarmerLandDetails.class);
        validator.validate(farmerLandDetails);
        /*List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerLandDetailsName(farmerLandDetailsRequest.getFarmerLandDetailsName());
        if(!farmerLandDetailsList.isEmpty() && farmerLandDetailsList.stream().filter(FarmerLandDetails::getActive).findAny().isPresent()){
            throw new ValidationException("FarmerLandDetails name already exist");
        }
        if(!farmerLandDetailsList.isEmpty() && farmerLandDetailsList.stream().filter(Predicate.not(FarmerLandDetails::getActive)).findAny().isPresent()){
            throw new ValidationException("FarmerLandDetails name already exist with inactive state");
        }*/
        return mapper.farmerLandDetailsEntityToObject(farmerLandDetailsRepository.save(farmerLandDetails),FarmerLandDetailsResponse.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getPaginatedFarmerLandDetailsDetails(final Pageable pageable){
        return convertToMapResponse(farmerLandDetailsRepository.findByActiveOrderByFarmerLandDetailsIdAsc( true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<FarmerLandDetails> activeFarmerLandDetailss) {
        Map<String, Object> response = new HashMap<>();

        List<FarmerLandDetailsResponse> farmerLandDetailsResponses = activeFarmerLandDetailss.getContent().stream()
                .map(farmerLandDetails -> mapper.farmerLandDetailsEntityToObject(farmerLandDetails,FarmerLandDetailsResponse.class)).collect(Collectors.toList());
        response.put("farmerLandDetails",farmerLandDetailsResponses);
        response.put("currentPage", activeFarmerLandDetailss.getNumber());
        response.put("totalItems", activeFarmerLandDetailss.getTotalElements());
        response.put("totalPages", activeFarmerLandDetailss.getTotalPages());

        return response;
    }

    @Transactional
    public void deleteFarmerLandDetailsDetails(long id) {
        FarmerLandDetails farmerLandDetails = farmerLandDetailsRepository.findByFarmerLandDetailsIdAndActive(id, true);
        if (Objects.nonNull(farmerLandDetails)) {
            farmerLandDetails.setActive(false);
            farmerLandDetailsRepository.save(farmerLandDetails);
        } else {
            throw new ValidationException("Invalid Id");
        }
    }

    @Transactional
    public FarmerLandDetailsResponse getById(int id){
        FarmerLandDetails farmerLandDetails = farmerLandDetailsRepository.findByFarmerLandDetailsIdAndActive(id,true);
        if(farmerLandDetails == null){
            throw new ValidationException("Invalid Id");
        }
        log.info("Entity is ",farmerLandDetails);
        return mapper.farmerLandDetailsEntityToObject(farmerLandDetails,FarmerLandDetailsResponse.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getByFarmerId(int farmerId){
        List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerIdAndActive(farmerId, true);
        if(farmerLandDetailsList.isEmpty()){
            throw new ValidationException("Farmer Land Details not found by Farmer Id");
        }
        return convertListToMapResponse(farmerLandDetailsList);
    }

    private Map<String, Object> convertListToMapResponse(List<FarmerLandDetails> farmerLandDetailsList) {
        Map<String, Object> response = new HashMap<>();
        List<FarmerLandDetailsResponse> farmerLandDetailsResponses = farmerLandDetailsList.stream()
                .map(farmerLandDetails -> mapper.farmerLandDetailsEntityToObject(farmerLandDetails,FarmerLandDetailsResponse.class)).collect(Collectors.toList());
        response.put("farmerLandDetails",farmerLandDetailsResponses);
        response.put("totalItems", farmerLandDetailsList.size());
        return response;
    }

    @Transactional
    public FarmerLandDetailsResponse updateFarmerLandDetailsDetails(EditFarmerLandDetailsRequest farmerLandDetailsRequest){
       /* List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerLandDetailsName(farmerLandDetailsRequest.getFarmerLandDetailsName());
        if(farmerLandDetailsList.size()>0){
            throw new ValidationException("FarmerLandDetails already exists with this name, duplicates are not allowed.");
        }*/

        FarmerLandDetails farmerLandDetails = farmerLandDetailsRepository.findByFarmerLandDetailsIdAndActiveIn(farmerLandDetailsRequest.getFarmerLandDetailsId(), Set.of(true,false));
        if(Objects.nonNull(farmerLandDetails)){
            farmerLandDetails.setFarmerId(farmerLandDetailsRequest.getFarmerId());
            farmerLandDetails.setCategoryNumber(farmerLandDetailsRequest.getCategoryNumber());
            farmerLandDetails.setLandOwnerShipId(farmerLandDetailsRequest.getLandOwnerShipId());
            farmerLandDetails.setSoilTypeId(farmerLandDetailsRequest.getSoilTypeId());
            farmerLandDetails.setHissa(farmerLandDetailsRequest.getHissa());
            farmerLandDetails.setMulberryArea(farmerLandDetailsRequest.getMulberryArea());
            farmerLandDetails.setMulberrySourceId(farmerLandDetailsRequest.getMulberrySourceId());
            farmerLandDetails.setMulberryVarietyId(farmerLandDetailsRequest.getMulberryVarietyId());
            farmerLandDetails.setPlantationDate(farmerLandDetailsRequest.getPlantationDate());
            farmerLandDetails.setSpacing(farmerLandDetailsRequest.getSpacing());
            farmerLandDetails.setPlantationTypeId(farmerLandDetailsRequest.getPlantationTypeId());
            farmerLandDetails.setIrrigationSourceId(farmerLandDetailsRequest.getIrrigationSourceId());
            farmerLandDetails.setIrrigationTypeId(farmerLandDetailsRequest.getIrrigationTypeId());
            farmerLandDetails.setRearingHouseDetails(farmerLandDetailsRequest.getRearingHouseDetails());
            farmerLandDetails.setRoofTypeId(farmerLandDetailsRequest.getRoofTypeId());
            farmerLandDetails.setSilkWormVarietyId(farmerLandDetailsRequest.getSilkWormVarietyId());
            farmerLandDetails.setRearingCapacityCrops(farmerLandDetailsRequest.getRearingCapacityCrops());
            farmerLandDetails.setRearingCapacityDlf(farmerLandDetailsRequest.getRearingCapacityDlf());
            farmerLandDetails.setSubsidyAvailed(farmerLandDetailsRequest.getSubsidyAvailed());
            farmerLandDetails.setSubsidyMasterId(farmerLandDetailsRequest.getSubsidyMasterId());
            farmerLandDetails.setLoanDetails(farmerLandDetailsRequest.getLoanDetails());
            farmerLandDetails.setEquipmentDetails(farmerLandDetailsRequest.getEquipmentDetails());
            farmerLandDetails.setGpsLat(farmerLandDetailsRequest.getGpsLat());
            farmerLandDetails.setGpsLng(farmerLandDetailsRequest.getGpsLng());
            farmerLandDetails.setSurveyNumber(farmerLandDetailsRequest.getSurveyNumber());
            farmerLandDetails.setStateId(farmerLandDetails.getStateId());
            farmerLandDetails.setDistrictId(farmerLandDetailsRequest.getDistrictId());
            farmerLandDetails.setTalukId(farmerLandDetails.getTalukId());
            farmerLandDetails.setHobliId(farmerLandDetailsRequest.getHobliId());
            farmerLandDetails.setVillageId(farmerLandDetailsRequest.getVillageId());
            farmerLandDetails.setAddress(farmerLandDetailsRequest.getAddress());
            farmerLandDetails.setPincode(farmerLandDetailsRequest.getPincode());

            farmerLandDetails.setActive(true);
        }else{
            throw new ValidationException("Error occurred while fetching farmerLandDetails");
        }
        return mapper.farmerLandDetailsEntityToObject(farmerLandDetailsRepository.save(farmerLandDetails),FarmerLandDetailsResponse.class);
    }

}