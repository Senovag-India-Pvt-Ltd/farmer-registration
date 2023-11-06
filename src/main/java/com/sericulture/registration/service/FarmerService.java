package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmer.EditFarmerRequest;
import com.sericulture.registration.model.api.farmer.FarmerRequest;
import com.sericulture.registration.model.api.farmer.FarmerResponse;
import com.sericulture.registration.model.entity.Farmer;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.FarmerRepository;
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
public class FarmerService {

    @Autowired
    FarmerRepository farmerRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FarmerResponse getFarmerDetails(String farmerNumber){
        Farmer farmer = null;
        if(farmer==null){
            farmer = farmerRepository.findByFarmerNumberAndActive(farmerNumber,true);
        }
        log.info("Entity is ",farmer);
        return mapper.farmerEntityToObject(farmer,FarmerResponse.class);
    }

    @Transactional
    public FarmerResponse insertFarmerDetails(FarmerRequest farmerRequest){
        Farmer farmer = mapper.farmerObjectToEntity(farmerRequest,Farmer.class);
        validator.validate(farmer);
        List<Farmer> farmerList = farmerRepository.findByFarmerNumber(farmerRequest.getFarmerNumber());
        if(!farmerList.isEmpty() && farmerList.stream().filter(Farmer::getActive).findAny().isPresent()){
            throw new ValidationException("Farmer number already exist");
        }
        if(!farmerList.isEmpty() && farmerList.stream().filter(Predicate.not(Farmer::getActive)).findAny().isPresent()){
            throw new ValidationException("Farmer number already exist with inactive farmer");
        }
        return mapper.farmerEntityToObject(farmerRepository.save(farmer),FarmerResponse.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getPaginatedFarmerDetails(final Pageable pageable){
        return convertToMapResponse(farmerRepository.findByActiveOrderByFarmerIdAsc( true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<Farmer> activeFarmers) {
        Map<String, Object> response = new HashMap<>();

        List<FarmerResponse> farmerResponses = activeFarmers.getContent().stream()
                .map(farmer -> mapper.farmerEntityToObject(farmer,FarmerResponse.class)).collect(Collectors.toList());
        response.put("farmer",farmerResponses);
        response.put("currentPage", activeFarmers.getNumber());
        response.put("totalItems", activeFarmers.getTotalElements());
        response.put("totalPages", activeFarmers.getTotalPages());

        return response;
    }

    @Transactional
    public void deleteFarmerDetails(long id) {
        Farmer farmer = farmerRepository.findByFarmerIdAndActive(id, true);
        if (Objects.nonNull(farmer)) {
            farmer.setActive(false);
            farmerRepository.save(farmer);
        } else {
            throw new ValidationException("Invalid Id");
        }
    }

    @Transactional
    public FarmerResponse getById(int id){
        Farmer farmer = farmerRepository.findByFarmerIdAndActive(id,true);
        if(farmer == null){
            throw new ValidationException("Invalid Id");
        }
        log.info("Entity is ",farmer);
        return mapper.farmerEntityToObject(farmer,FarmerResponse.class);
    }

    @Transactional
    public FarmerResponse updateFarmerDetails(EditFarmerRequest farmerRequest){
        List<Farmer> farmerList = farmerRepository.findByFarmerNumber(farmerRequest.getFarmerNumber());
        if(farmerList.size()>0){
            throw new ValidationException("farmer already exists with this name, duplicates are not allowed.");
        }

        Farmer farmer = farmerRepository.findByFarmerIdAndActiveIn(farmerRequest.getFarmerId(), Set.of(true,false));
        if(Objects.nonNull(farmer)){
            farmer.setFarmerNumber(farmerRequest.getFarmerNumber());
            farmer.setActive(true);
        }else{
            throw new ValidationException("Error occurred while fetching farmer");
        }
        return mapper.farmerEntityToObject(farmerRepository.save(farmer),FarmerResponse.class);
    }

}
