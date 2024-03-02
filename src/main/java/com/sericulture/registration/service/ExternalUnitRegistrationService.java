package com.sericulture.registration.service;

import com.sericulture.registration.model.api.externalUnitRegistration.ExternalUnitRegistrationResponse;
import com.sericulture.registration.model.api.externalUnitRegistration.EditExternalUnitRegistrationRequest;
import com.sericulture.registration.model.api.externalUnitRegistration.ExternalUnitRegistrationRequest;
import com.sericulture.registration.model.api.externalUnitRegistration.ExternalUnitRegistrationResponse;
import com.sericulture.registration.model.api.traderLicense.TraderLicenseResponse;
import com.sericulture.registration.model.dto.externalUnitRegistration.ExternalUnitRegistrationDTO;
import com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO;
import com.sericulture.registration.model.entity.ExternalUnitRegistration;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.ExternalUnitRegistrationRepository;
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
public class ExternalUnitRegistrationService {

    @Autowired
    ExternalUnitRegistrationRepository externalUnitRegistrationRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Transactional
    public ExternalUnitRegistrationResponse insertExternalUnitRegistrationDetails(ExternalUnitRegistrationRequest externalUnitRegistrationRequest) {
        ExternalUnitRegistrationResponse externalUnitRegistrationResponse = new ExternalUnitRegistrationResponse();
        ExternalUnitRegistration externalUnitRegistration = mapper.externalUnitRegistrationObjectToEntity(externalUnitRegistrationRequest, ExternalUnitRegistration.class);
        validator.validate(externalUnitRegistration);
       /* List<ExternalUnitRegistration> externalUnitRegistrationList = externalUnitRegistrationRepository.findByExternalUnitRegistrationName(externalUnitRegistrationRequest.getExternalUnitRegistrationName());
        if(!externalUnitRegistrationList.isEmpty() && externalUnitRegistrationList.stream().filter(ExternalUnitRegistration::getActive).findAny().isPresent()){
            throw new ValidationException("ExternalUnitRegistration name already exist");
        }
        if(!externalUnitRegistrationList.isEmpty() && externalUnitRegistrationList.stream().filter(Predicate.not(ExternalUnitRegistration::getActive)).findAny().isPresent()){
            throw new ValidationException("ExternalUnitRegistration name already exist with inactive state");
        }*/
//        return mapper.externalUnitRegistrationEntityToObject(externalUnitRegistrationRepository.save(externalUnitRegistration),ExternalUnitRegistrationResponse.class);
        return mapper.externalUnitRegistrationEntityToObject(externalUnitRegistrationRepository.save(externalUnitRegistration), ExternalUnitRegistrationResponse.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> getPaginatedExternalUnitRegistrationDetails(final Pageable pageable) {
        return convertToMapResponse(externalUnitRegistrationRepository.findByActiveOrderByExternalUnitRegistrationIdAsc(true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<ExternalUnitRegistration> activeExternalUnitRegistrations) {
        Map<String, Object> response = new HashMap<>();

        List<ExternalUnitRegistrationResponse> externalUnitRegistrationResponses = activeExternalUnitRegistrations.getContent().stream()
                .map(externalUnitRegistration -> mapper.externalUnitRegistrationEntityToObject(externalUnitRegistration, ExternalUnitRegistrationResponse.class)).collect(Collectors.toList());
        response.put("externalUnitRegistration", externalUnitRegistrationResponses);
        response.put("currentPage", activeExternalUnitRegistrations.getNumber());
        response.put("totalItems", activeExternalUnitRegistrations.getTotalElements());
        response.put("totalPages", activeExternalUnitRegistrations.getTotalPages());

        return response;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getPaginatedExternalUnitRegistrationDetailsWithJoin(final Pageable pageable){
        return convertDTOToMapResponse(externalUnitRegistrationRepository.getByActiveOrderByExternalUnitRegistrationIdAsc( true, pageable));
    }

    private Map<String, Object> convertDTOToMapResponse(final Page<ExternalUnitRegistrationDTO> activeExternalUnitRegistrations) {
        Map<String, Object> response = new HashMap<>();

        List<ExternalUnitRegistrationResponse> externalUnitRegistrationResponses = activeExternalUnitRegistrations.getContent().stream()
                .map(externalUnitRegistration -> mapper.externalUnitRegistrationDTOToObject(externalUnitRegistration,ExternalUnitRegistrationResponse.class)).collect(Collectors.toList());
        response.put("externalUnitRegistration",externalUnitRegistrationResponses);
        response.put("currentPage", activeExternalUnitRegistrations.getNumber());
        response.put("totalItems", activeExternalUnitRegistrations.getTotalElements());
        response.put("totalPages", activeExternalUnitRegistrations.getTotalPages());
        return response;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getExternalUnitRegistrationByExternalUnitId(Long externalUnitTypeId){
        return convertListToMapResponse(externalUnitRegistrationRepository.findByExternalUnitTypeIdAndActive( externalUnitTypeId,true));
    }

    private Map<String, Object> convertListToMapResponse(final List<ExternalUnitRegistration> activeExternalUnitRegistrations) {
        Map<String, Object> response = new HashMap<>();

        List<ExternalUnitRegistrationResponse> externalUnitRegistrationResponses = activeExternalUnitRegistrations.stream()
                .map(externalUnitRegistration -> mapper.externalUnitRegistrationEntityToObject(externalUnitRegistration,ExternalUnitRegistrationResponse.class)).collect(Collectors.toList());
        response.put("externalUnitRegistration",externalUnitRegistrationResponses);
        response.put("totalItems", activeExternalUnitRegistrations.size());
        return response;
    }

    @Transactional
    public ExternalUnitRegistrationResponse deleteExternalUnitRegistrationDetails(long id) {
        ExternalUnitRegistrationResponse externalUnitRegistrationResponse = new ExternalUnitRegistrationResponse();
        ExternalUnitRegistration externalUnitRegistration = externalUnitRegistrationRepository.findByExternalUnitRegistrationIdAndActive(id, true);
        if (Objects.nonNull(externalUnitRegistration)) {
            externalUnitRegistration.setActive(false);
            externalUnitRegistrationResponse = mapper.externalUnitRegistrationEntityToObject(externalUnitRegistrationRepository.save(externalUnitRegistration), ExternalUnitRegistrationResponse.class);
            externalUnitRegistrationResponse.setError(false);
        } else {
            externalUnitRegistrationResponse.setError(true);
            externalUnitRegistrationResponse.setError_description("Invalid Id");
            // throw new ValidationException("Invalid Id");
        }
        return externalUnitRegistrationResponse;
    }

    @Transactional
    public ExternalUnitRegistrationResponse getById(int id) {
        ExternalUnitRegistrationResponse externalUnitRegistrationResponse = new ExternalUnitRegistrationResponse();
        ExternalUnitRegistration externalUnitRegistration = externalUnitRegistrationRepository.findByExternalUnitRegistrationIdAndActive(id, true);
        if (externalUnitRegistration == null) {
            externalUnitRegistrationResponse.setError(true);
            externalUnitRegistrationResponse.setError_description("Invalid id");
        } else {
            externalUnitRegistrationResponse = mapper.externalUnitRegistrationEntityToObject(externalUnitRegistration, ExternalUnitRegistrationResponse.class);
            externalUnitRegistrationResponse.setError(false);
        }
        log.info("Entity is ", externalUnitRegistration);
        return externalUnitRegistrationResponse;
    }

    @Transactional
    public ExternalUnitRegistrationResponse getByIdJoin(int id){
        ExternalUnitRegistrationResponse externalUnitRegistrationResponse = new ExternalUnitRegistrationResponse();
        ExternalUnitRegistrationDTO externalUnitRegistrationDTO = externalUnitRegistrationRepository.getByExternalUnitRegistrationIdAndActive(id,true);
        if(externalUnitRegistrationDTO == null){
            externalUnitRegistrationResponse.setError(true);
            externalUnitRegistrationResponse.setError_description("Invalid id");
        } else {
            externalUnitRegistrationResponse = mapper.externalUnitRegistrationDTOToObject(externalUnitRegistrationDTO, ExternalUnitRegistrationResponse.class);
            externalUnitRegistrationResponse.setError(false);
        }
        log.info("Entity is ", externalUnitRegistrationDTO);
        return externalUnitRegistrationResponse;
    }

    @Transactional
    public ExternalUnitRegistrationResponse updateExternalUnitRegistrationDetails(EditExternalUnitRegistrationRequest externalUnitRegistrationRequest) {
        ExternalUnitRegistrationResponse externalUnitRegistrationResponse = new ExternalUnitRegistrationResponse();
      /*  List<ExternalUnitRegistration> externalUnitRegistrationList = externalUnitRegistrationRepository.findByExternalUnitRegistrationName(externalUnitRegistrationRequest.getExternalUnitRegistrationName());
        if(externalUnitRegistrationList.size()>0){
            throw new ValidationException("ExternalUnitRegistration already exists with this name, duplicates are not allowed.");
        }*/

        ExternalUnitRegistration externalUnitRegistration = externalUnitRegistrationRepository.findByExternalUnitRegistrationIdAndActiveIn(externalUnitRegistrationRequest.getExternalUnitRegistrationId(), Set.of(true, false));
        if (Objects.nonNull(externalUnitRegistration)) {
            externalUnitRegistration.setAddress(externalUnitRegistrationRequest.getAddress());
            externalUnitRegistration.setName(externalUnitRegistrationRequest.getName());
            externalUnitRegistration.setLicenseNumber(externalUnitRegistrationRequest.getLicenseNumber());
            externalUnitRegistration.setExternalUnitNumber(externalUnitRegistrationRequest.getExternalUnitNumber());
            externalUnitRegistration.setExternalUnitTypeId(externalUnitRegistrationRequest.getExternalUnitTypeId());
            externalUnitRegistration.setOrganisationName(externalUnitRegistrationRequest.getOrganisationName());
            externalUnitRegistration.setRaceMasterId(externalUnitRegistrationRequest.getRaceMasterId());
            externalUnitRegistration.setActive(true);
            ExternalUnitRegistration externalUnitRegistration1 = externalUnitRegistrationRepository.save(externalUnitRegistration);
            externalUnitRegistrationResponse = mapper.externalUnitRegistrationEntityToObject(externalUnitRegistration1, ExternalUnitRegistrationResponse.class);
            externalUnitRegistrationResponse.setError(false);
        } else {
            externalUnitRegistrationResponse.setError(true);
            externalUnitRegistrationResponse.setError_description("Error occurred while fetching externalUnitRegistration");
            // throw new ValidationException("Error occurred while fetching village");
        }
        return externalUnitRegistrationResponse;
    }
}