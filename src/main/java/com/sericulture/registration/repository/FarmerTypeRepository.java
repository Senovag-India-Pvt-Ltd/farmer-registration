package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.FarmerLandDetails;
import com.sericulture.registration.model.entity.FarmerType;
import com.sericulture.registration.model.entity.FarmerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FarmerTypeRepository extends PagingAndSortingRepository<FarmerType, Long> {
    public List<FarmerType> findByFarmerTypeName(String farmerTypeName);

    public List<FarmerType> findByFarmerTypeNameAndFarmerTypeNameInKannadaAndActive(String farmerTypeName,String farmerTypeNameInKannada, boolean active);

    public List<FarmerType> findByFarmerTypeNameAndFarmerTypeNameInKannada(String farmerTypeName,String farmerTypeNameInKannada);

    public List<FarmerType> findByFarmerTypeNameAndFarmerTypeNameInKannadaAndFarmerTypeIdIsNot(String farmerTypeName,String farmerTypeNameInKannada, long id);

    public List<FarmerType> findByFarmerTypeNameAndFarmerTypeNameInKannadaAndActiveAndFarmerTypeIdIsNot(String farmerTypeName,String farmerTypeNameInKannada, boolean isActive, long id);
    public List<FarmerType> findByFarmerTypeNameAndActive(String farmerTypeName,boolean isActive);

    public Page<FarmerType> findByActiveOrderByFarmerTypeNameAsc(boolean isActive, final Pageable pageable);

    public FarmerType save(FarmerType farmerType);

    public FarmerType findByFarmerTypeIdAndActive(long id, boolean isActive);
    public FarmerType findByFarmerTypeIdAndActiveIn(@Param("farmerTypeId") long farmerLandDetailsId, @Param("active") Set<Boolean> active);

    public List<FarmerType> findByActive(boolean isActive);

}