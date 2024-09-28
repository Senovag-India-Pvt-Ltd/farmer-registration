package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmerLandDetails.EditFarmerLandDetailsRequest;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsRequest;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsResponse;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountResponse;
import com.sericulture.registration.model.dto.farmer.FarmerLandDetailsDTO;
import com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO;
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
        FarmerLandDetailsResponse farmerLandDetailsResponse = new FarmerLandDetailsResponse();
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

    public FarmerLandDetailsRequest editToFarmerLandDetailsRequest(EditFarmerLandDetailsRequest editRequest) {
        FarmerLandDetailsRequest landDetailsRequest = new FarmerLandDetailsRequest();
        // Set fields from editRequest to landDetailsRequest
        landDetailsRequest.setFarmerId(editRequest.getFarmerId());
        landDetailsRequest.setFarmerLandDetailsId(editRequest.getFarmerLandDetailsId());
        landDetailsRequest.setCategoryNumber(editRequest.getCategoryNumber());
        landDetailsRequest.setSoilTypeId(editRequest.getSoilTypeId());
        landDetailsRequest.setHissa(editRequest.getHissa());
        landDetailsRequest.setMulberryArea(editRequest.getMulberryArea());
        landDetailsRequest.setMulberrySourceId(editRequest.getMulberrySourceId());
        landDetailsRequest.setMulberryVarietyId(editRequest.getMulberryVarietyId());
        landDetailsRequest.setPlantationDate(editRequest.getPlantationDate());
        landDetailsRequest.setSpacing(editRequest.getSpacing());
        landDetailsRequest.setPlantationTypeId(editRequest.getPlantationTypeId());
        landDetailsRequest.setIrrigationSourceId(editRequest.getIrrigationSourceId());
        landDetailsRequest.setIrrigationTypeId(editRequest.getIrrigationTypeId());
        landDetailsRequest.setRearingHouseDetails(editRequest.getRearingHouseDetails());
        landDetailsRequest.setIrrigationSourceId(editRequest.getIrrigationSourceId());
        landDetailsRequest.setIrrigationTypeId(editRequest.getIrrigationTypeId());
        landDetailsRequest.setRearingHouseDetails(editRequest.getRearingHouseDetails());
        landDetailsRequest.setRoofTypeId(editRequest.getRoofTypeId());
        landDetailsRequest.setSilkWormVarietyId(editRequest.getSilkWormVarietyId());
        landDetailsRequest.setRearingCapacityCrops(editRequest.getRearingCapacityCrops());
        landDetailsRequest.setRearingCapacityDlf(editRequest.getRearingCapacityDlf());
        landDetailsRequest.setSubsidyAvailed(editRequest.getSubsidyAvailed());
        landDetailsRequest.setSubsidyId(editRequest.getSubsidyId());
        landDetailsRequest.setLoanDetails(editRequest.getLoanDetails());
        landDetailsRequest.setEquipmentDetails(editRequest.getEquipmentDetails());
        landDetailsRequest.setGpsLat(editRequest.getGpsLat());
        landDetailsRequest.setGpsLng(editRequest.getGpsLng());
        landDetailsRequest.setSurveyNumber(editRequest.getSurveyNumber());
        landDetailsRequest.setStateId(editRequest.getStateId());
        landDetailsRequest.setDistrictId(editRequest.getDistrictId());
        landDetailsRequest.setTalukId(editRequest.getTalukId());
        landDetailsRequest.setVillageId(editRequest.getVillageId());
        landDetailsRequest.setHobliId(editRequest.getHobliId());
        landDetailsRequest.setAddress(editRequest.getAddress());
        landDetailsRequest.setPincode(editRequest.getPincode());
        landDetailsRequest.setOwnerName(editRequest.getOwnerName());
        landDetailsRequest.setAddress(editRequest.getAddress());
        landDetailsRequest.setPincode(editRequest.getPincode());
        landDetailsRequest.setOwnerName(editRequest.getOwnerName());
        landDetailsRequest.setSurNoc(editRequest.getSurNoc());
        landDetailsRequest.setNameScore(editRequest.getNameScore());
        landDetailsRequest.setOwnerNo(editRequest.getOwnerNo());
        landDetailsRequest.setMainOwnerNo(editRequest.getMainOwnerNo());
        landDetailsRequest.setAcre(editRequest.getAcre());
        landDetailsRequest.setGunta(editRequest.getGunta());
        landDetailsRequest.setFGunta(editRequest.getFGunta());
        landDetailsRequest.setLandCode(editRequest.getLandCode());
        landDetailsRequest.setDistrictCode(editRequest.getDistrictCode());
        landDetailsRequest.setTalukCode(editRequest.getTalukCode());
        landDetailsRequest.setHobliCode(editRequest.getHobliCode());
        landDetailsRequest.setVillageCode(editRequest.getVillageCode());
        // Set other fields as necessary...
        return landDetailsRequest;
    }


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
    public FarmerLandDetailsResponse deleteFarmerLandDetailsDetails(long id) {
        FarmerLandDetailsResponse farmerLandDetailsResponse = new FarmerLandDetailsResponse();
        FarmerLandDetails farmerLandDetails = farmerLandDetailsRepository.findByFarmerLandDetailsIdAndActive(id, true);
        if (Objects.nonNull(farmerLandDetails)) {
            farmerLandDetails.setActive(false);
            farmerLandDetailsResponse = mapper.farmerLandDetailsEntityToObject(farmerLandDetailsRepository.save(farmerLandDetails), FarmerLandDetailsResponse.class);
            farmerLandDetailsResponse.setError(false);
        } else {
            farmerLandDetailsResponse.setError(true);
            farmerLandDetailsResponse.setError_description("Invalid Id");
            // throw new ValidationException("Invalid Id");
        }
        return farmerLandDetailsResponse;
    }

    public FarmerLandDetailsResponse getById(int id){
        FarmerLandDetailsResponse farmerLandDetailsResponse = new FarmerLandDetailsResponse();
        FarmerLandDetails farmerLandDetails = farmerLandDetailsRepository.findByFarmerLandDetailsIdAndActive(id,true);
        if(farmerLandDetails == null){
            farmerLandDetailsResponse.setError(true);
            farmerLandDetailsResponse.setError_description("Invalid id");
        }else{
            farmerLandDetailsResponse =  mapper.farmerLandDetailsEntityToObject(farmerLandDetails,FarmerLandDetailsResponse.class);
            farmerLandDetailsResponse.setError(false);
        }
        log.info("Entity is ",farmerLandDetails);
        return farmerLandDetailsResponse;
    }

    public Map<String,Object> getByFarmerId(int farmerId) {
        Map<String, Object> response = new HashMap<>();
        List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerIdAndActive(farmerId, true);
        if (farmerLandDetailsList.isEmpty()) {
            response.put("error", "Error");
            response.put("error_description", "Invalid id");
            return response;
        } else {
            log.info("Entity is ", farmerLandDetailsList);
            response = convertListToMapResponse(farmerLandDetailsList);
            return response;
        }
    }

    private Map<String, Object> convertListToMapResponse(List<FarmerLandDetails> farmerLandDetailsList) {
        Map<String, Object> response = new HashMap<>();
        List<FarmerLandDetailsResponse> farmerLandDetailsResponses = farmerLandDetailsList.stream()
                .map(farmerLandDetails -> mapper.farmerLandDetailsEntityToObject(farmerLandDetails,FarmerLandDetailsResponse.class)).collect(Collectors.toList());
        response.put("farmerLandDetails",farmerLandDetailsResponses);
        response.put("totalItems", farmerLandDetailsList.size());
        return response;
    }

    public FarmerLandDetailsResponse getByIdJoin(int id){
        FarmerLandDetailsResponse farmerLandDetailsResponse = new FarmerLandDetailsResponse();
        FarmerLandDetailsDTO farmerLandDetailsDTO = farmerLandDetailsRepository.getByFarmerLandDetailsIdAndActive(id,true);
        if(farmerLandDetailsDTO == null){
            farmerLandDetailsResponse.setError(true);
            farmerLandDetailsResponse.setError_description("Invalid id");
        } else {
            farmerLandDetailsResponse = mapper.farmerLandDetailsDTOToObject(farmerLandDetailsDTO, FarmerLandDetailsResponse.class);
            farmerLandDetailsResponse.setError(false);
        }
        log.info("Entity is ", farmerLandDetailsDTO);
        return farmerLandDetailsResponse;
    }

    @Transactional
    public FarmerLandDetailsResponse updateFarmerLandDetailsDetails(EditFarmerLandDetailsRequest farmerLandDetailsRequest){
        FarmerLandDetailsResponse farmerLandDetailsResponse = new FarmerLandDetailsResponse();
       /* List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerLandDetailsName(farmerLandDetailsRequest.getFarmerLandDetailsName());
        if(farmerLandDetailsList.size()>0){
            throw new ValidationException("FarmerLandDetails already exists with this name, duplicates are not allowed.");
        }*/

        FarmerLandDetails farmerLandDetails = farmerLandDetailsRepository.findByFarmerLandDetailsIdAndActiveIn(farmerLandDetailsRequest.getFarmerLandDetailsId(), Set.of(true,false));
        if(Objects.nonNull(farmerLandDetails)){
            farmerLandDetails.setFarmerId(farmerLandDetailsRequest.getFarmerId());
            farmerLandDetails.setCategoryNumber(farmerLandDetailsRequest.getCategoryNumber());
            farmerLandDetails.setLandOwnershipId(farmerLandDetailsRequest.getLandOwnershipId());
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
            farmerLandDetails.setSubsidyId(farmerLandDetailsRequest.getSubsidyId());
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
            farmerLandDetails.setOwnerName(farmerLandDetailsRequest.getOwnerName());
            farmerLandDetails.setSurNoc(farmerLandDetails.getSurNoc());
            farmerLandDetails.setNameScore(farmerLandDetailsRequest.getNameScore());
            farmerLandDetails.setOwnerNo(farmerLandDetails.getMainOwnerNo());
            farmerLandDetails.setMainOwnerNo(farmerLandDetailsRequest.getMainOwnerNo());
            farmerLandDetails.setAcre(farmerLandDetailsRequest.getAcre());
            farmerLandDetails.setGunta(farmerLandDetailsRequest.getGunta());
            farmerLandDetails.setFGunta(farmerLandDetailsRequest.getFGunta());
            farmerLandDetails.setLandCode(farmerLandDetailsRequest.getLandCode());
            farmerLandDetails.setDistrictCode(farmerLandDetailsRequest.getDistrictCode());
            farmerLandDetails.setTalukCode(farmerLandDetailsRequest.getTalukCode());
            farmerLandDetails.setHobliCode(farmerLandDetailsRequest.getHobliCode());
            farmerLandDetails.setVillageCode(farmerLandDetailsRequest.getVillageCode());

            farmerLandDetails.setActive(true);
            FarmerLandDetails farmerLandDetails1 = farmerLandDetailsRepository.save(farmerLandDetails);
            farmerLandDetailsResponse = mapper.farmerLandDetailsEntityToObject(farmerLandDetails1, FarmerLandDetailsResponse.class);
            farmerLandDetailsResponse.setError(false);
        } else {
            farmerLandDetailsResponse.setError(true);
            farmerLandDetailsResponse.setError_description("Error occurred while fetching farmerLandDetails");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return farmerLandDetailsResponse;
    }

    public Map<String,Object> getByFarmerIdJoin(int farmerId){
        Map<String, Object> response = new HashMap<>();
        List<FarmerLandDetailsDTO> farmerLandDetailsDTO = farmerLandDetailsRepository.getByFarmerIdAndActive(farmerId, true);
        if(farmerLandDetailsDTO.isEmpty()){
            response.put("error","Error");
            response.put("error_description","Invalid id");
            return response;
        }else {
            log.info("Entity is ", farmerLandDetailsDTO);
            response = convertListDTOToMapResponse(farmerLandDetailsDTO);
            return response;
        }
    }

    private Map<String, Object> convertListDTOToMapResponse(List<FarmerLandDetailsDTO> farmerLandDetailsDTOList) {
        Map<String, Object> response = new HashMap<>();
        List<FarmerLandDetailsResponse> farmerLandDetailsResponse = farmerLandDetailsDTOList.stream()
                .map(farmerLandDetailsDTO -> mapper.farmerLandDetailsDTOToObject(farmerLandDetailsDTO, FarmerLandDetailsResponse.class)).collect(Collectors.toList());
        response.put("farmerLandDetails", farmerLandDetailsResponse);
        response.put("totalItems", farmerLandDetailsDTOList.size());
        return response;
    }

}