package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.SoilType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface SoilTypeRepository  extends PagingAndSortingRepository<SoilType, Long> {
    public List<SoilType> findBySoilTypeNameAndSoilTypeNameInKannada(String soilTypeName, String soilTypeNameInKannada);

    public List<SoilType> findByActiveAndSoilTypeNameAndSoilTypeNameInKannada(boolean a,String soilTypeName,String soilTypeNameInKannada);

    public SoilType findBySoilTypeNameAndActive(String soilTypeName,boolean isActive);

    public Page<SoilType> findByActiveOrderBySoilTypeNameAsc(boolean isActive, final Pageable pageable);

    public SoilType save(SoilType soilType);

    public SoilType findBySoilTypeIdAndActive(long id, boolean isActive);

    public SoilType findBySoilTypeIdAndActiveIn(@Param("id") long id, @Param("active") Set<Boolean> active);

    public List<SoilType> findByActive(boolean isActive);
}
