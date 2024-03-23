package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.IrrigationSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface IrrigationSourceRepository extends PagingAndSortingRepository<IrrigationSource, Long> {

    public List<IrrigationSource> findByIrrigationSourceNameAndIrrigationSourceNameInKannada(String irrigationSourceName,String irrigationSourceNameInKannada);

    public java.util.List<IrrigationSource> findByActiveAndIrrigationSourceNameAndIrrigationSourceNameInKannada(boolean active, String irrigationSourceName, String irrigationSourceNameInKannada);

    public IrrigationSource findByIrrigationSourceNameAndActive(String irrigationSourceName,boolean isActive);

    public Page<IrrigationSource> findByActiveOrderByIrrigationSourceNameAsc(boolean isActive, final Pageable pageable);

    public IrrigationSource save(IrrigationSource irrigationSource);

    public IrrigationSource findByIrrigationSourceIdAndActive(long id, boolean isActive);

    public IrrigationSource findByIrrigationSourceIdAndActiveIn(@Param("irrigationSourceId") long irrigationSourceId, @Param("active") Set<Boolean> active);

    public List<IrrigationSource> findByActive(boolean isActive);
}
