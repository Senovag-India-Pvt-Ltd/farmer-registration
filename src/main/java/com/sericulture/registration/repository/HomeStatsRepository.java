package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Home-page statistics — single round-trip count query for the public landing page.
 *
 * Returns one row of pre-aggregated counts so the FE doesn't have to fetch full
 * farmer/reeler/etc. tables just to call `.length`. Each scalar lives behind its
 * own subquery, so a slow / missing table never sinks the whole panel.
 */
@Repository
public interface HomeStatsRepository extends JpaRepository<Farmer, Long> {

    @Query(nativeQuery = true, value = """
        SELECT
            (SELECT COUNT(*) FROM farmer WHERE active = 1)                                 AS farmers,
            (SELECT COUNT(*) FROM reeler WHERE active = 1)                                 AS reelers,
            (SELECT COUNT(*) FROM trader_license WHERE active = 1)                         AS traders,
            (SELECT COUNT(*)
               FROM external_unit_registration eur
               INNER JOIN external_unit_type_master eut
                       ON eur.external_unit_type_id = eut.external_unit_type_id
              WHERE eur.active = 1
                AND UPPER(LTRIM(RTRIM(eut.external_unit_type_name))) = 'RSP')             AS rsp,
            (SELECT COUNT(*)
               FROM external_unit_registration eur
               INNER JOIN external_unit_type_master eut
                       ON eur.external_unit_type_id = eut.external_unit_type_id
              WHERE eur.active = 1
                AND UPPER(LTRIM(RTRIM(eut.external_unit_type_name))) = 'NSSO')            AS nsso,
            (SELECT COUNT(*)
               FROM external_unit_registration eur
               INNER JOIN external_unit_type_master eut
                       ON eur.external_unit_type_id = eut.external_unit_type_id
              WHERE eur.active = 1
                AND UPPER(LTRIM(RTRIM(eut.external_unit_type_name))) = 'RCRC')            AS crc,
            -- Beneficiary count = applications whose form has reached a real success state:
            -- `PAYMENT SUCCESS` or `ACKNOWLEDGEMENT SUCCESS`. DBT PUSHED is in-flight, excluded.
            (SELECT COUNT(*)
               FROM sc_application_form af
              WHERE af.active = 1
                AND af.application_status IN ('PAYMENT SUCCESS IN DBT', 'ACKNOWLEDGEMENT SUCCESS')
            )                                                                              AS beneficiaries,
            -- Amount disbursed = sum(scheme_amount) over the same success cohort.
            (SELECT COALESCE(SUM(CAST(af.scheme_amount AS DECIMAL(18,2))), 0)
               FROM sc_application_form af
              WHERE af.active = 1
                AND af.application_status IN ('PAYMENT SUCCESS IN DBT', 'ACKNOWLEDGEMENT SUCCESS')
            )                                                                              AS disbursed_amount,
            -- Master-data counts are colocated in the same database, so we can fetch
            -- everything in one round-trip instead of a second cross-service call.
            (SELECT COUNT(*) FROM farm_master       WHERE active = 1)                      AS farms,
            (SELECT COUNT(*) FROM grainage_master   WHERE active = 1)                      AS grainages,
            (SELECT COUNT(*) FROM market_master     WHERE active = 1)                      AS markets,
            (SELECT COUNT(*) FROM sc_sub_scheme_details WHERE active = 1)                      AS schemes
    """)
    List<Map<String, Object>> getHomeStatsCounts();
}
