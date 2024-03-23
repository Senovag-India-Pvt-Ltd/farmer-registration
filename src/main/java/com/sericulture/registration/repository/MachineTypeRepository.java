package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.MachineType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface MachineTypeRepository extends PagingAndSortingRepository<MachineType, Long>{


        public List<MachineType> findByMachineTypeNameAndMachineTypeNameInKannada(String machineTypeName, String machineTypeNameInKannada);

        public List<MachineType> findByActiveAndMachineTypeNameAndMachineTypeNameInKannada(boolean a,String machineTypeName,String machineTypeNameInKannada);

        public MachineType findByMachineTypeNameAndActive(String machineTypeName,boolean isActive);

        public Page<MachineType> findByActiveOrderByMachineTypeNameAsc(boolean isActive, final Pageable pageable);

        public MachineType save(MachineType machineType);

        public MachineType findByMachineTypeIdAndActive(long id, boolean isActive);

        public MachineType findByMachineTypeIdAndActiveIn(@Param("machineTypeId") long machineTypeId, @Param("active") Set<Boolean> active);

        public List<MachineType> findByActiveOrderByMachineTypeNameAsc(boolean isActive);
    }

