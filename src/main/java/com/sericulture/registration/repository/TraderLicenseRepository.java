package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.TraderLicense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TraderLicenseRepository extends PagingAndSortingRepository<TraderLicense, Long> {
    public Page<TraderLicense> findByActiveOrderByTraderLicenseIdAsc(boolean isActive, final Pageable pageable);

    public TraderLicense save(TraderLicense farmerAddress);

    public TraderLicense findByTraderLicenseIdAndActive(long id, boolean isActive);

    public TraderLicense findByTraderLicenseIdAndActiveIn(@Param("traderLicenseId") long traderLicenseId, @Param("active") Set<Boolean> active);
}