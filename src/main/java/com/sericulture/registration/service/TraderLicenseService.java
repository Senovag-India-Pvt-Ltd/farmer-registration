package com.sericulture.registration.service;

import com.sericulture.registration.model.api.traderLicense.EditTraderLicenseRequest;
import com.sericulture.registration.model.api.traderLicense.TraderLicenseRequest;
import com.sericulture.registration.model.api.traderLicense.TraderLicenseResponse;
import com.sericulture.registration.model.entity.TraderLicense;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.TraderLicenseRepository;
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
public class TraderLicenseService {

    @Autowired
    TraderLicenseRepository traderLicenseRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Transactional
    public TraderLicenseResponse insertTraderLicenseDetails(TraderLicenseRequest traderLicenseRequest){
        TraderLicense traderLicense = mapper.traderLicenseObjectToEntity(traderLicenseRequest,TraderLicense.class);
        validator.validate(traderLicense);
        /*List<TraderLicense> traderLicenseList = traderLicenseRepository.findByTraderLicenseNumber(traderLicenseRequest.getTraderLicenseNumber());
        if(!traderLicenseList.isEmpty() && traderLicenseList.stream().filter(TraderLicense::getActive).findAny().isPresent()){
            throw new ValidationException("TraderLicense number already exist");
        }
        if(!traderLicenseList.isEmpty() && traderLicenseList.stream().filter(Predicate.not(TraderLicense::getActive)).findAny().isPresent()){
            throw new ValidationException("TraderLicense number already exist with inactive traderLicense");
        }*/
        return mapper.traderLicenseEntityToObject(traderLicenseRepository.save(traderLicense),TraderLicenseResponse.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getPaginatedTraderLicenseDetails(final Pageable pageable){
        return convertToMapResponse(traderLicenseRepository.findByActiveOrderByTraderLicenseIdAsc( true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<TraderLicense> activeTraderLicenses) {
        Map<String, Object> response = new HashMap<>();

        List<TraderLicenseResponse> traderLicenseResponses = activeTraderLicenses.getContent().stream()
                .map(traderLicense -> mapper.traderLicenseEntityToObject(traderLicense,TraderLicenseResponse.class)).collect(Collectors.toList());
        response.put("traderLicense",traderLicenseResponses);
        response.put("currentPage", activeTraderLicenses.getNumber());
        response.put("totalItems", activeTraderLicenses.getTotalElements());
        response.put("totalPages", activeTraderLicenses.getTotalPages());

        return response;
    }

    @Transactional
    public void deleteTraderLicenseDetails(long id) {
        TraderLicense traderLicense = traderLicenseRepository.findByTraderLicenseIdAndActive(id, true);
        if (Objects.nonNull(traderLicense)) {
            traderLicense.setActive(false);
            traderLicenseRepository.save(traderLicense);
        } else {
            throw new ValidationException("Invalid Id");
        }
    }

    @Transactional
    public TraderLicenseResponse getById(int id){
        TraderLicense traderLicense = traderLicenseRepository.findByTraderLicenseIdAndActive(id,true);
        if(traderLicense == null){
            throw new ValidationException("Invalid Id");
        }
        log.info("Entity is ",traderLicense);
        return mapper.traderLicenseEntityToObject(traderLicense,TraderLicenseResponse.class);
    }

    @Transactional
    public TraderLicenseResponse updateTraderLicenseDetails(EditTraderLicenseRequest traderLicenseRequest){
        /*List<TraderLicense> traderLicenseList = traderLicenseRepository.findByTraderLicenseNumber(traderLicenseRequest.getTraderLicenseNumber());
        if(traderLicenseList.size()>0){
            throw new ValidationException("traderLicense already exists with this name, duplicates are not allowed.");
        }
*/
        TraderLicense traderLicense = traderLicenseRepository.findByTraderLicenseIdAndActiveIn(traderLicenseRequest.getTraderLicenseId(), Set.of(true,false));
        if(Objects.nonNull(traderLicense)){
            traderLicense.setArnNumber(traderLicenseRequest.getArnNumber());
            traderLicense.setTraderTypeId(traderLicenseRequest.getTraderTypeId());
            traderLicense.setFirstName(traderLicenseRequest.getFirstName());
            traderLicense.setMiddleName(traderLicenseRequest.getMiddleName());
            traderLicense.setLastName(traderLicenseRequest.getLastName());

            traderLicense.setFatherName(traderLicenseRequest.getFatherName());
            traderLicense.setStateId(traderLicenseRequest.getStateId());
            traderLicense.setDistrictId(traderLicenseRequest.getDistrictId());
            traderLicense.setAddress(traderLicenseRequest.getAddress());
            traderLicense.setPremisesDescription(traderLicenseRequest.getPremisesDescription());

            traderLicense.setApplicationDate(traderLicenseRequest.getApplicationDate());
            traderLicense.setApplicationNumber(traderLicenseRequest.getApplicationNumber());
            traderLicense.setTraderLicenseNumber(traderLicenseRequest.getLicenseChallanNumber());
            traderLicense.setRepresentativeDetails(traderLicenseRequest.getRepresentativeDetails());
            traderLicense.setLicenseFee(traderLicenseRequest.getLicenseFee());
            traderLicense.setLicenseChallanNumber(traderLicenseRequest.getLicenseChallanNumber());
            traderLicense.setGodownDetails(traderLicenseRequest.getGodownDetails());
            traderLicense.setSilkExchangeMahajar(traderLicenseRequest.getSilkExchangeMahajar());
           // traderLicense.setLicenseNumberSequence(traderLicenseRequest.getLicenseNumberSequence());

            traderLicense.setActive(true);
        }else{
            throw new ValidationException("Error occurred while fetching traderLicense");
        }
        return mapper.traderLicenseEntityToObject(traderLicenseRepository.save(traderLicense),TraderLicenseResponse.class);
    }

}
