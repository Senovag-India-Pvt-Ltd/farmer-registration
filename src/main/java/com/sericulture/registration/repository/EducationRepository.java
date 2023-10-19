package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.Education;

import jakarta.persistence.PrePersist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends PagingAndSortingRepository<Education, Long> {

    public Education findByCode(String code);

    public Education findById(long id);

    @Query(value = """
                    SELECT
                        CASE WHEN EXISTS (
                            SELECT 1
                            FROM education e where e.education_name =:name
                            and e.active in (1, 0)
                        )
                        THEN 'true'
                        ELSE 'false'
                        END
            """, nativeQuery = true)
    public boolean existsByName(@Param("name") String name);
    @Query(value = "SELECT e FROM Education e")
    Page<Education> getPaginatedEducationDetails(final Pageable pageable);

    public void save(Education education);


}
