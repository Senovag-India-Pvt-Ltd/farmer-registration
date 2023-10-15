package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.Education;

import jakarta.persistence.PrePersist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends PagingAndSortingRepository<Education, Long> {

    public Education findByCode(String code);

    public Education findById(long id);

    public boolean existsByName(String name);
    @Query(value = "SELECT e FROM Education e")
    Page<Education> getPaginatedEducationDetails(final Pageable pageable);

    public void save(Education education);


}
