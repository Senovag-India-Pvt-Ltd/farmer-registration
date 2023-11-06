package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.FarmerAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FarmerAddressRepository extends PagingAndSortingRepository<FarmerAddress, Long> {
    public Page<FarmerAddress> findByActiveOrderByFarmerAddressIdAsc(boolean isActive, final Pageable pageable);

    public FarmerAddress save(FarmerAddress farmerAddress);

    public FarmerAddress findByFarmerAddressIdAndActive(long id, boolean isActive);

    public FarmerAddress findByFarmerAddressIdAndActiveIn(@Param("farmerAddressId") long farmerAddressId, @Param("active") Set<Boolean> active);
}