package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.Education;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends CrudRepository<Education, Long> {

    public Education findByCode(String code);

    public Education findById(long id);

}
