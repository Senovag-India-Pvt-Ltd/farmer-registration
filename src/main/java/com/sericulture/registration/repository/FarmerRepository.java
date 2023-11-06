package com.sericulture.registration.repository;
import com.sericulture.registration.model.entity.Farmer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FarmerRepository extends PagingAndSortingRepository<Farmer, Long> {
    public List<Farmer> findByFarmerNumber(String farmerNumber);

    public Farmer findByFarmerNumberAndActive(String farmerNumber,boolean isActive);

    public Page<Farmer> findByActiveOrderByFarmerIdAsc(boolean isActive, final Pageable pageable);

    public Farmer save(Farmer farmer);

    public Farmer findByFarmerIdAndActive(long id, boolean isActive);

    public Farmer findByFarmerIdAndActiveIn(@Param("farmerId") long farmerId, @Param("active") Set<Boolean> active);
}