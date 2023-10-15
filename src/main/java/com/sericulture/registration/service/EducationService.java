package com.sericulture.registration.service;

import com.sericulture.registration.model.api.EducationRequest;
import com.sericulture.registration.model.api.EducationResponse;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EducationResponse getEducationDetails(String code) {
        Education education = educationRepository.findByCode(code);
        log.info("The entity is:", education);
        return mapper.educationEntityToObject(education, EducationResponse.class);
    }

    @Transactional
    public void insertEducationDetails(EducationRequest request) {
        Education education = mapper.educationObjectToEntity(request, Education.class);
        //validating the class
        validator.validate(education);
        if(!educationRepository.existsByName(education.getName())) {
            educationRepository.save(education);
        }
        log.info("The entity is:", education);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> getPaginatedEducationDetails(final Pageable pageable) {
        return convertToMapResponse(educationRepository.getPaginatedEducationDetails(pageable));
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
    public void deleteEducationDetails(int id) {
        Education education = educationRepository.findById(id);
        if (Objects.nonNull(education)) {
            education.setActive(false);
            educationRepository.save(education);
        } else {
            throw new ValidationException("Invalid Id");
        }
    }

    @Transactional
    public void updateEducationDetails(EducationRequest educationRequest) {
        Education education = educationRepository.findById(educationRequest.getId());
        if (Objects.nonNull(education)) {
            education.setName(educationRequest.getName());
        } else {
            throw new ValidationException("Invalid Id");
        }
    }
}
