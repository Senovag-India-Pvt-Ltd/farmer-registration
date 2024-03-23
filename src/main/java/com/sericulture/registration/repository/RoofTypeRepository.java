package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.RoofType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface RoofTypeRepository extends PagingAndSortingRepository<RoofType, Long> {

    public List<RoofType> findByRoofTypeNameAndRoofTypeNameInKannada(String roofTypeName, String roofTypeNameInKannada);

    public List<RoofType> findByRoofTypeNameAndRoofTypeNameInKannadaAndRoofTypeIdIsNot(String roofTypeName,String roofTypeNameInKannada, long roofTypeId);


    public RoofType findByRoofTypeNameAndActive(String roofTypeName,boolean isActive);

    public Page<RoofType> findByActiveOrderByRoofTypeNameAsc(boolean isActive, final Pageable pageable);

    public RoofType save(RoofType roofType);

    public RoofType findByRoofTypeIdAndActive(long id, boolean isActive);

    public RoofType findByRoofTypeIdAndActiveIn(@Param("roofTypeId") long roofTypeId, @Param("active") Set<Boolean> active);
    public List<RoofType> findByActive(boolean isActive);
}
