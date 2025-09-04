package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.ChowkiManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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

    @Query(value = """
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
            """, nativeQuery = true)
    List<Map<String, Object>> getChawkiManagementDetails();


    @Query(value = """
                SELECT
                        ht.hd_ticket_id
                        ht.ticket_arn,
                        ht.hd_users_affected,
                        ht.query,
                        ht.query_details,
                        ht.on_behalf_of,
                        ht.ticket_number,
                        hm.hd_module_name,
                        hf.hd_feature_name,
                        hb.hd_board_category_name,
                        hc.hd_category_name,
                        hsc.hd_sub_category_name,
                        ht.assigned_to,
                        ht.solution,
                        hsev.hd_severity_name,
                        um.username AS on_behalf_username,
                        hs.hd_status_name,
                         ht.created_by
                    FROM hd_ticket ht
                    LEFT JOIN hd_module_master hm
                           ON ht.hd_module_id = hm.hd_module_id
                    LEFT JOIN hd_feature_master hf
                           ON ht.hd_feature_id = hf.hd_feature_id
                    LEFT JOIN hd_board_category_master hb
                           ON ht.hd_board_category_id = hb.hd_board_category_id
                    LEFT JOIN hd_category_master hc
                           ON ht.hd_category_id = hc.hd_category_id
                    LEFT JOIN hd_sub_category_master hsc
                           ON ht.hd_sub_category_id = hsc.hd_sub_category_id
                    LEFT JOIN hd_status_master hs
                           ON ht.hd_status_id = hs.hd_status_id
                    LEFT JOIN user_master um
                           ON ht.on_behalf_of = um.user_master_id
                    LEFT JOIN hd_severity_master hsev
                           ON ht.hd_severity_id = hsev.hd_severity_id
            """, nativeQuery = true)
    List<Map<String, Object>> getHelpDeskDetails();

    @Query(value = """
                SELECT
                    ts.tr_schedule_id,
                    ts.tr_stakeholder_type,
                    ts.tr_duration,
                    ts.tr_period,
                    ts.tr_no_of_participant,
                    ts.tr_name,
                    ts.tr_upload_path,
                    ts.tr_start_date,
                    ts.tr_date_of_completion,
                    um.username,
                    tim.tr_institution_master_name,
                    tgm.tr_group_master_name,
                    tpm.tr_program_name,
                    tcm.tr_course_name,
                    tmm.tr_mode_master_name
                FROM tr_schedule ts
                INNER JOIN tr_group_master tgm
                    ON ts.tr_group_master_id = tgm.tr_group_master_id
                   AND tgm.active = 1
                INNER JOIN tr_program_master tpm
                    ON ts.tr_program_master_id = tpm.tr_program_id
                   AND tpm.active = 1
                INNER JOIN tr_course_master tcm
                    ON ts.tr_course_master_id = tcm.tr_course_id
                   AND tcm.active = 1
                INNER JOIN tr_mode_master tmm
                    ON ts.tr_mode_master_id = tmm.tr_mode_master_id
                   AND tmm.active = 1
                LEFT JOIN training_schedule_user tsu
                    ON ts.tr_schedule_id = tsu.tr_schedule_id
                LEFT JOIN user_master um
                    ON tsu.user_master_id = um.user_master_id
                   AND um.active = 1
                LEFT JOIN tr_institution_master tim
                    ON tsu.tr_institution_master_id = tim.tr_institution_master_id
                   AND tim.active = 1
            """, nativeQuery = true)
    List<Map<String, Object>> getTrainerDetails();

    @Query(value = """
                SELECT
                ts.tr_schedule_id,
                ts.tr_stakeholder_type,
                ts.tr_duration,
                ts.tr_period,
                ts.tr_no_of_participant,
                ts.tr_name,
                ts.tr_upload_path,
                ts.tr_start_date,
                ts.tr_date_of_completion,
                tim.tr_institution_master_name,
                tgm.tr_group_master_name,
                tpm.tr_program_name,
                tcm.tr_course_name,
                tmm.tr_mode_master_name,
            
                tt.tr_trainee_name,
                dm.name,
                om.tr_office_name,
                tt.gender,
                tt.mobile_number,
                tt.place,
                s.state_name,
                dt.district_name,
                t.taluk_name,
                h.hobli_name,
                v.village_name,
                tt.pre_test_score,
                tt.post_test_score,
                tt.percentage_imporoved
                FROM tr_schedule ts
                INNER JOIN tr_group_master tgm
                ON ts.tr_group_master_id = tgm.tr_group_master_id
                AND tgm.active = 1
                LEFT JOIN tr_institution_master tim
                ON ts.tr_institution_master_id = tim.tr_institution_master_id
                AND tim.active = 1
                INNER JOIN tr_program_master tpm
                ON ts.tr_program_master_id = tpm.tr_program_id
                AND tpm.active = 1
                INNER JOIN tr_course_master tcm
                ON ts.tr_course_master_id = tcm.tr_course_id
                AND tcm.active = 1
                INNER JOIN tr_mode_master tmm
                ON ts.tr_mode_master_id = tmm.tr_mode_master_id
                AND tmm.active = 1
                INNER JOIN tr_trainee tt
                ON ts.tr_schedule_id = tt.tr_schedule_id
            
                LEFT JOIN designation dm
                ON tt.designation_id = dm.designation_id AND dm.active = 1
                LEFT JOIN tr_office om
                ON tt.tr_office_id = om.tr_office_id  AND om.active = 1
                LEFT JOIN state s
                ON tt.state_id = s.state_id AND s.active = 1
                LEFT JOIN district dt
                ON tt.district_id = dt.district_id AND dt.active = 1
                LEFT JOIN taluk t
                ON tt.taluk_id = t.taluk_id AND t.active = 1
                LEFT JOIN hobli h
                ON tt.hobli_id = h.hobli_id AND h.active = 1
                LEFT JOIN village v
                ON tt.village_id = v.village_id AND v.active = 1
            """, nativeQuery = true)
    List<Map<String, Object>> getTraineeDetails();


    @Query(value = """
                WITH FirstAddress AS (
                          SELECT
                          fa.farmer_id,
                          fa.DISTRICT_ID,
                          fa.TALUK_ID,
                          fa.HOBLI_ID,
                          fa.VILLAGE_ID,
                          ROW_NUMBER() OVER (PARTITION BY fa.farmer_id ORDER BY fa.farmer_address_id) AS rn
                  FROM farmer_address fa
                ),
                LatestTransaction AS (
                  SELECT
                      sat.*,
                      ROW_NUMBER() OVER (
                          PARTITION BY sat.application_form_id, sat.scheme_id
                          ORDER BY sat.sc_application_transaction_id DESC
                      ) AS rn
                  FROM sc_application_transaction sat
                )
                  SELECT
                  af.sanction_no,
                  af.scheme_amount,
                  f.fruits_id,
                  f.first_name,
                  f.last_name,
                  af.application_status,
                  d.district_name,
                  t.TALUK_NAME,
                  h.HOBLI_NAME,
                  v.VILLAGE_NAME,
                  af.sc_application_form_id,
                  af.remarks,
                  ssd.sub_scheme_name,
                  af.beneficiary_id,
                  sc.sc_component_name,
                  fym.financial_year,
                  af.fruits_status,
                  sat.application_status as dbt_status,
                  af.category_id,
                  af.component_id,
                  sq.scheme_quota_name,
                  sq.scheme_quota_payment_type,
                  sat.file_name,
                  sat.fruits_id,
                  sat.application_status
                  FROM sc_application_form af
                  LEFT JOIN FARMER f ON f.farmer_id = af.farmer_id
                  LEFT JOIN FirstAddress fa ON fa.farmer_id = f.farmer_id AND fa.rn = 1
                  LEFT JOIN DISTRICT d ON d.district_ID = fa.DISTRICT_ID
                  LEFT JOIN TALUK t ON t.TALUK_ID = fa.TALUK_ID
                  LEFT JOIN HOBLI h ON h.HOBLI_ID = fa.HOBLI_ID
                  LEFT JOIN VILLAGE v ON v.VILLAGE_ID = fa.VILLAGE_ID
                  LEFT JOIN financial_year_master fym ON fym.financial_year_master_id = af.financial_year_master_id
                  LEFT JOIN sc_sub_scheme_details  ssd ON ssd.sc_sub_scheme_details_id = af.sub_scheme_id
                  LEFT JOIN sc_component sc ON sc.sc_component_id = af.component_id
                  LEFT JOIN scheme_quota sq ON sq.scheme_quota_id = af.component_type
                  LEFT JOIN LatestTransaction sat
                     ON sat.application_form_id = af.sc_application_form_id
                    AND sat.scheme_id = af.component_type
                    AND sat.rn = 1
            """, nativeQuery = true)
    List<Map<String, Object>> getDBTDetails();



    @Query(value = """
            WITH PrimaryAddress AS (
            SELECT
            fa.farmer_id,
            fa.STATE_ID,
            fa.DISTRICT_ID,
            fa.TALUK_ID,
            fa.HOBLI_ID,
            fa.VILLAGE_ID,
            fa.address_text,
            ROW_NUMBER() OVER (PARTITION BY fa.farmer_id ORDER BY fa.district_id DESC) AS rn
            FROM
            farmer_address fa
            WHERE
            fa.active = 1
            ),
            MainQuery AS (
            SELECT
            f.farmer_id,
            f.farmer_number,
            f.fruits_id,
            (ISNULL(f.first_name, '') + ' ' +ISNULL(f.middle_name, '') + ' ' +ISNULL(f.last_name, '') + ' - ' +ISNULL(pa.address_text, '')
            ) AS farmer_full_name,
            v.village_name_in_kannada,
            mm.market_name_in_kannada,
            rm.race_name,
            mm.box_weight,
            l.status,
            lg.lot_groupage_id,
            lg.buyer_id,
            lg.buyer_type,
            lg.lot_weight,
            lg.amount,
            lg.market_fee,
            lg.sold_amount,
            ma.dfl_lot_number,
            ma.lot_variety,
            ma.lot_Parental_Level,
            ma.estimated_weight,
            ptaca.TEST_DATE,
            ptaca.NO_OF_COCOON_TAKEN_FOR_EXAMINATION,
            ptaca.NO_OF_DFL_FROM_FC,
            ptaca.NO_OF_COCOON_PER_KG,
            ma.market_auction_date,
            l.allotted_lot_id,
            lg.average_yield,
            lg.invoice_number,
            l.LOT_WEIGHT_AFTER_WEIGHMENT,
            CASE
            WHEN lg.buyer_type = 'RSP' THEN es.license_number
            WHEN lg.buyer_type = 'NSSO' THEN es.address
            WHEN lg.buyer_type = 'Govt Grainage' THEN gm.grainage_master_name
            WHEN lg.buyer_type = 'Reeling' THEN r.name
            ELSE NULL
            END AS buyer_name
            FROM
            FARMER f
            INNER JOIN
            market_auction ma ON ma.farmer_id = f.FARMER_ID
            INNER JOIN
            lot l ON l.market_auction_id = ma.market_auction_id
            LEFT JOIN
            PrimaryAddress pa ON pa.farmer_id = f.FARMER_ID AND pa.rn = 1
            LEFT JOIN
            Village v ON pa.VILLAGE_ID = v.village_id AND f.ACTIVE = 1
            LEFT JOIN
            market_master mm ON mm.market_master_id = ma.market_id
            LEFT JOIN
            race_master rm ON rm.race_id = ma.lot_variety
            LEFT JOIN
            lot_groupage lg ON l.lot_id = lg.lot_id
            LEFT JOIN
            PUPA_TEST_AND_COCOON_ASSESSMENT ptaca ON ptaca.MARKET_AUCTION_ID = ma.market_auction_id AND ptaca.ACTIVE = 1
            LEFT JOIN
            reeler r ON lg.buyer_id = r.reeler_id AND lg.buyer_type = 'Reeling'
            LEFT JOIN
            external_unit_registration es ON lg.external_unit_id = es.external_unit_registration_id
            AND lg.buyer_type IN ('RSP', 'NSSO')
            LEFT JOIN
            grainage_master gm ON lg.external_unit_id = gm.grainage_master_id AND lg.buyer_type = 'Govt Grainage'
            )
            """, nativeQuery = true)
    List<Map<String, Object>> getSeedMarketDetails();

    @Query(value = """
    WITH PrimaryAddress AS (
        SELECT
        ROW_NUMBER() OVER (PARTITION BY fa.farmer_id ORDER BY fa.district_id DESC) AS rn,
        fa.farmer_id,
        fa.DISTRICT_ID,
        fa.TALUK_ID,
        fa.HOBLI_ID,
        fa.VILLAGE_ID
        FROM farmer_address fa
        WHERE fa.active = 1
                )
        SELECT
        msn.id,
        msn.farmer_name,
        msn.fruits_id,
        msn.area,
        msn.date,
        msn.date_of_planting,
        msn.mulberry_variety_id,
        msn.sapling_age,
        msn.quantity,
        msn.rate,
        msn.receipt_number,
        msn.remittance_details,
        msn.nursery_sale_details,
        v.village_name,
        d.district_name,
        t.taluk_name,
        h.hobli_name,
        tm.name AS tsc_name,
        mv.mulberry_variety_name,
        f.father_name
        FROM maintenance_and_sale_of_nursery msn
        LEFT JOIN farmer f ON f.fruits_id = msn.fruits_id
        LEFT JOIN mulberry_variety mv  ON msn.mulberry_variety_id  = mv.mulberry_variety_id
        LEFT JOIN PrimaryAddress pa ON pa.farmer_id = f.farmer_id AND pa.rn = 1
        LEFT JOIN district d ON pa.DISTRICT_ID = d.DISTRICT_ID AND d.active = 1
        LEFT JOIN taluk t ON pa.TALUK_ID = t.TALUK_ID AND t.active = 1
        LEFT JOIN hobli h ON pa.HOBLI_ID = h.HOBLI_ID AND h.active = 1
        LEFT JOIN village v ON pa.VILLAGE_ID = v.VILLAGE_ID AND v.active = 1
        LEFT JOIN tsc_master tm ON f.tsc_master_id = tm.tsc_master_id AND tm.active = 1
    """, nativeQuery = true)
        List<Map<String, Object>> getMaintenanceAndSaleOfNurseryDetails();

    @Query(value = """
    SELECT
    MG.id,
    MG.plot_number,
    V.mulberry_variety_name AS variety,
    MG.area_under_each_variety,
    MG.pruning_date,
    MG.plantation_date,
    MG.fertilizer_application_date,
    MG.fertilizer_application_status,
    MG.fym_application_date,
    MG.fym_application_status,
    MG.irrigation_date,
    MG.irrigation_status,
    MG.foliar_spray_1,
    MG.foliar_spray1_status,
    MG.foliar_spray_2,
    MG.foliar_spray2_status,
    MG.brushing_date,
    S.soil_type_name,
    MG.mulberry_spacing
    FROM maintenance_of_mulberry_garden MG
    LEFT JOIN mulberry_variety V
    ON V.mulberry_variety_id = MG.variety
    AND V.active = 1
    LEFT JOIN soil_type S
    ON S.soil_type_id = MG.soil_type_id
    AND S.active = 1
    """, nativeQuery = true)
            List<Map<String, Object>> getMaintenanceOfMulberryGardenDetails();


    @Query(value = """
    WITH PrimaryAddress AS (
        SELECT
            ROW_NUMBER() OVER (PARTITION BY fa.farmer_id ORDER BY fa.district_id DESC) AS rn,
            fa.farmer_id,
            fa.DISTRICT_ID,
            fa.TALUK_ID,
            fa.HOBLI_ID,
            fa.VILLAGE_ID
        FROM farmer_address fa
        WHERE fa.active = 1
    )
    SELECT
        scb.id,
        f.first_name,
        f.father_name,
        scb.fruits_id,
        scb.date_of_pruning,
        scb.quantity_of_seed_cuttings,
        scb.rate_per_tonne,
        scb.receipt_number,
        scb.remittance_details,
        v.village_name,
        d.district_name,
        t.taluk_name,
        h.hobli_name,
        tm.name
    FROM seed_cutting_bank scb
    LEFT JOIN farmer f ON f.fruits_id = scb.fruits_id
    LEFT JOIN PrimaryAddress pa ON pa.farmer_id = f.farmer_id AND pa.rn = 1
    LEFT JOIN district d ON pa.DISTRICT_ID = d.DISTRICT_ID AND d.active = 1
    LEFT JOIN taluk t ON pa.TALUK_ID = t.TALUK_ID AND t.active = 1
    LEFT JOIN hobli h ON pa.HOBLI_ID = h.HOBLI_ID AND h.active = 1
    LEFT JOIN village v ON pa.VILLAGE_ID = v.VILLAGE_ID AND v.active = 1
    LEFT JOIN tsc_master tm ON f.tsc_master_id = tm.tsc_master_id AND tm.active = 1
    """, nativeQuery = true)
    List<Map<String, Object>> getSeedCuttingBankDetails();


    @Query(value = """
    SELECT
        a.id,
        a.brushing_date,
        a.chawki_percentage,
        a.cocoon_assessment_details,
        a.cold_storage_details,
        a.crop_number,
        a.released_on_date,
        a.spun_on_date,
        a.spun_on_to_date,
        a.worm_test_details,
        a.worm_weight,
        dm.disinfectant_master_name,

        gnm.generation_number,
        b.hatching_date,
        b.invoice_date,
        b.invoice_number,
        b.laid_on_date,
        lnm.line_name,
        b.lot_number,
        b.number_of_dfls_released,
        rm.race_name,
        b.number_of_dfls_received,
        a.cocoon_assessment_details / b.number_of_dfls_released AS AVG_COCOONS,

        s.date_of_supply,
        s.dispatch_date,
        s.invoice_no,
        s.number_of_cocoons_dispatched,
        s.screening_batch_no,
        s.cacoons_supplied_in_kg,
    
        g.grainage_master_name,
        g.grainage_master_name_in_kannada,
        f.farm_name,
        f.farm_name_in_kannada
        FROM rearing_of_dfls a
        INNER JOIN receipt_of_dfls b
          ON a.lot_number_id = b.id
         AND a.user_master_id = b.user_master_id
        LEFT JOIN supply_of_cocoons s
          ON s.lot_number = b.lot_number
        LEFT JOIN farm_master f
          ON f.user_master_id = a.user_master_id
        LEFT JOIN grainage_master g
          ON b.grainage_id = g.grainage_master_id
        LEFT JOIN disinfectant_master dm 
          ON a.disinfectant_master_id  = dm.disinfectant_master_id
        LEFT JOIN generation_number_master gnm 
          ON b.generation_number_id   = gnm.generation_number_id
        LEFT JOIN line_name_master lnm
          ON b.line_name_id   = lnm.line_name_id
        LEFT JOIN race_master rm
          ON b.race_of_dfls    = rm.race_id
    """, nativeQuery = true)
    List<Map<String, Object>> getSupplyOfCocoonsDetails();



    @Query(value = """
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
    """, nativeQuery = true)
    List<Map<String, Object>> getChawkiDistributionDetails();

}
