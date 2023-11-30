package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmerType.EditFarmerTypeRequest;
import com.sericulture.registration.model.api.farmerType.FarmerTypeRequest;
import com.sericulture.registration.model.api.farmerType.FarmerTypeResponse;
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
        FarmerType farmerType = mapper.farmerTypeObjectToEntity(farmerTypeRequest,FarmerType.class);
        validator.validate(farmerType);
        List<FarmerType> farmerTypeList = farmerTypeRepository.findByNameAndActive(farmerTypeRequest.getName(),true);
        if(!farmerTypeList.isEmpty()){
            throw new ValidationException("Farmer Type name already exist");
        }
//        if(!farmerTypeList.isEmpty() && farmerTypeList.stream().filter(FarmerType::getActive).findAny().isPresent()){
//            throw new ValidationException("FarmerType name already exist");
//        }
//        if(!farmerTypeList.isEmpty() && farmerTypeList.stream().filter(Predicate.not(FarmerType::getActive)).findAny().isPresent()){
//            throw new ValidationException("FarmerType name already exist with inactive state");
//        }
        return mapper.farmerTypeEntityToObject(farmerTypeRepository.save(farmerType),FarmerTypeResponse.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getPaginatedFarmerTypeDetails(final Pageable pageable){
        return convertToMapResponse(farmerTypeRepository.findByActiveOrderByFarmerTypeIdAsc( true, pageable));
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
    public void deleteFarmerTypeDetails(long id) {
        FarmerType farmerType = farmerTypeRepository.findByFarmerTypeIdAndActive(id, true);
        if (Objects.nonNull(farmerType)) {
            farmerType.setActive(false);
            farmerTypeRepository.save(farmerType);
        } else {
            throw new ValidationException("Invalid Id");
        }
    }

    @Transactional
    public FarmerTypeResponse getById(int id){
        FarmerType farmerType = farmerTypeRepository.findByFarmerTypeIdAndActive(id,true);
        if(farmerType == null){
            throw new ValidationException("Invalid Id");
        }
        log.info("Entity is ",farmerType);
        return mapper.farmerTypeEntityToObject(farmerType,FarmerTypeResponse.class);
    }

    @Transactional
    public FarmerTypeResponse updateFarmerTypeDetails(EditFarmerTypeRequest farmerTypeRequest){
        List<FarmerType> farmerTypeList = farmerTypeRepository.findByName(farmerTypeRequest.getName());
        if(farmerTypeList.size()>0){
            throw new ValidationException("FarmerType already exists with this name, duplicates are not allowed.");
        }

        FarmerType farmerType = farmerTypeRepository.findByFarmerTypeIdAndActiveIn(farmerTypeRequest.getFarmerTypeId(), Set.of(true,false));
        if(Objects.nonNull(farmerType)){
            farmerType.setName(farmerTypeRequest.getName());
            farmerType.setActive(true);
        }else{
            throw new ValidationException("Error occurred while fetching farmerType");
        }
        return mapper.farmerTypeEntityToObject(farmerTypeRepository.save(farmerType),FarmerTypeResponse.class);
    }

}