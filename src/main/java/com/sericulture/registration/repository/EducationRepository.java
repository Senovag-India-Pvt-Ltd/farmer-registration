package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.Education;

import jakarta.persistence.PrePersist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EducationRepository extends PagingAndSortingRepository<Education, Long> {

    public Education findByCode(String code);

    public Education findByEducationIdAndActive(long id, boolean isActive);

    public List<Education> findByNameAndActiveIn(@Param("name") String name, @Param("active") Set<Boolean> active);
    public Education findByEducationIdAndActiveIn(@Param("id") long id, @Param("active") Set<Boolean> active);
    Page<Education> findByActiveOrderByEducationIdAsc(boolean isActive, final Pageable pageable);

    public Education save(Education education);


}
