package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.ChowkiManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ChowkiManagementRepository extends JpaRepository<ChowkiManagement, Integer> {


    @Query(nativeQuery = true, value = """
        SELECT 
            CM.chowki_id,
            f.first_name,
            f.father_name,
            CM.fruits_id,
            CM.source_of_dfls,
            CM.race_of_dfls,
            R.race_name,
            CM.numbers_of_dfls,
            CM.lot_numbers_of_the_rsp,
            CM.lot_numbers_crc,
            V.village_name,
            D.district_name,
            S.state_name,
            T.taluk_name,
            H.hobli_name,
            U.name,
            CM.village,
            CM.district,
            CM.state,
            CM.taluk,
            CM.hobli,
            CM.tsc,
            CM.sold_after_1st_or_2nd_mould,
            CM.rate_per_100_dfls,
            CM.price,
            CM.hatching_date,
            CM.dispatch_date,
            CM.farmer_id,
            CM.isverified,
            CM.receipt_no
        FROM chowki_management CM
        LEFT JOIN farmer F ON F.farmer_id = CM.farmer_id
        LEFT JOIN village V ON V.village_id = CM.village
        LEFT JOIN district D ON D.district_id = CM.district
        LEFT JOIN state S ON S.state_id = CM.state
        LEFT JOIN taluk T ON T.taluk_id = CM.taluk
        LEFT JOIN hobli H ON H.hobli_id = CM.hobli
        LEFT JOIN race_master R ON R.race_id = CM.race_of_dfls
        LEFT JOIN tsc_master U ON U.tsc_master_id = CM.tsc
        WHERE (:districtId IS NULL OR CM.district = :districtId)
          AND (:talukId IS NULL OR CM.taluk = :talukId)
          AND (:villageId IS NULL OR CM.village = :villageId)
          AND (:tscMasterId IS NULL OR CM.tsc = :tscMasterId)
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM chowki_management CM
        WHERE (:districtId IS NULL OR CM.district = :districtId)
          AND (:talukId IS NULL OR CM.taluk = :talukId)
          AND (:villageId IS NULL OR CM.village = :villageId)
          AND (:tscMasterId IS NULL OR CM.tsc = :tscMasterId)
        """)
    Page<Object[]> getChowkiDetails(
            @Param("districtId") Long districtId,
            @Param("talukId") Long talukId,
            @Param("villageId") Long villageId,
            @Param("tscMasterId") Long tscMasterId,
            Pageable pageable
    );

    @Query(nativeQuery = true, value = """
        SELECT 
            CM.chowki_id,
            f.first_name,
            f.father_name,
            CM.fruits_id,
            CM.source_of_dfls,
            CM.race_of_dfls,
            R.race_name,
            CM.numbers_of_dfls,
            CM.lot_numbers_of_the_rsp,
            CM.lot_numbers_crc,
            V.village_name,
            D.district_name,
            S.state_name,
            T.taluk_name,
            H.hobli_name,
            U.name,
            CM.village,
            CM.district,
            CM.state,
            CM.taluk,
            CM.hobli,
            CM.tsc,
            CM.sold_after_1st_or_2nd_mould,
            CM.rate_per_100_dfls,
            CM.price,
            CM.hatching_date,
            CM.dispatch_date,
            CM.receipt_no
        FROM chawki_distribution CM
        LEFT JOIN farmer F ON F.fruits_id = CM.fruits_id
        LEFT JOIN village V ON V.village_id = CM.village
        LEFT JOIN district D ON D.district_id = CM.district
        LEFT JOIN state S ON S.state_id = CM.state
        LEFT JOIN taluk T ON T.taluk_id = CM.taluk
        LEFT JOIN hobli H ON H.hobli_id = CM.hobli
        LEFT JOIN race_master R ON R.race_id = CM.race_of_dfls
        LEFT JOIN tsc_master U ON U.tsc_master_id = CM.tsc
        WHERE (:districtId IS NULL OR CM.district = :districtId)
          AND (:talukId IS NULL OR CM.taluk = :talukId)
          AND (:villageId IS NULL OR CM.village = :villageId)
          AND (:tscMasterId IS NULL OR CM.tsc = :tscMasterId)
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM chawki_distribution CM
        WHERE (:districtId IS NULL OR CM.district = :districtId)
          AND (:talukId IS NULL OR CM.taluk = :talukId)
          AND (:villageId IS NULL OR CM.village = :villageId)
          AND (:tscMasterId IS NULL OR CM.tsc = :tscMasterId)
        """)
    Page<Object[]> getChowkiDistributionDetails(
            @Param("districtId") Long districtId,
            @Param("talukId") Long talukId,
            @Param("villageId") Long villageId,
            @Param("tscMasterId") Long tscMasterId,
            Pageable pageable
    );



}
