package com.sericulture.registration.service;

import com.sericulture.registration.model.api.common.SearchWithSortRequest;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Map<String, Object> getPaginatedExternalUnitRegistrationDetails(final Pageable pageable) {
        return convertToMapResponse(externalUnitRegistrationRepository.findByActiveOrderByExternalUnitRegistrationIdAsc(true, pageable));
    }

    public Map<String, Object> getAllByActive(boolean isActive) {
        return convertListToMapResponse(externalUnitRegistrationRepository.findByActiveOrderByExternalUnitRegistrationIdAsc(isActive));


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
            externalUnitRegistration.setCapacity(externalUnitRegistrationRequest.getCapacity());
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

    public Map<String,Object> searchByColumnAndSort(SearchWithSortRequest searchWithSortRequest){
        if(searchWithSortRequest.getSearchText() == null || searchWithSortRequest.getSearchText().equals("")){
            searchWithSortRequest.setSearchText("%%");
        }else{
            searchWithSortRequest.setSearchText("%" + searchWithSortRequest.getSearchText() + "%");
        }
        if(searchWithSortRequest.getSortColumn() == null || searchWithSortRequest.getSortColumn().equals("")){
            searchWithSortRequest.setSortColumn("externalUnitType.externalUnitTypeName");
        }
        if(searchWithSortRequest.getSortOrder() == null || searchWithSortRequest.getSortOrder().equals("")){
            searchWithSortRequest.setSortOrder("asc");
        }
        if(searchWithSortRequest.getPageNumber() == null || searchWithSortRequest.getPageNumber().equals("")){
            searchWithSortRequest.setPageNumber("0");
        }
        if(searchWithSortRequest.getPageSize() == null || searchWithSortRequest.getPageSize().equals("")){
            searchWithSortRequest.setPageSize("5");
        }
        Sort sort;
        if(searchWithSortRequest.getSortOrder().equals("asc")){
            sort = Sort.by(Sort.Direction.ASC, searchWithSortRequest.getSortColumn());
        }else{
            sort = Sort.by(Sort.Direction.DESC, searchWithSortRequest.getSortColumn());
        }
        Pageable pageable = PageRequest.of(Integer.parseInt(searchWithSortRequest.getPageNumber()), Integer.parseInt(searchWithSortRequest.getPageSize()), sort);
        Page<ExternalUnitRegistrationDTO> externalUnitRegistrationDTOS = externalUnitRegistrationRepository.getSortedExternalUnitRegistration(searchWithSortRequest.getJoinColumn(),searchWithSortRequest.getSearchText(),true, pageable);
        log.info("Entity is ",externalUnitRegistrationDTOS);
        return convertPageableDTOToMapResponse(externalUnitRegistrationDTOS);
    }

    private Map<String, Object> convertPageableDTOToMapResponse(final Page<ExternalUnitRegistrationDTO> activeExternalUnitRegistrations) {
        Map<String, Object> response = new HashMap<>();

        List<ExternalUnitRegistrationResponse> externalUnitRegistrationResponses = activeExternalUnitRegistrations.getContent().stream()
                .map(externalUnitRegistration -> mapper.externalUnitRegistrationDTOToObject(externalUnitRegistration,ExternalUnitRegistrationResponse.class)).collect(Collectors.toList());
        response.put("externalUnitRegistration",externalUnitRegistrationResponses);
        response.put("currentPage", activeExternalUnitRegistrations.getNumber());
        response.put("totalItems", activeExternalUnitRegistrations.getTotalElements());
        response.put("totalPages", activeExternalUnitRegistrations.getTotalPages());

        return response;
    }


}