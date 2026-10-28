package com.sericulture.registration.sevasindhu;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SevaSindhuCitizenRequestRepository extends CrudRepository<SevaSindhuCitizenRequest, Long> {

    Optional<SevaSindhuCitizenRequest> findBySessionHashAndActive(String sessionHash, Boolean active);

    /** Karnataka is state_id 2 (state_id 58 is an inactive duplicate row). Columns: 0 id, 1 name, 2 name in Kannada. */
    @Query(nativeQuery = true, value = """
            select DISTRICT_ID, DISTRICT_NAME, district_name_in_kannada from DISTRICT
            where ACTIVE = 1 and STATE_ID = :stateId order by DISTRICT_NAME""")
    List<Object[]> districts(@Param("stateId") Long stateId);

    @Query(nativeQuery = true, value = """
            select TALUK_ID, TALUK_NAME, taluk_name_in_kannada from TALUK
            where ACTIVE = 1 and DISTRICT_ID = :districtId order by TALUK_NAME""")
    List<Object[]> taluks(@Param("districtId") Long districtId);

    @Query(nativeQuery = true, value = """
            select HOBLI_ID, HOBLI_NAME, hobli_name_in_kannada from HOBLI
            where ACTIVE = 1 and TALUK_ID = :talukId order by HOBLI_NAME""")
    List<Object[]> hoblis(@Param("talukId") Long talukId);

    @Query(nativeQuery = true, value = """
            select VILLAGE_ID, VILLAGE_NAME, village_name_in_kannada from VILLAGE
            where ACTIVE = 1 and HOBLI_ID = :hobliId order by VILLAGE_NAME""")
    List<Object[]> villages(@Param("hobliId") Long hobliId);

    /** 1 when village -> hobli -> taluk -> district -> state 2 is one consistent active chain. */
    @Query(nativeQuery = true, value = """
            select count(*) from VILLAGE v
            join HOBLI h on h.HOBLI_ID = v.HOBLI_ID and h.ACTIVE = 1
            join TALUK t on t.TALUK_ID = h.TALUK_ID and t.ACTIVE = 1
            join DISTRICT d on d.DISTRICT_ID = t.DISTRICT_ID and d.ACTIVE = 1
            where v.ACTIVE = 1 and v.VILLAGE_ID = :villageId and h.HOBLI_ID = :hobliId
              and t.TALUK_ID = :talukId and d.DISTRICT_ID = :districtId and d.STATE_ID = 2""")
    int locationChainMatches(@Param("districtId") Long districtId, @Param("talukId") Long talukId,
                             @Param("hobliId") Long hobliId, @Param("villageId") Long villageId);

    /**
     * TSC for an address from TSC Geo Mapping (master-data sc_tsc_geo_mapping): the most specific active
     * mapping wins -- village, then hobli, then taluk.
     */
    @Query(nativeQuery = true, value = """
            select top 1 m.tsc_master_id from sc_tsc_geo_mapping m
            where m.ACTIVE = 1 and m.taluk_id = :talukId
              and (m.hobli_id is null or m.hobli_id = :hobliId)
              and (m.village_id is null or m.village_id = :villageId)
            order by case when m.village_id is not null then 0 when m.hobli_id is not null then 1 else 2 end""")
    Long tscForLocation(@Param("talukId") Long talukId, @Param("hobliId") Long hobliId, @Param("villageId") Long villageId);
}
