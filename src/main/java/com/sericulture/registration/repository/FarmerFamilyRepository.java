package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.FarmerFamily;
import com.sericulture.registration.model.entity.ReelerVirtualBankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FarmerFamilyRepository extends PagingAndSortingRepository<FarmerFamily, Long> {
    public List<FarmerFamily> findByFarmerFamilyName(String farmerFamilyName);

    public FarmerFamily findByFarmerFamilyNameAndActive(String farmerFamilyName, boolean isActive);

    public Page<FarmerFamily> findByActiveOrderByFarmerFamilyIdAsc(boolean isActive, final Pageable pageable);

    public FarmerFamily save(FarmerFamily farmerFamily);

    public FarmerFamily findByFarmerFamilyIdAndActive(long id, boolean isActive);

    public FarmerFamily findByFarmerFamilyIdAndActiveIn(@Param("farmerFamilyId") long farmerFamilyId, @Param("active") Set<Boolean> active);

    public List<FarmerFamily> findByFarmerIdAndActive(long farmerId, boolean isActive);

}