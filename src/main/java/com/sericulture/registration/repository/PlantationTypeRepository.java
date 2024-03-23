package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.PlantationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface PlantationTypeRepository extends PagingAndSortingRepository<PlantationType, Long> {
    public List<PlantationType> findByPlantationTypeNameAndPlantationTypeNameInKannada(String plantationTypeName, String plantationTypeNameInKannada);

    public List<PlantationType> findByActiveAndPlantationTypeNameAndPlantationTypeNameInKannada(boolean a,String plantationTypeName,String plantationTypeNameInKannada);

    public PlantationType findByPlantationTypeNameAndActive(String plantationTypeName,boolean isActive);

    public Page<PlantationType> findByActiveOrderByPlantationTypeNameAsc(boolean isActive, final Pageable pageable);

    public PlantationType save(PlantationType plantationType);

    public PlantationType findByPlantationTypeIdAndActive(long id, boolean isActive);

    public PlantationType findByPlantationTypeIdAndActiveIn(@Param("plantationTypeId") long plantationTypeId, @Param("active") Set<Boolean> active);

    public List<PlantationType> findByActive(boolean isActive);
}
