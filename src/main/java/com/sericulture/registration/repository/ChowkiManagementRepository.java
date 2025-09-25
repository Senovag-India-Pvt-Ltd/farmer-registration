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
                        ht.hd_ticket_id,
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
            )SELECT *
             FROM MainQuery;
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


    @Query(value = """
            SELECT
                    ci.date,
                    ci.note,
                    cs.name AS crop_status_name,
                    m.name AS mount_name,
                    r.name As reason_name,
                    ci.sale_and_disposal_id,
                    tm.name,
                    f.first_name,
                    f.father_name,
                    f.fruits_id
                FROM
                    crop_inspection ci
                Left JOIN
                    crop_status cs ON ci.crop_status_id = cs.crop_status_id
                Left JOIN
                    farmer f ON ci.farmer_id = f.farmer_id
                Left JOIN
                    sale_and_disposal_of_dfls sd ON sd.id = ci.sale_and_disposal_id
                Left JOIN
                    mount m ON ci.mount_id = m.mount_id
                Left JOIN
                    reason r ON ci.reason_id = r.reason_id
                Left JOIN
                    tsc_master tm ON tm.tsc_master_id = sd.tsc
            """, nativeQuery = true)
    List<Map<String, Object>> getCropInspectionDetails();


    @Query(value = """
            SELECT
                       f.first_name,
                       f.father_name,
                       f.fruits_id,
                       fc.fitness_certificate_id,
                       fc.fitness_certificate_path,
                       fc.farmer_id,
                       sadod.rate_per100dfls_price,
                       sadod.number_of_dfls_disposed,
                       sadod.lot_number,
                       rm.race_name,
                       tm.name
                       FROM fitness_certificate fc
                       Inner JOIN sale_and_disposal_of_dfls sadod ON sadod.id = fc.sale_and_disposal_id  AND  sadod.active = 1
                       LEFT JOIN race_master rm ON rm.race_id = sadod.race_id AND rm.active = 1
                       LEFT JOIN farmer f ON fc.farmer_id = f.farmer_id AND f.active = 1
                       LEFT JOIN tsc_master tm ON sadod.tsc = tm.tsc_master_id AND tm.active = 1
            """, nativeQuery = true)
    List<Map<String, Object>> getFitnessCertificateDetails();


    @Query(value = """
            SELECT
                        F.name_kan AS first_name,
                        F.father_name,
                        F.fruits_id,
                        fm.scheme,
                        fa.address_text,
                        tm.name AS tsc,
                        tm.name_in_kannada,
                        mv.mulberry_variety_name,
                        mv.mulberry_variety_name_in_kannada,
                        fm.extension_date AS plantation_date,
                        fm.number_of_sapplings,
                        fm.mulberry_area,
                        fm.spacing,
                        fm.application_type,
                        fm.uprooting_reason,
                        fm.uprooting_date
                    FROM FARMER F
                    INNER JOIN farmer_address fa
                        ON F.FARMER_ID = fa.FARMER_ID
                       AND fa.active = 1
                    INNER JOIN farmer_land_details fl
                        ON F.FARMER_ID = fl.farmer_id
                       AND fa.FARMER_ID = fl.farmer_id
                       AND fl.active = 1
                    INNER JOIN farmer_mulberry_extension fm
                        ON F.FARMER_ID = fm.farmer_id
                       AND fm.farmer_id = fa.FARMER_ID
                       AND fm.farmer_land_details_id = fl.farmer_land_details_id
                       AND fm.active = 1
                    INNER JOIN mulberry_variety mv
                        ON fm.mulberry_variety_id = mv.mulberry_variety_id
                       AND mv.active = 1
                    LEFT JOIN tsc_master tm
                        ON F.tsc_master_id = tm.tsc_master_id
                       AND tm.active = 1
            """, nativeQuery = true)
    List<Map<String, Object>> getFarmerMulberryExtensionDetails();

    @Query(value = """
            SELECT
                        f.first_name,
                        f.father_name,
                        f.fruits_id,
                        sod.invoice_no_date,
                        sod.quantity,
                        sod.disinfectant_name,
                        sod.quantity_supplied,
                        sod.supply_date,
                        sod.size_of_rearing_house,
                        sod.no_of_dfls,
                        dm.disinfectant_master_name,
                        tm.name
                    FROM
                    supply_of_disinfectants sod
                    LEFT JOIN disinfectant_master dm ON dm.disinfectant_master_id = sod.disinfectant_master_id AND dm.active =1
                    LEFT JOIN farmer f ON sod.farmer_id  = f.FARMER_ID And f.active =1
                    LEFT JOIN tsc_master tm  ON tm.tsc_master_id  = f.tsc_master_id And tm.active = 1
            """, nativeQuery = true)
    List<Map<String, Object>> getSupplyOfDisinfectantDetails();

    @Query(value = """
            SELECT
            mt.mulberry_targets_id,
            mt.mulberry_target_type_id,
            mtt.mulberry_target_type_name,
            mt.tsc_master_id,
            tm.name,
            mt.district_id,
            d.DISTRICT_NAME,
            mt.taluk_id,
            t.TALUK_NAME,
            mt.financial_year_master_id,
            fym.financial_year,
            mt.target_type,
            mt.user_master_id,
            um.username,
            mt.month,
            mt.value,
            mt.page_type
            FROM mulberry_targets mt
            LEFT JOIN mulberry_target_type mtt
            ON mt.mulberry_target_type_id = mtt.mulberry_target_type_id
            LEFT JOIN tsc_master tm
            ON mt.tsc_master_id = tm.tsc_master_id
            LEFT JOIN TALUK t
            ON mt.taluk_id = t.TALUK_ID
            LEFT JOIN DISTRICT d
            ON mt.district_id = d.DISTRICT_ID
            LEFT JOIN financial_year_master fym
            ON mt.financial_year_master_id = fym.financial_year_master_id
            LEFT JOIN user_master um
            ON mt.user_master_id = um.user_master_id
            """, nativeQuery = true)
    List<Map<String, Object>> getMulberryTargetDetails();


    @Query(value = """
            SELECT
                pt.production_targets_id,
                pt.mulberry_target_type_id,
                mtt.mulberry_target_type_name,
                pt.tsc_master_id,
                tm.name,
                pt.district_id,
                d.DISTRICT_NAME,
                pt.financial_year_master_id,
                fym.financial_year,
                pt.race_master_id,
                rm.race_name,
                pt.user_master_id,
                um.username,
                pt.month,
                pt.value,
                pt.taluk_id,
                t.TALUK_NAME,
                pt.page_type
            FROM production_targets pt
            LEFT JOIN mulberry_target_type mtt
                   ON pt.mulberry_target_type_id = mtt.mulberry_target_type_id
            LEFT JOIN tsc_master tm
                   ON pt.tsc_master_id = tm.tsc_master_id
            LEFT JOIN DISTRICT d
                   ON pt.district_id = d.DISTRICT_ID
            LEFT JOIN TALUK t
                   ON pt.taluk_id = t.TALUK_ID
            LEFT JOIN financial_year_master fym
                   ON pt.financial_year_master_id = fym.financial_year_master_id
            LEFT JOIN race_master rm
                   ON pt.race_master_id = rm.race_id
            LEFT JOIN user_master um
                   ON pt.user_master_id = um.user_master_id;
            """, nativeQuery = true)
    List<Map<String, Object>> getProductionTargetDetails();


    @Query(value = """
            SELECT
                st.scheme_targets_id,
                st.mulberry_target_type_id,
                mtt.mulberry_target_type_name,
                st.tsc_master_id,
                tm.name,
                st.district_id,
                d.DISTRICT_NAME,
                st.taluk_id,
                t.TALUK_NAME,
                st.financial_year_master_id,
                fym.financial_year,
                st.race_master_id,
                rm.race_name,
                st.sc_head_account_id,
                sha.sc_head_account_name,
                st.sc_component_id,
                scc.sc_component_name,
                st.sc_scheme_details_id,
                ssd.scheme_name,
                st.sc_sub_scheme_details_id,
                sssd.sub_scheme_name,
                st.sc_category_id,
                sc.category_name,
                st.value,
                st.target,
                st.month,
                st.target_type,
                st.state_share,
                st.central_share,
                st.user_master_id,
                um.username
            FROM scheme_targets st
            LEFT JOIN mulberry_target_type mtt
                   ON st.mulberry_target_type_id = mtt.mulberry_target_type_id
            LEFT JOIN tsc_master tm
                   ON st.tsc_master_id = tm.tsc_master_id
            LEFT JOIN DISTRICT d
                   ON st.district_id = d.DISTRICT_ID
            LEFT JOIN TALUK t
                   ON st.taluk_id = t.TALUK_ID
            LEFT JOIN financial_year_master fym
                   ON st.financial_year_master_id = fym.financial_year_master_id
            LEFT JOIN race_master rm
                   ON st.race_master_id = rm.race_id
            LEFT JOIN sc_head_account sha
                   ON st.sc_head_account_id = sha.sc_head_account_id
            LEFT JOIN sc_component scc
                   ON st.sc_component_id = scc.sc_component_id
            LEFT JOIN sc_scheme_details ssd
                   ON st.sc_scheme_details_id = ssd.sc_scheme_details_id
            LEFT JOIN sc_sub_scheme_details sssd
                   ON st.sc_sub_scheme_details_id = sssd.sc_sub_scheme_details_id
            LEFT JOIN sc_category sc
                   ON st.sc_category_id = sc.sc_category_id
            LEFT JOIN user_master um
                   ON st.user_master_id = um.user_master_id;
            """, nativeQuery = true)
    List<Map<String, Object>> getSchemeTargetDetails();


    @Query(value = """
                    SELECT
                        t.targets_id,
                        t.mulberry_target_type_id,
                        mtt.mulberry_target_type_name,
                        t.financial_year_master_id,
                        fym.financial_year,
                        t.race_master_id,
                        rm.race_name,
                        t.training_institution_id,
                        tim.tr_institution_master_name,
                        t.farm_id,
                        fm.farm_name,
                        t.grainage_master_id,
                        gm.grainage_master_name,
                        t.course_name,
                        tcm.tr_course_name,
                        t.user_master_id,
                        um.username,
                        t.value,
                        t.target,
                        t.month,
                        t.page_type
                    FROM targets t
                    LEFT JOIN mulberry_target_type mtt
                           ON t.mulberry_target_type_id = mtt.mulberry_target_type_id
                    LEFT JOIN financial_year_master fym
                           ON t.financial_year_master_id = fym.financial_year_master_id
                    LEFT JOIN race_master rm
                           ON t.race_master_id = rm.race_id
                    LEFT JOIN tr_institution_master tim
                           ON t.training_institution_id = tim.tr_institution_master_id
                    LEFT JOIN farm_master fm
                           ON t.farm_id = fm.farm_id
                    LEFT JOIN grainage_master gm
                           ON t.grainage_master_id = gm.grainage_master_id
                    LEFT JOIN tr_course_master tcm
                           ON t.course_name = tcm.tr_course_id
                    LEFT JOIN user_master um
                           ON t.user_master_id = um.user_master_id;
            """, nativeQuery = true)
    List<Map<String, Object>> getTargetDetails();


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
            FROM maintenance_of_mulberry_farm MG
            LEFT JOIN mulberry_variety V
            ON V.mulberry_variety_id = MG.variety
            AND V.active = 1
            LEFT JOIN soil_type S
            ON S.soil_type_id = MG.soil_type_id
            AND S.active = 1
            """, nativeQuery = true)
    List<Map<String, Object>> getSeedAndDFLMulberryFarmDetails();

    @Query(value = """
            SELECT
            a.chawki_percentage,
            a.cold_storage_details,
            a.crop_detail,
            a.crop_failure_details,
            a.crop_number,
            a.laid_on_date,
            a.number_ofdfls as line_number_of_dfls,
            a.released_on_date,
            a.spun_on_date,
            a.spun_on_to_date,
            a.worm_test_dates_and_results,
            a.worm_weight_in_grams,
            a.hatching_date,
            dm.disinfectant_master_name,
            gnm.generation_number,
            b.hatching_date AS receipt_hatching_date,
            b.invoice_date,
            b.invoice_number,
            b.laid_on_date AS receipt_laid_on_date,
            lnm.line_name,
            b.lot_number,
            b.number_of_dfls_released,
            rm.race_name,
            
            g.grainage_master_name,
            g.grainage_master_name_in_kannada,
            f.farm_name,
            f.farm_name_in_kannada,
            
            molrfer.average_weight,
            molrfer.average_weight_male,
            molrfer.date_of_selection_cocoon,
            molrfer.farmer_name,
            molrfer.farmer_name_male,
            molrfer.fruits_id,
            molrfer.lot_number,
            molrfer.lot_number_male,
            molrfer.market_master_id,
            molrfer.market_master_id_male,
            molrfer.no_of_cocoons_selected,
            molrfer.no_of_cocoons_selected_male,
            molrfer.number_of_dfls,
            molrfer.number_of_dfls_male,
            molrfer.pupa_test_details,
            
            mosbr.black_boxing_date,
            mosbr.brushed_on_date,
            mosbr.chawki_percentage AS screening_chawki_percentage,
            mosbr.cocoons_produced_at_each_generation,
            mosbr.cocoons_produced_at_each_screening,
            mosbr.crop_failure_details AS screening_crop_failure_details,
            mosbr.incubation_date,
            mosbr.lot_number AS screening_lot_number,
            mosbr.screening_batch_no,
            mosbr.screening_batch_results,
            mosbr.selected_bed_as_per_the_mean_performance,
            mosbr.spun_on_date AS screening_spun_on_date,
            mosbr.spun_on_to_date AS screening_spun_on_to_date,
            
            roeooff.bank_challan_number,
            roeooff.bank_challan_upload,
            roeooff.bill_number,
            roeooff.date AS remittance_date,
            roeooff.lot_number AS remittance_lot_number,
            roeooff.number_ofdfls,
            roeooff.rtc25,
            roeooff.total_amount
            
            FROM rearing_ofdfls_for_the8lines a
            INNER JOIN receipt_of_dfls_from_p4_grainage b
            ON a.lot_number = b.lot_number
            AND a.user_master_id = b.user_master_id
            
            LEFT JOIN maintenance_of_line_records_for_each_race molrfer
            ON molrfer.lot_number = a.lot_number
            LEFT JOIN maintenance_of_screening_batch_records mosbr
            ON mosbr.lot_number = a.lot_number
            LEFT JOIN remittance_of_eggs_orpcor_others_for_farm roeooff
            ON roeooff.lot_number = a.lot_number
            LEFT JOIN farm_master f
            ON f.user_master_id = a.user_master_id
            LEFT JOIN grainage_master g
            ON b.grainage_id = g.grainage_master_id
            LEFT JOIN disinfectant_master dm
            ON a.disinfectant_master_id = dm.disinfectant_master_id
            LEFT JOIN generation_number_master gnm
            ON b.generation_number_id = gnm.generation_number_id
            LEFT JOIN line_name_master lnm
            ON b.line_name_id = lnm.line_name_id
            LEFT JOIN race_master rm
            ON roeooff.race_id = rm.race_id
            """, nativeQuery = true)
    List<Map<String, Object>> getSeedAndDFLFarmWiseDetails();

    @Query(value = """
            SELECT
            p.bed_number_or_kgs_of_cocoons_supplied,
            p.cocoon_rejection_details,
            p.crop_number,
            p.date_of_seed_cocoon_supply,
            p.invoice_date,
            p.name_of_the_government_seed_farm_or_farmer,
            p.number_of_pupa_examined,
            p.rate_per_kg,
            p.spun_on_date,
            l.line_name,
            l.line_name_in_kannada,
            l.line_code,
            rm.race_name,
            rm.race_name_in_kannada,
            e.number_of_cocoonscb,
            e.date_of_moth_emergence,
            e.number_of_pairs,
            e.number_of_cocoonscb AS TestedCocoons,
            e.created_date,
            e.number_of_rejection,
            e.lot_number,
            e.laid_on_date,
            e.dfls_obtained,
            g.grainage_master_name,
            e.egg_recovery_percentage,
            CASE WHEN e.test_results = 'Disease-Free' THEN ISNULL(SUM(e.dfls_obtained),0) END AS test_results_disease_free,
            CASE WHEN e.test_results = 'Diseased' THEN ISNULL(SUM(e.dfls_obtained),0) END AS test_results_disease,
            a.lot_number AS disposal_lot_number,
            a.date_of_disposal,
            a.egg_sheet_numbers,
            a.expected_date_of_hatching,
            a.invoice_number,
            a.name_and_address_of_the_farm,
            a.number_of_dfls_disposed,
            a.rate_per100dfls_price,
            a.release_date,
            moe.date_of_cold_store,
            moe.date_of_release,
            moe.grainage_details,
            moe.incubation_details,
            moe.laid_on_date AS moe_laid_on_date,
            moe.lot_number AS moe_lot_number,
            tomp.pebrine_free_status_of_pupa_and_moth,
            tomp.source_details,
            tm.name
            FROM preservation_of_seed_cocoon_for_processing p
            LEFT JOIN preparation_of_eggs e
            ON e.lot_number = p.lot_number
            LEFT JOIN sale_and_disposal_of_dfls a
            ON a.lot_number = p.lot_number
            LEFT JOIN maintenance_of_eggs_at_cold_storage moe
            ON moe.lot_number = p.lot_number
            LEFT JOIN testing_of_moth_pupa tomp
            ON tomp.lot_number = p.lot_number
            LEFT JOIN line_name_master l
            ON l.line_name_id = p.line_name_id
            LEFT JOIN grainage_master g
            ON p.user_master_id = g.user_master_id
            LEFT JOIN race_master rm
            ON p.race_id = rm.race_id
            LEFT JOIN tsc_master tm
            ON tm.tsc_master_id = a.tsc
            GROUP BY
            p.bed_number_or_kgs_of_cocoons_supplied,
            p.cocoon_rejection_details,
            p.crop_number,
            p.date_of_seed_cocoon_supply,
            p.invoice_date,
            p.name_of_the_government_seed_farm_or_farmer,
            p.number_of_pupa_examined,
            p.rate_per_kg,
            p.spun_on_date,
            l.line_name,
            l.line_name_in_kannada,
            l.line_code,
            rm.race_name,
            rm.race_name_in_kannada,
            e.number_of_cocoonscb,
            e.date_of_moth_emergence,
            e.number_of_pairs,
            e.number_of_cocoonscb,
            e.created_date,
            e.number_of_rejection,
            e.lot_number,
            e.laid_on_date,
            e.dfls_obtained,
            g.grainage_master_name,
            e.egg_recovery_percentage,
            e.test_results,
            a.lot_number,
            a.date_of_disposal,
            a.egg_sheet_numbers,
            a.expected_date_of_hatching,
            a.invoice_number,
            a.name_and_address_of_the_farm,
            a.number_of_dfls_disposed,
            a.rate_per100dfls_price,
            a.release_date,
            moe.date_of_cold_store,
            moe.date_of_release,
            moe.grainage_details,
            moe.incubation_details,
            moe.laid_on_date,
            moe.lot_number,
            tomp.pebrine_free_status_of_pupa_and_moth,
            tomp.source_details,
            tm.name;
            """, nativeQuery = true)
    List<Map<String, Object>> getTSCWiseSoldDFLDetails();

}
