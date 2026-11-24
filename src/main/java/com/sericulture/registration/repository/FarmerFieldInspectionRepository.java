package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.FarmerFieldInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerFieldInspectionRepository extends JpaRepository<FarmerFieldInspection, Long> {

    List<FarmerFieldInspection> findByFarmerIdAndActiveOrderByInspectionDateDescFarmerFieldInspectionIdDesc(Long farmerId, Boolean active);

    Optional<FarmerFieldInspection> findByFarmerFieldInspectionIdAndActive(Long id, Boolean active);

    /** 0 FARMER_ID. Empty when the farmer does not exist or is inactive. */
    @Query(nativeQuery = true, value = "select f.FARMER_ID from farmer f where f.FARMER_ID = :farmerId and f.ACTIVE = 1")
    List<Object> activeFarmer(@Param("farmerId") Long farmerId);

    /** Owner of an active land record: 0 farmer_id, 1 gps_lat, 2 gps_lng. Empty when missing / inactive. */
    @Query(nativeQuery = true, value = """
            select l.farmer_id, l.gps_lat, l.gps_lng from farmer_land_details l
            where l.farmer_land_details_id = :landId and l.active = 1""")
    List<Object[]> activeLand(@Param("landId") Long landId);

    /**
     * Active land records of a farmer (farm + rearing-house details live on the same record).
     * 0 farmer_land_details_id, 1 survey_number, 2 hissa, 3 acre, 4 gunta, 5 f_gunta, 6 mulberry_area,
     * 7 rearing_house_details, 8 rearing_capacity_dlf, 9 gps_lat, 10 gps_lng, 11 village name, 12 owner_name
     */
    @Query(nativeQuery = true, value = """
            select l.farmer_land_details_id, l.survey_number, l.hissa, l.acre, l.gunta, l.f_gunta, l.mulberry_area,
                   l.rearing_house_details, l.rearing_capacity_dlf, l.gps_lat, l.gps_lng, v.VILLAGE_NAME, l.owner_name
            from farmer_land_details l
            left join village v on v.VILLAGE_ID = l.village_id
            where l.farmer_id = :farmerId and l.active = 1
            order by l.farmer_land_details_id""")
    List<Object[]> lands(@Param("farmerId") Long farmerId);
}
