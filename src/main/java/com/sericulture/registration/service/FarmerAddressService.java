package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmerAddress.EditFarmerAddressRequest;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressRequest;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressResponse;
import com.sericulture.registration.model.api.farmerFamily.FarmerFamilyResponse;
import com.sericulture.registration.model.dto.farmer.FarmerAddressDTO;
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
        FarmerAddressResponse farmerAddressResponse = new FarmerAddressResponse();
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
    public FarmerAddressResponse deleteFarmerAddressDetails(long id) {
        FarmerAddressResponse farmerAddressResponse = new FarmerAddressResponse();
        FarmerAddress farmerAddress = farmerAddressRepository.findByFarmerAddressIdAndActive(id, true);
        if (Objects.nonNull(farmerAddress)) {
            farmerAddress.setActive(false);
            farmerAddressResponse = mapper.farmerAddressEntityToObject(farmerAddressRepository.save(farmerAddress), FarmerAddressResponse.class);
            farmerAddressResponse.setError(false);
        } else {
            farmerAddressResponse.setError(true);
            farmerAddressResponse.setError_description("Invalid Id");
            // throw new ValidationException("Invalid Id");
        }
        return farmerAddressResponse;
    }

    public FarmerAddressResponse getById(int id){
        FarmerAddressResponse farmerAddressResponse = new FarmerAddressResponse();
        FarmerAddress farmerAddress = farmerAddressRepository.findByFarmerAddressIdAndActive(id,true);
        if(farmerAddress == null){
            farmerAddressResponse.setError(true);
            farmerAddressResponse.setError_description("Invalid id");
        }else{
            farmerAddressResponse =  mapper.farmerAddressEntityToObject(farmerAddress,FarmerAddressResponse.class);
            farmerAddressResponse.setError(false);
        }
        log.info("Entity is ",farmerAddress);
        return farmerAddressResponse;
    }

    public Map<String,Object> getByFarmerId(int farmerId){
        Map<String, Object> response = new HashMap<>();
        List<FarmerAddress> farmerAddress = farmerAddressRepository.findByFarmerIdAndActive(farmerId, true);
        if(farmerAddress.isEmpty()){
            response.put("error","Error");
            response.put("error_description","Invalid id");
            return response;
        }else {
            log.info("Entity is ", farmerAddress);
            response = convertListToMapResponse(farmerAddress);
            return response;
        }
    }

    public FarmerAddressResponse getByIdJoin(int id){
        FarmerAddressResponse farmerAddressResponse = new FarmerAddressResponse();
        FarmerAddressDTO farmerAddressDTO = farmerAddressRepository.getByFarmerAddressIdAndActive(id,true);
        if(farmerAddressDTO == null){
            farmerAddressResponse.setError(true);
            farmerAddressResponse.setError_description("Invalid id");
        } else {
            farmerAddressResponse = mapper.farmerAddressDTOToObject(farmerAddressDTO, FarmerAddressResponse.class);
            farmerAddressResponse.setError(false);
        }
        log.info("Entity is ", farmerAddressDTO);
        return farmerAddressResponse;
    }

    @Transactional
    public FarmerAddressResponse updateFarmerAddressDetails(EditFarmerAddressRequest farmerAddressRequest){
        FarmerAddressResponse farmerAddressResponse = new FarmerAddressResponse();
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
            FarmerAddress farmerAddress1 = farmerAddressRepository.save(farmerAddress);
            farmerAddressResponse = mapper.farmerAddressEntityToObject(farmerAddress1, FarmerAddressResponse.class);
            farmerAddressResponse.setError(false);
        } else {
            farmerAddressResponse.setError(true);
            farmerAddressResponse.setError_description("Error occurred while fetching farmerAddress");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return farmerAddressResponse;
    }

    private Map<String, Object> convertListToMapResponse(List<FarmerAddress> farmerAddressList) {
        Map<String, Object> response = new HashMap<>();
        List<FarmerAddressResponse> farmerAddressResponse = farmerAddressList.stream()
                .map(farmerAddress -> mapper.farmerAddressEntityToObject(farmerAddress, FarmerAddressResponse.class)).collect(Collectors.toList());
        response.put("farmerAddress", farmerAddressResponse);
        response.put("totalItems", farmerAddressList.size());
        return response;
    }


    public Map<String,Object> getByFarmerIdJoin(int farmerId){
        Map<String, Object> response = new HashMap<>();
        List<FarmerAddressDTO> farmerAddressDTO = farmerAddressRepository.getByFarmerIdAndActive(farmerId, true);
        if(farmerAddressDTO.isEmpty()){
            response.put("error","Error");
            response.put("error_description","Invalid id");
            return response;
        }else {
            log.info("Entity is ", farmerAddressDTO);
            response = convertListDTOToMapResponse(farmerAddressDTO);
            return response;
        }
    }

    private Map<String, Object> convertListDTOToMapResponse(List<FarmerAddressDTO> farmerAddressDTOList) {
        Map<String, Object> response = new HashMap<>();
        List<FarmerAddressResponse> farmerAddressResponse = farmerAddressDTOList.stream()
                .map(farmerAddressDTO -> mapper.farmerAddressDTOToObject(farmerAddressDTO, FarmerAddressResponse.class)).collect(Collectors.toList());
        response.put("farmerAddress", farmerAddressResponse);
        response.put("totalItems", farmerAddressDTOList.size());
        return response;
    }

}