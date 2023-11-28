package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmer.*;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressResponse;
import com.sericulture.registration.model.dto.farmer.FarmerAddressDTO;
import com.sericulture.registration.model.entity.*;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.*;
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
    FarmerAddressRepository farmerAddressRepository;

    @Autowired
    FarmerLandDetailsRepository farmerLandDetailsRepository;

    @Autowired
    FarmerFamilyRepository farmerFamilyRepository;

    @Autowired
    FarmerBankAccountRepository farmerBankAccountRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

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
        /*List<Farmer> farmerList = farmerRepository.findByFarmerNumber(farmerRequest.getFarmerNumber());
        if(farmerList.size()>0){
            throw new ValidationException("farmer already exists with this name, duplicates are not allowed.");
        }
*/
        Farmer farmer = farmerRepository.findByFarmerIdAndActiveIn(farmerRequest.getFarmerId(), Set.of(true,false));
        if(Objects.nonNull(farmer)){
            farmer.setFarmerNumber(farmerRequest.getFarmerNumber());
            farmer.setFruitsId(farmerRequest.getFruitsId());
            farmer.setFirstName(farmerRequest.getFirstName());
            farmer.setMiddleName(farmerRequest.getMiddleName());
            farmer.setLastName(farmerRequest.getLastName());
            farmer.setDob(farmerRequest.getDob());
            farmer.setGenderId(farmerRequest.getGenderId());
            farmer.setGenderId(farmerRequest.getGenderId());
            farmer.setCasteId(farmerRequest.getCasteId());
            farmer.setDifferentlyAbled(farmerRequest.getDifferentlyAbled());
            farmer.setEmail(farmerRequest.getEmail());
            farmer.setMobileNumber(farmerRequest.getMobileNumber());
            farmer.setAadhaarNumber(farmerRequest.getAadhaarNumber());
            farmer.setEpicNumber(farmerRequest.getEpicNumber());
            farmer.setRationCardNumber(farmerRequest.getRationCardNumber());
            farmer.setTotalLandHolding(farmerRequest.getTotalLandHolding());
            farmer.setPassbookNumber(farmerRequest.getPassbookNumber());
            farmer.setLandHoldingCategoryId(farmerRequest.getLandHoldingCategoryId());
            farmer.setEducationId(farmerRequest.getEducationId());
            farmer.setRepresentativeId(farmerRequest.getRepresentativeId());
            farmer.setKhazaneRecipientId(farmerRequest.getKhazaneRecipientId());
            farmer.setPhotoPath(farmerRequest.getPhotoPath());
            farmer.setActive(true);
        }else{
            throw new ValidationException("Error occurred while fetching farmer");
        }
        return mapper.farmerEntityToObject(farmerRepository.save(farmer),FarmerResponse.class);
    }

    @Transactional
    public GetFarmerResponse getFarmerDetails(GetFarmerRequest getFarmerRequest){
        Farmer farmer = farmerRepository.findByFruitsIdAndActive(getFarmerRequest.getFruitsId(),true);
        if(farmer == null){
            throw new ValidationException("Invalid fruits id");
        }
        List<FarmerAddress> farmerAddressList = farmerAddressRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
        List<FarmerAddressDTO> farmerAddressDTOList = farmerAddressRepository.getByFarmerIdAndActive(farmer.getFarmerId(), true);
        List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
        List<FarmerFamily> farmerFamilyList = farmerFamilyRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
        FarmerBankAccount farmerBankAccount = farmerBankAccountRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);

        GetFarmerResponse getFarmerResponse = new GetFarmerResponse();
        getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer,FarmerResponse.class));
        getFarmerResponse.setFarmerAddressList(farmerAddressList);
        getFarmerResponse.setFarmerAddressDTOList(farmerAddressDTOList);
        getFarmerResponse.setFarmerFamilyList(farmerFamilyList);
        getFarmerResponse.setFarmerLandDetailsList(farmerLandDetailsList);
        getFarmerResponse.setFarmerBankAccount(farmerBankAccount);

        return getFarmerResponse;
    }

}
