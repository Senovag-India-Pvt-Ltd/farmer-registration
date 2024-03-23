package com.sericulture.registration.repository;

import com.sericulture.registration.model.dto.village.VillageDTO;
import com.sericulture.registration.model.entity.Village;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface VillageRepository extends PagingAndSortingRepository<Village, Long> {


        public List<Village> findByVillageNameAndVillageNameInKannada(String villageName,String villageNameInKannada);

        public List<Village> findByActiveAndVillageNameAndVillageNameInKannada(boolean a,String villageName,String villageNameInKannada);

        public List<Village> findByVillageNameAndStateId(String villageName, long stateId);
    public Village findByVillageNameAndActive(String villageName,boolean isActive);
    @Query("select new com.sericulture.registration.model.dto.village.VillageDTO(" +
            " village.villageId," +
            " village.villageName," +
            " village.stateId," +
            " village.districtId," +
            " village.talukId," +
            " village.hobliId," +
            " state.stateName," +
            " district.districtName," +
            " taluk.talukName," +
            " hobli.hobliName" +
            ") \n" +
            "from Village village\n" +
            "left join State state\n" +
            "on village.stateId = state.stateId " +
            "left join District district\n" +
            "on village.districtId = district.districtId " +
            "left join Taluk taluk\n" +
            "on village.talukId = taluk.talukId " +
            "left join Hobli hobli\n" +
            "on village.hobliId = hobli.hobliId " +
            "where village.active = :isActive AND village.villageId = :id "
    )
    public VillageDTO getByVillageIdAndActive(long id, boolean isActive);

    public Village save(Village village);
}
