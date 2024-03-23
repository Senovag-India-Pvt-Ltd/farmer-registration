package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.IrrigationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface IrrigationTypeRepository extends PagingAndSortingRepository<IrrigationType, Long> {

    public List<IrrigationType> findByIrrigationTypeNameAndIrrigationTypeNameInKannada(String irrigationTypeName, String irrigationTypeNameInKannada);

    public List<IrrigationType> findByActiveAndIrrigationTypeNameAndIrrigationTypeNameInKannada(boolean active,String irrigationTypeName,String irrigationTypeNameInKannada);

    public IrrigationType findByIrrigationTypeNameAndActive(String irrigationTypeName,boolean isActive);

    public Page<IrrigationType> findByActiveOrderByIrrigationTypeNameAsc(boolean isActive, final Pageable pageable);

    public IrrigationType save(IrrigationType irrigationType);

    public IrrigationType findByIrrigationTypeIdAndActive(long id, boolean isActive);

    public IrrigationType findByIrrigationTypeIdAndActiveIn(@Param("irrigationTypeId") long irrigationTypeId, @Param("active") Set<Boolean> active);

    public List<IrrigationType> findByActive(boolean isActive);
}
