package com.sericulture.registration.service;

import com.sericulture.registration.model.api.EducationRequest;
import com.sericulture.registration.model.api.EducationResponse;
import com.sericulture.registration.model.entity.Education;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.EducationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class EducationService {

    @Autowired
    EducationRepository educationRepository;

    @Autowired
    Mapper mapper;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EducationResponse getEducationDetails(String code) {

        Education education = educationRepository.findByCode(code);
        log.info("The entity is:", education);
        return mapper.educationEntityToObject(education, EducationResponse.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertEducationDetails(EducationRequest request) {
        Education education = mapper.educationObjectToEntity(request, Education.class);
        educationRepository.save(education);
        log.info("The entity is:", education);
    }
}
