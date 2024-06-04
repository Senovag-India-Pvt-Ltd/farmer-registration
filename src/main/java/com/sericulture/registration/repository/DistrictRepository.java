package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface DistrictRepository extends PagingAndSortingRepository<District, Long> {
        public List<District> findByDistrictNameAndDistrictNameInKannada(String districtName,String districtNameInKannada);

        District findByDistrictNameAndStateIdAndActive(String districtName, long stateId, boolean active);
        public List<District> findByDistrictNameAndDistrictNameInKannadaAndActive(String districtName,String districtNameInKannada, boolean active);
        public List<District> findByDistrictName(String districtName);

        public  District findByDistrictCode(String distCode);

        public List<District> findByDistrictNameAndStateId(String districtName, long stateId);

        public District findByDistrictNameAndActive(String districtName,boolean isActive);

        public Page<District> findByActiveOrderByDistrictIdAsc(boolean isActive, final Pageable pageable);


}
