package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.Reeler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ReelerRepository extends PagingAndSortingRepository<Reeler, Long> {

    public Page<Reeler> findByActiveOrderByReelerIdAsc(boolean isActive, final Pageable pageable);

    public Reeler save(Reeler Reeler);

    public Reeler findByReelerIdAndActive(long id, boolean isActive);

    public Reeler findByReelerIdAndActiveIn(@Param("ReelerId") long ReelerId, @Param("active") Set<Boolean> active);

    public Reeler findByReelingLicenseNumberAndActive(String reelingLicenseNumber, boolean isActive);

}