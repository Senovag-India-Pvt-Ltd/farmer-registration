package com.sericulture.registration.service;

import com.sericulture.registration.model.api.education.EditEducationRequest;
import com.sericulture.registration.model.api.education.EducationRequest;
import com.sericulture.registration.model.api.education.EducationResponse;
import com.sericulture.registration.model.entity.Education;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.EducationRepository;
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
public class EducationService {

    @Autowired
    EducationRepository educationRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    public EducationResponse getEducationDetails(String code) {
        Education education = educationRepository.findByCode(code);
        log.info("The entity is:", education);
        return mapper.educationEntityToObject(education, EducationResponse.class);
    }

    @Transactional
    public EducationResponse insertEducationDetails(EducationRequest request) {
        Education education = mapper.educationObjectToEntity(request, Education.class);
        //validating the class
        validator.validate(education);
        List<Education> educations = educationRepository.findByNameAndActiveIn(education.getName(), Set.of(true,false));
        if(!educations.isEmpty() && educations.stream().filter(Education::getActive).findAny().isPresent()) {
            EducationResponse educationResponse = mapper.educationEntityToObject(educations.stream().filter(Education::getActive).findAny().get(), EducationResponse.class);
            return educationResponse;
        } /*else if(!educations.isEmpty() && educations.stream().filter(Predicate.not(Education::getActive)).findAny().isPresent()) {
            throw new ValidationException("Name already exists in Inactive State.");
        }*/
        else {
           return mapper.educationEntityToObject(educationRepository.save(education), EducationResponse.class);
        }
    }

    public Map<String, Object> getPaginatedEducationDetails(final Pageable pageable) {
        return convertToMapResponse(educationRepository.findByActiveOrderByEducationIdAsc( true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<Education> pageEducationDetails) {
        Map<String, Object> response = new HashMap<>();

        List<EducationResponse> educationList = pageEducationDetails.getContent()
                .stream()
                .map(education -> mapper.educationEntityToObject(education, EducationResponse.class))
                .collect(Collectors.toList());


        response.put("education", educationList);
        response.put("currentPage", pageEducationDetails.getNumber());
        response.put("totalItems", pageEducationDetails.getTotalElements());
        response.put("totalPages", pageEducationDetails.getTotalPages());
        return response;
    }

    @Transactional
    public void deleteEducationDetails(long id) {
        Education education = educationRepository.findByEducationIdAndActive(id, true);
        if (Objects.nonNull(education)) {
            education.setActive(false);
            educationRepository.save(education);
        } else {
            throw new ValidationException("Invalid Id");
        }
    }

    @Transactional
    public EducationResponse updateEducationDetails(EditEducationRequest educationRequest) {
        List<Education> educations = educationRepository.findByNameAndActiveIn(educationRequest.getName(), Set.of(true,false));
        if(!educations.isEmpty()) {
            throw new ValidationException("Name already exists, duplicates are not allowed.");
        }

        Education education = educationRepository.findByEducationIdAndActiveIn(educationRequest.getId(), Set.of(true,false));
        if (Objects.nonNull(education)) {
            education.setName(educationRequest.getName());
            education.setActive(true);
        }
        else {

        }
        return mapper.educationEntityToObject(education, EducationResponse.class);
    }
}
