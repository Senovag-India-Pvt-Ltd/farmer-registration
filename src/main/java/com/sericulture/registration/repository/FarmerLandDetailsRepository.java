package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.FarmerLandDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FarmerLandDetailsRepository extends PagingAndSortingRepository<FarmerLandDetails, Long> {

    public Page<FarmerLandDetails> findByActiveOrderByFarmerLandDetailsIdAsc(boolean isActive, final Pageable pageable);

    public FarmerLandDetails save(FarmerLandDetails farmerLandDetails);

    public FarmerLandDetails findByFarmerLandDetailsIdAndActive(long id, boolean isActive);

    public FarmerLandDetails findByFarmerLandDetailsIdAndActiveIn(@Param("farmerLandDetailsId") long farmerLandDetailsId, @Param("active") Set<Boolean> active);
}