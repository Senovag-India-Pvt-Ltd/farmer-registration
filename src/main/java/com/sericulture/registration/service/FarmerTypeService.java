package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmerFamily.FarmerFamilyResponse;
import com.sericulture.registration.model.api.farmerType.EditFarmerTypeRequest;
import com.sericulture.registration.model.api.farmerType.FarmerTypeRequest;
import com.sericulture.registration.model.api.farmerType.FarmerTypeResponse;
import com.sericulture.registration.model.entity.FarmerFamily;
import com.sericulture.registration.model.entity.FarmerType;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.FarmerTypeRepository;
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
public class FarmerTypeService {

    @Autowired
    FarmerTypeRepository farmerTypeRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Transactional
    public FarmerTypeResponse insertFarmerTypeDetails(FarmerTypeRequest farmerTypeRequest){
        FarmerTypeResponse farmerTypeResponse = new FarmerTypeResponse();
        FarmerType farmerType = mapper.farmerTypeObjectToEntity(farmerTypeRequest,FarmerType.class);
        validator.validate(farmerType);
        List<FarmerType> farmerTypeList = farmerTypeRepository.findByFarmerTypeNameAndFarmerTypeNameInKannada(farmerTypeRequest.getFarmerTypeName(),farmerTypeRequest.getFarmerTypeNameInKannada());
        if(!farmerTypeList.isEmpty()){
            farmerTypeResponse.setError(true);
            farmerTypeResponse.setError_description("Farmer Type name already exist");
//        if(!farmerTypeList.isEmpty() && farmerTypeList.stream().filter(FarmerType::getActive).findAny().isPresent()){
//            throw new ValidationException("FarmerType name already exist");
//        }
//        if(!farmerTypeList.isEmpty() && farmerTypeList.stream().filter(Predicate.not(FarmerType::getActive)).findAny().isPresent()){
//            throw new ValidationException("FarmerType name already exist with inactive state");
//        }
//        return mapper.farmerTypeEntityToObject(farmerTypeRepository.save(farmerType),FarmerTypeResponse.class);
        }else {
            farmerTypeResponse = mapper.farmerTypeEntityToObject(farmerTypeRepository.save(farmerType), FarmerTypeResponse.class);
            farmerTypeResponse.setError(false);
        }
        return farmerTypeResponse;
    }

//    @Transactional
//    public FarmerTypeResponse insertFarmerTypeDetails(FarmerTypeRequest farmerTypeRequest){
//        FarmerTypeResponse farmerTypeResponse = new FarmerTypeResponse();
//        FarmerType farmerType = mapper.farmerTypeObjectToEntity(farmerTypeRequest,FarmerType.class);
//        validator.validate(farmerType);
//        List<FarmerType> farmerTypeList = farmerTypeRepository.findByFarmerTypeName(farmerTypeRequest.getFarmerTypeName());
//        if(!farmerTypeList.isEmpty() && farmerTypeList.stream().filter(FarmerType::getActive).findAny().isPresent()){
//            farmerTypeResponse.setError(true);
//            farmerTypeResponse.setError_description("Farmer Type name already exist");
//        }
//       else if(!farmerTypeList.isEmpty() && farmerTypeList.stream().filter(Predicate.not(FarmerType::getActive)).findAny().isPresent()){
//            farmerTypeResponse.setError(true);
//            farmerTypeResponse.setError_description("Farmer Type name already exist with inactive state");
//        }else {
//            farmerTypeResponse = mapper.farmerTypeEntityToObject(farmerTypeRepository.save(farmerType), FarmerTypeResponse.class);
//            farmerTypeResponse.setError(false);
//        }
//        return farmerTypeResponse;
//    }



    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getPaginatedFarmerTypeDetails(final Pageable pageable){
        return convertToMapResponse(farmerTypeRepository.findByActiveOrderByFarmerTypeNameAsc( true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<FarmerType> activeFarmerTypes) {
        Map<String, Object> response = new HashMap<>();

        List<FarmerTypeResponse> farmerTypeResponses = activeFarmerTypes.getContent().stream()
                .map(farmerType -> mapper.farmerTypeEntityToObject(farmerType,FarmerTypeResponse.class)).collect(Collectors.toList());
        response.put("farmerType",farmerTypeResponses);
        response.put("currentPage", activeFarmerTypes.getNumber());
        response.put("totalItems", activeFarmerTypes.getTotalElements());
        response.put("totalPages", activeFarmerTypes.getTotalPages());

        return response;
    }

    @Transactional
    public FarmerTypeResponse deleteFarmerTypeDetails(long id) {
        FarmerTypeResponse farmerTypeResponse = new FarmerTypeResponse();
        FarmerType farmerType = farmerTypeRepository.findByFarmerTypeIdAndActive(id, true);
        if (Objects.nonNull(farmerType)) {
            farmerType.setActive(false);
            farmerTypeResponse = mapper.farmerTypeEntityToObject(farmerTypeRepository.save(farmerType), FarmerTypeResponse.class);
            farmerTypeResponse.setError(false);
        } else {
            farmerTypeResponse.setError(true);
            farmerTypeResponse.setError_description("Invalid Id");
            // throw new ValidationException("Invalid Id");
        }
        return farmerTypeResponse;
    }

    @Transactional
    public FarmerTypeResponse getById(int id){
        FarmerTypeResponse farmerTypeResponse = new FarmerTypeResponse();
        FarmerType farmerType = farmerTypeRepository.findByFarmerTypeIdAndActive(id,true);
        if(farmerType == null){
            farmerTypeResponse.setError(true);
            farmerTypeResponse.setError_description("Invalid id");
        }else{
            farmerTypeResponse =  mapper.farmerTypeEntityToObject(farmerType,FarmerTypeResponse.class);
            farmerTypeResponse.setError(false);
        }
        log.info("Entity is ",farmerType);
        return farmerTypeResponse;
    }

    @Transactional
    public FarmerTypeResponse updateFarmerTypeDetails(EditFarmerTypeRequest farmerTypeRequest) {
        FarmerTypeResponse farmerTypeResponse = new FarmerTypeResponse();
        List<FarmerType> farmerTypeList = farmerTypeRepository.findByFarmerTypeNameAndFarmerTypeNameInKannada(farmerTypeRequest.getFarmerTypeName(),farmerTypeRequest.getFarmerTypeNameInKannada());
        if (farmerTypeList.size() > 0) {
            farmerTypeResponse.setError(true);
            farmerTypeResponse.setError_description("Farmer Type already exists, duplicates are not allowed.");
            // throw new ValidationException("Village already exists, duplicates are not allowed.");
        } else {

            FarmerType farmerType = farmerTypeRepository.findByFarmerTypeIdAndActiveIn(farmerTypeRequest.getFarmerTypeId(), Set.of(true, false));
            if (Objects.nonNull(farmerType)) {
                farmerType.setFarmerTypeName(farmerTypeRequest.getFarmerTypeName());
                farmerType.setFarmerTypeNameInKannada(farmerTypeRequest.getFarmerTypeNameInKannada());
                FarmerType farmerType1 = farmerTypeRepository.save(farmerType);
                farmerTypeResponse = mapper.farmerTypeEntityToObject(farmerType1, FarmerTypeResponse.class);
                farmerTypeResponse.setError(false);
            } else {
                farmerTypeResponse.setError(true);
                farmerTypeResponse.setError_description("Error occurred while fetching farmer type");
                // throw new ValidationException("Error occurred while fetching village");
            }
        }
        return farmerTypeResponse;
    }
}