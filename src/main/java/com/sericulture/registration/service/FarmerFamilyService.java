package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmerAddress.FarmerAddressResponse;
import com.sericulture.registration.model.api.farmerFamily.EditFarmerFamilyRequest;
import com.sericulture.registration.model.api.farmerFamily.FarmerFamilyRequest;
import com.sericulture.registration.model.api.farmerFamily.FarmerFamilyResponse;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountResponse;
import com.sericulture.registration.model.dto.farmer.FarmerAddressDTO;
import com.sericulture.registration.model.dto.farmer.FarmerFamilyDTO;
import com.sericulture.registration.model.entity.FarmerFamily;
import com.sericulture.registration.model.entity.ReelerVirtualBankAccount;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.FarmerFamilyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FarmerFamilyService {

    @Autowired
    FarmerFamilyRepository farmerFamilyRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    public FarmerFamilyResponse getFarmerFamilyDetails(String farmerFamilyName){
        FarmerFamilyResponse farmerFamilyResponse = new FarmerFamilyResponse();
        FarmerFamily farmerFamily = farmerFamilyRepository.findByFarmerFamilyNameAndActive(farmerFamilyName,true);
        if(farmerFamily==null){
            farmerFamilyResponse.setError(true);
            farmerFamilyResponse.setError_description("Farmer Family not found");
        }else{
            farmerFamilyResponse = mapper.farmerFamilyEntityToObject(farmerFamily,FarmerFamilyResponse.class);
            farmerFamilyResponse.setError(false);
        }
        log.info("Entity is ",farmerFamily);
        return farmerFamilyResponse;
    }

    @Transactional
    public FarmerFamilyResponse insertFarmerFamilyDetails(FarmerFamilyRequest farmerFamilyRequest){
        FarmerFamilyResponse farmerFamilyResponse = new FarmerFamilyResponse();
        FarmerFamily farmerFamily = mapper.farmerFamilyObjectToEntity(farmerFamilyRequest,FarmerFamily.class);
        validator.validate(farmerFamily);
        List<FarmerFamily> farmerFamilyList = farmerFamilyRepository.findByFarmerFamilyNameAndFarmerIdAndActive(farmerFamilyRequest.getFarmerFamilyName(), farmerFamilyRequest.getFarmerId(), true);
        if(!farmerFamilyList.isEmpty()){
//            throw new ValidationException("Farmer Family name already exist");
            farmerFamilyResponse.setError(true);
            farmerFamilyResponse.setError_description("Farmer Family name already exist");

//        if(!farmerFamilyList.isEmpty() && farmerFamilyList.stream().filter(FarmerFamily::getActive).findAny().isPresent()){
//            throw new ValidationException("FarmerFamily name already exist");
//        }
//        if(!farmerFamilyList.isEmpty() && farmerFamilyList.stream().filter(Predicate.not(FarmerFamily::getActive)).findAny().isPresent()){
//            throw new ValidationException("FarmerFamily name already exist with inactive state");
//        }
        }else {
            farmerFamilyResponse = mapper.farmerFamilyEntityToObject(farmerFamilyRepository.save(farmerFamily), FarmerFamilyResponse.class);
            farmerFamilyResponse.setError(false);
        }
        return farmerFamilyResponse;
//        return mapper.farmerFamilyEntityToObject(farmerFamilyRepository.save(farmerFamily),FarmerFamilyResponse.class);
    }

    public Map<String,Object> getPaginatedFarmerFamilyDetails(final Pageable pageable){
        return convertToMapResponse(farmerFamilyRepository.findByActiveOrderByFarmerFamilyIdAsc( true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<FarmerFamily> activeFarmerFamilys) {
        Map<String, Object> response = new HashMap<>();

        List<FarmerFamilyResponse> farmerFamilyResponses = activeFarmerFamilys.getContent().stream()
                .map(farmerFamily -> mapper.farmerFamilyEntityToObject(farmerFamily,FarmerFamilyResponse.class)).collect(Collectors.toList());
        response.put("farmerFamily",farmerFamilyResponses);
        response.put("currentPage", activeFarmerFamilys.getNumber());
        response.put("totalItems", activeFarmerFamilys.getTotalElements());
        response.put("totalPages", activeFarmerFamilys.getTotalPages());

        return response;
    }

    @Transactional
    public FarmerFamilyResponse deleteFarmerFamilyDetails(long id) {
        FarmerFamilyResponse farmerFamilyResponse = new FarmerFamilyResponse();
        FarmerFamily farmerFamily = farmerFamilyRepository.findByFarmerFamilyIdAndActive(id, true);
        if (Objects.nonNull(farmerFamily)) {
            farmerFamily.setActive(false);
            farmerFamilyResponse = mapper.farmerFamilyEntityToObject(farmerFamilyRepository.save(farmerFamily), FarmerFamilyResponse.class);
            farmerFamilyResponse.setError(false);
        } else {
            farmerFamilyResponse.setError(true);
            farmerFamilyResponse.setError_description("Invalid Id");
            // throw new ValidationException("Invalid Id");
        }
        return farmerFamilyResponse;
    }

    public FarmerFamilyResponse getById(int id){
        FarmerFamilyResponse farmerFamilyResponse = new FarmerFamilyResponse();
        FarmerFamily farmerFamily = farmerFamilyRepository.findByFarmerFamilyIdAndActive(id,true);
        if(farmerFamily == null){
            farmerFamilyResponse.setError(true);
            farmerFamilyResponse.setError_description("Invalid id");
        }else{
            farmerFamilyResponse =  mapper.farmerFamilyEntityToObject(farmerFamily,FarmerFamilyResponse.class);
            farmerFamilyResponse.setError(false);
        }
        log.info("Entity is ",farmerFamily);
        return farmerFamilyResponse;
    }

    public Map<String,Object> getByFarmerId(int farmerId){
        Map<String, Object> response = new HashMap<>();
        List<FarmerFamily> familyList = farmerFamilyRepository.findByFarmerIdAndActive(farmerId, true);
        if(familyList.isEmpty()){
            response.put("error","Error");
            response.put("error_description","Invalid id");
            return response;
        }else {
            log.info("Entity is ", familyList);
            response = convertListToMapResponse(familyList);
            return response;
        }
    }

    public FarmerFamilyResponse getByIdJoin(int id){
        FarmerFamilyDTO farmerFamilyDTO = farmerFamilyRepository.getByFarmerFamilyIdAndActive(id,true);
        if(farmerFamilyDTO == null){
            throw new ValidationException("Invalid Id");
        }
        // log.info("Entity is ", farmerAddressDTO);
        return mapper.farmerFamilyDTOToObject(farmerFamilyDTO, FarmerFamilyResponse.class);
    }


    @Transactional
    public FarmerFamilyResponse updateFarmerFamilyDetails(EditFarmerFamilyRequest farmerFamilyRequest){
        FarmerFamilyResponse farmerFamilyResponse = new FarmerFamilyResponse();

//        List<FarmerFamily> farmerFamilyList = farmerFamilyRepository.findByFarmerFamilyNameAndFarmerIdAndActive(farmerFamilyRequest.getFarmerFamilyName(),farmerFamilyRequest.getFarmerId(),true);
//        if(farmerFamilyList.size()>0){
//            farmerFamilyResponse.setError(true);
//            farmerFamilyResponse.setError_description("FarmerFamily already exists, duplicates are not allowed.");
//            // throw new ValidationException("Village already exists, duplicates are not allowed.");
//        }else {

            FarmerFamily farmerFamily = farmerFamilyRepository.findByFarmerFamilyIdAndActiveIn(farmerFamilyRequest.getFarmerFamilyId(), Set.of(true,false));
            if(Objects.nonNull(farmerFamily)){
                farmerFamily.setFarmerFamilyName(farmerFamilyRequest.getFarmerFamilyName());
                farmerFamily.setFarmerId(farmerFamilyRequest.getFarmerId());
                farmerFamily.setRelationshipId(farmerFamilyRequest.getRelationshipId());
                farmerFamily.setActive(true);
                FarmerFamily farmerFamily1 = farmerFamilyRepository.save(farmerFamily);
                farmerFamilyResponse = mapper.farmerFamilyEntityToObject(farmerFamily1, FarmerFamilyResponse.class);
                farmerFamilyResponse.setError(false);
            } else {
                farmerFamilyResponse.setError(true);
                farmerFamilyResponse.setError_description("Error occurred while fetching farmerFamily");
                // throw new ValidationException("Error occurred while fetching village");
            }
//        }
        return farmerFamilyResponse;
    }

    private Map<String, Object> convertListToMapResponse(List<FarmerFamily> familyList) {
        Map<String, Object> response = new HashMap<>();
        List<FarmerFamilyResponse> familyResponse = familyList.stream()
                .map(farmerFamily -> mapper.farmerFamilyEntityToObject(farmerFamily,FarmerFamilyResponse.class)).collect(Collectors.toList());
        response.put("farmerFamily", familyResponse);
        response.put("totalItems", familyList.size());
        return response;
    }

    public Map<String,Object> getByFarmerIdJoin(int farmerId){
        Map<String, Object> response = new HashMap<>();
        List<FarmerFamilyDTO> farmerFamilyDTO = farmerFamilyRepository.getByFarmerIdAndActive(farmerId, true);
        if(farmerFamilyDTO.isEmpty()){
            response.put("error","Error");
            response.put("error_description","Invalid id");
            return response;
        }else {
            log.info("Entity is ", farmerFamilyDTO);
            response = convertListDTOToMapResponse(farmerFamilyDTO);
            return response;
        }
    }

    private Map<String, Object> convertListDTOToMapResponse(List<FarmerFamilyDTO> farmerFamilyDTOList) {
        Map<String, Object> response = new HashMap<>();
        List<FarmerFamilyResponse> farmerFamilyResponse = farmerFamilyDTOList.stream()
                .map(farmerFamilyDTO -> mapper.farmerFamilyDTOToObject(farmerFamilyDTO, FarmerFamilyResponse.class)).collect(Collectors.toList());
        response.put("farmerFamily", farmerFamilyResponse);
        response.put("totalItems", farmerFamilyDTOList.size());
        return response;
    }


}