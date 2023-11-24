package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmerAddress.EditFarmerAddressRequest;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressRequest;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressResponse;
import com.sericulture.registration.model.api.farmerFamily.FarmerFamilyResponse;
import com.sericulture.registration.model.entity.FarmerAddress;
import com.sericulture.registration.model.entity.FarmerFamily;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.FarmerAddressRepository;
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
public class FarmerAddressService {

    @Autowired
    FarmerAddressRepository farmerAddressRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Transactional
    public FarmerAddressResponse insertFarmerAddressDetails(FarmerAddressRequest farmerAddressRequest){
        FarmerAddress farmerAddress = mapper.farmerAddressObjectToEntity(farmerAddressRequest,FarmerAddress.class);
        validator.validate(farmerAddress);
        /*List<FarmerAddress> farmerAddressList = farmerAddressRepository.findByFarmerAddressName(farmerAddressRequest.getFarmerAddressName());
        if(!farmerAddressList.isEmpty() && farmerAddressList.stream().filter(FarmerAddress::getActive).findAny().isPresent()){
            throw new ValidationException("FarmerAddress name already exist");
        }
        if(!farmerAddressList.isEmpty() && farmerAddressList.stream().filter(Predicate.not(FarmerAddress::getActive)).findAny().isPresent()){
            throw new ValidationException("FarmerAddress name already exist with inactive state");
        }*/
        return mapper.farmerAddressEntityToObject(farmerAddressRepository.save(farmerAddress),FarmerAddressResponse.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getPaginatedFarmerAddressDetails(final Pageable pageable){
        return convertToMapResponse(farmerAddressRepository.findByActiveOrderByFarmerAddressIdAsc( true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<FarmerAddress> activeFarmerAddresss) {
        Map<String, Object> response = new HashMap<>();

        List<FarmerAddressResponse> farmerAddressResponses = activeFarmerAddresss.getContent().stream()
                .map(farmerAddress -> mapper.farmerAddressEntityToObject(farmerAddress,FarmerAddressResponse.class)).collect(Collectors.toList());
        response.put("farmerAddress",farmerAddressResponses);
        response.put("currentPage", activeFarmerAddresss.getNumber());
        response.put("totalItems", activeFarmerAddresss.getTotalElements());
        response.put("totalPages", activeFarmerAddresss.getTotalPages());

        return response;
    }

    @Transactional
    public void deleteFarmerAddressDetails(long id) {
        FarmerAddress farmerAddress = farmerAddressRepository.findByFarmerAddressIdAndActive(id, true);
        if (Objects.nonNull(farmerAddress)) {
            farmerAddress.setActive(false);
            farmerAddressRepository.save(farmerAddress);
        } else {
            throw new ValidationException("Invalid Id");
        }
    }

    @Transactional
    public FarmerAddressResponse getById(int id){
        FarmerAddress farmerAddress = farmerAddressRepository.findByFarmerAddressIdAndActive(id,true);
        if(farmerAddress == null){
            throw new ValidationException("Invalid Id");
        }
        log.info("Entity is ",farmerAddress);
        return mapper.farmerAddressEntityToObject(farmerAddress,FarmerAddressResponse.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getByFarmerId(int farmerId){
        List<FarmerAddress> familyList = farmerAddressRepository.findByFarmerIdAndActive(farmerId, true);
        if(familyList.isEmpty()){
            throw new ValidationException("Farmer Address not found by Farmer Id");
        }
        return convertListToMapResponse(familyList);
    }

    @Transactional
    public FarmerAddressResponse updateFarmerAddressDetails(EditFarmerAddressRequest farmerAddressRequest){
       /* List<FarmerAddress> farmerAddressList = farmerAddressRepository.findByFarmerAddressName(farmerAddressRequest.getFarmerAddressName());
        if(farmerAddressList.size()>0){
            throw new ValidationException("FarmerAddress already exists with this name, duplicates are not allowed.");
        }*/

        FarmerAddress farmerAddress = farmerAddressRepository.findByFarmerAddressIdAndActiveIn(farmerAddressRequest.getFarmerAddressId(), Set.of(true,false));
        if(Objects.nonNull(farmerAddress)){
            farmerAddress.setFarmerId(farmerAddressRequest.getFarmerId());
            farmerAddress.setStateId(farmerAddressRequest.getStateId());
            farmerAddress.setDistrictId(farmerAddressRequest.getDistrictId());
            farmerAddress.setTalukId(farmerAddressRequest.getTalukId());
            farmerAddress.setHobliId(farmerAddressRequest.getHobliId());
            farmerAddress.setVillageId(farmerAddressRequest.getVillageId());
            farmerAddress.setAddressText(farmerAddressRequest.getAddressText());
            farmerAddress.setPincode(farmerAddressRequest.getPincode());
            farmerAddress.setDefaultAddress(farmerAddressRequest.getDefaultAddress());
            farmerAddress.setActive(true);
        }else{
            throw new ValidationException("Error occurred while fetching farmerAddress");
        }
        return mapper.farmerAddressEntityToObject(farmerAddressRepository.save(farmerAddress),FarmerAddressResponse.class);
    }

    private Map<String, Object> convertListToMapResponse(List<FarmerAddress> familyList) {
        Map<String, Object> response = new HashMap<>();
        List<FarmerAddressResponse> farmerAddressResponse = familyList.stream()
                .map(farmerFamily -> mapper.farmerAddressEntityToObject(farmerFamily, FarmerAddressResponse.class)).collect(Collectors.toList());
        response.put("farmerFamily", farmerAddressResponse);
        response.put("totalItems", familyList.size());
        return response;
    }

}