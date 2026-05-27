/* =============================================================================
   Performance indexes for farmer-registration service (SQL Server)

   Why these were added
   --------------------
   Saves / updates / list APIs in this service were getting slow, and the DB
   server was occasionally getting overwhelmed. The reason is that the tables
   below are queried very heavily on columns that are NOT indexed today:

     - "active" filter is present on almost every read path
     - farmer / reeler / trader lookups by business keys (farmer_number,
       fruits_id, mobile_number, aadhaar_number, reeling_license_number,
       reeler_number, trader_license_number, arn_number, etc.)
     - many-to-one joins by *_id columns (farmer_id, district_id, taluk_id,
       hobli_id, village_id, tsc_master_id, caste_id, market_master_id ...)
     - report queries that GROUP BY / WHERE on district / taluk / village

   Without these indexes the DB does full table scans, which is fine for a few
   thousand rows but becomes a server-killer once farmer / farmer_address /
   farmer_land_details grow to lakhs of rows. They also slow INSERT/UPDATE
   because Hibernate's "find before save" pattern (findByFarmerNumberAndActive
   etc.) re-scans the table on every write.

   Notes
   -----
   - Idempotent: each CREATE INDEX is wrapped in an IF NOT EXISTS check so
     this script can be re-run safely.
   - Non-clustered indexes only. The PK is already clustered by SQL Server.
   - Filtered indexes are used on "active" boolean columns to keep them small
     (most queries only look at active = 1).
   - Run this on the production DB during a low-traffic window. Index
     creation on a large table can hold a schema-modification lock for a few
     seconds, but does NOT need ONLINE = ON unless you are on Enterprise
     Edition.
   ============================================================================= */

SET NOCOUNT ON;
GO

/* -----------------------------------------------------------------------------
   FARMER
   - findByFarmerNumber / findByFarmerNumberAndActive
   - findByFruitsIdAndActive
   - findByMobileNumberAndActive
   - JPQL joins on caste_id, landholding_category_id, farmer_type_id,
     tsc_master_id, education_id, user_master_id (assign-to-inspect)
   - large native report query filters by tsc_master_id and caste_id
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_farmer_number' AND object_id = OBJECT_ID('dbo.farmer'))
    CREATE NONCLUSTERED INDEX idx_farmer_farmer_number ON dbo.farmer(FARMER_NUMBER);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_fruits_id' AND object_id = OBJECT_ID('dbo.farmer'))
    CREATE NONCLUSTERED INDEX idx_farmer_fruits_id ON dbo.farmer(FRUITS_ID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_mobile_number' AND object_id = OBJECT_ID('dbo.farmer'))
    CREATE NONCLUSTERED INDEX idx_farmer_mobile_number ON dbo.farmer(MOBILE_NUMBER);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_aadhaar_number' AND object_id = OBJECT_ID('dbo.farmer'))
    CREATE NONCLUSTERED INDEX idx_farmer_aadhaar_number ON dbo.farmer(AADHAAR_NUMBER);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_active' AND object_id = OBJECT_ID('dbo.farmer'))
    CREATE NONCLUSTERED INDEX idx_farmer_active ON dbo.farmer(ACTIVE) WHERE ACTIVE = 1;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_caste_id' AND object_id = OBJECT_ID('dbo.farmer'))
    CREATE NONCLUSTERED INDEX idx_farmer_caste_id ON dbo.farmer(CASTE_ID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_land_category_id' AND object_id = OBJECT_ID('dbo.farmer'))
    CREATE NONCLUSTERED INDEX idx_farmer_land_category_id ON dbo.farmer(LANDHOLDING_CATEGORY_ID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_farmer_type_id' AND object_id = OBJECT_ID('dbo.farmer'))
    CREATE NONCLUSTERED INDEX idx_farmer_farmer_type_id ON dbo.farmer(farmer_type_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_tsc_master_id' AND object_id = OBJECT_ID('dbo.farmer'))
    CREATE NONCLUSTERED INDEX idx_farmer_tsc_master_id ON dbo.farmer(tsc_master_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_education_id' AND object_id = OBJECT_ID('dbo.farmer'))
    CREATE NONCLUSTERED INDEX idx_farmer_education_id ON dbo.farmer(EDUCATION_ID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_assign_inspect_id' AND object_id = OBJECT_ID('dbo.farmer'))
    CREATE NONCLUSTERED INDEX idx_farmer_assign_inspect_id ON dbo.farmer(user_master_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_is_other_state' AND object_id = OBJECT_ID('dbo.farmer'))
    CREATE NONCLUSTERED INDEX idx_farmer_is_other_state ON dbo.farmer(is_other_state_farmer);
GO

/* -----------------------------------------------------------------------------
   FARMER_ADDRESS
   - findByFarmerIdAndActive (very hot — every getFarmer/report call)
   - PrimaryAddress CTE in district/taluk reports
   - filters by state_id / district_id / taluk_id / hobli_id / village_id
   - default_address used in OUTER APPLY for full farmer details
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_address_farmer_id_active' AND object_id = OBJECT_ID('dbo.farmer_address'))
    CREATE NONCLUSTERED INDEX idx_farmer_address_farmer_id_active ON dbo.farmer_address(FARMER_ID, ACTIVE)
        INCLUDE (STATE_ID, DISTRICT_ID, TALUK_ID, HOBLI_ID, VILLAGE_ID, DEFAULT_ADDRESS);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_address_district_id' AND object_id = OBJECT_ID('dbo.farmer_address'))
    CREATE NONCLUSTERED INDEX idx_farmer_address_district_id ON dbo.farmer_address(DISTRICT_ID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_address_taluk_id' AND object_id = OBJECT_ID('dbo.farmer_address'))
    CREATE NONCLUSTERED INDEX idx_farmer_address_taluk_id ON dbo.farmer_address(TALUK_ID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_address_hobli_id' AND object_id = OBJECT_ID('dbo.farmer_address'))
    CREATE NONCLUSTERED INDEX idx_farmer_address_hobli_id ON dbo.farmer_address(HOBLI_ID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_address_village_id' AND object_id = OBJECT_ID('dbo.farmer_address'))
    CREATE NONCLUSTERED INDEX idx_farmer_address_village_id ON dbo.farmer_address(VILLAGE_ID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_address_state_id' AND object_id = OBJECT_ID('dbo.farmer_address'))
    CREATE NONCLUSTERED INDEX idx_farmer_address_state_id ON dbo.farmer_address(STATE_ID);
GO

/* -----------------------------------------------------------------------------
   FARMER_BANK_ACCOUNT
   - findByFarmerIdAndActive (join target)
   - findByFarmerBankAccountNumberAndActive
   - search/sort by farmer_bank_account_number on the farmer list grid
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fba_farmer_id_active' AND object_id = OBJECT_ID('dbo.farmer_bank_account'))
    CREATE NONCLUSTERED INDEX idx_fba_farmer_id_active ON dbo.farmer_bank_account(FARMER_ID, ACTIVE);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fba_account_number' AND object_id = OBJECT_ID('dbo.farmer_bank_account'))
    CREATE NONCLUSTERED INDEX idx_fba_account_number ON dbo.farmer_bank_account(FARMER_BANK_ACCOUNT_NUMBER);
GO

/* -----------------------------------------------------------------------------
   FARMER_LAND_DETAILS
   - findByFarmerIdAndActive (hot)
   - LatestLand CTE in getPrimaryFarmerDetails
   - filters by district_id / taluk_id / village_id
   - findByCategoryNumberAndActive
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fld_farmer_id_active' AND object_id = OBJECT_ID('dbo.farmer_land_details'))
    CREATE NONCLUSTERED INDEX idx_fld_farmer_id_active ON dbo.farmer_land_details(FARMER_ID, ACTIVE)
        INCLUDE (created_date);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fld_district_id' AND object_id = OBJECT_ID('dbo.farmer_land_details'))
    CREATE NONCLUSTERED INDEX idx_fld_district_id ON dbo.farmer_land_details(district_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fld_taluk_id' AND object_id = OBJECT_ID('dbo.farmer_land_details'))
    CREATE NONCLUSTERED INDEX idx_fld_taluk_id ON dbo.farmer_land_details(taluk_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fld_village_id' AND object_id = OBJECT_ID('dbo.farmer_land_details'))
    CREATE NONCLUSTERED INDEX idx_fld_village_id ON dbo.farmer_land_details(village_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fld_mulberry_variety_id' AND object_id = OBJECT_ID('dbo.farmer_land_details'))
    CREATE NONCLUSTERED INDEX idx_fld_mulberry_variety_id ON dbo.farmer_land_details(mulberry_variety_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fld_category_number' AND object_id = OBJECT_ID('dbo.farmer_land_details'))
    CREATE NONCLUSTERED INDEX idx_fld_category_number ON dbo.farmer_land_details(category_number);
GO

/* -----------------------------------------------------------------------------
   FARMER_FAMILY
   - findByFarmerIdAndActive
   - findByFarmerFamilyNameAndFarmerIdAndActive
   - relationship join
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_family_farmer_id_active' AND object_id = OBJECT_ID('dbo.farmer_family'))
    CREATE NONCLUSTERED INDEX idx_farmer_family_farmer_id_active ON dbo.farmer_family(FARMER_ID, ACTIVE);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_farmer_family_relationship_id' AND object_id = OBJECT_ID('dbo.farmer_family'))
    CREATE NONCLUSTERED INDEX idx_farmer_family_relationship_id ON dbo.farmer_family(RELATIONSHIP_ID);
GO

/* -----------------------------------------------------------------------------
   REELER
   - findByReelingLicenseNumberAndActive
   - findByReelerNumberAndActive
   - findByMobileNumberAndActive
   - findByFruitsIdAndActive
   - joins to caste / tsc_master / education / machine_type / state / district /
     taluk / hobli / village / reeler_type_master / user_master
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_reeler_active' AND object_id = OBJECT_ID('dbo.reeler'))
    CREATE NONCLUSTERED INDEX idx_reeler_active ON dbo.reeler(ACTIVE) WHERE ACTIVE = 1;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_reeler_reeler_number' AND object_id = OBJECT_ID('dbo.reeler'))
    CREATE NONCLUSTERED INDEX idx_reeler_reeler_number ON dbo.reeler(reeler_number);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_reeler_license_number' AND object_id = OBJECT_ID('dbo.reeler'))
    CREATE NONCLUSTERED INDEX idx_reeler_license_number ON dbo.reeler(reeling_license_number);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_reeler_mobile_number' AND object_id = OBJECT_ID('dbo.reeler'))
    CREATE NONCLUSTERED INDEX idx_reeler_mobile_number ON dbo.reeler(mobile_number);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_reeler_fruits_id' AND object_id = OBJECT_ID('dbo.reeler'))
    CREATE NONCLUSTERED INDEX idx_reeler_fruits_id ON dbo.reeler(fruits_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_reeler_aadhaar_number' AND object_id = OBJECT_ID('dbo.reeler'))
    CREATE NONCLUSTERED INDEX idx_reeler_aadhaar_number ON dbo.reeler(aadhaar_number);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_reeler_tsc_master_id' AND object_id = OBJECT_ID('dbo.reeler'))
    CREATE NONCLUSTERED INDEX idx_reeler_tsc_master_id ON dbo.reeler(tsc_master_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_reeler_district_id' AND object_id = OBJECT_ID('dbo.reeler'))
    CREATE NONCLUSTERED INDEX idx_reeler_district_id ON dbo.reeler(district_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_reeler_taluk_id' AND object_id = OBJECT_ID('dbo.reeler'))
    CREATE NONCLUSTERED INDEX idx_reeler_taluk_id ON dbo.reeler(taluk_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_reeler_hobli_id' AND object_id = OBJECT_ID('dbo.reeler'))
    CREATE NONCLUSTERED INDEX idx_reeler_hobli_id ON dbo.reeler(hobli_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_reeler_village_id' AND object_id = OBJECT_ID('dbo.reeler'))
    CREATE NONCLUSTERED INDEX idx_reeler_village_id ON dbo.reeler(village_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_reeler_assign_inspect_id' AND object_id = OBJECT_ID('dbo.reeler'))
    CREATE NONCLUSTERED INDEX idx_reeler_assign_inspect_id ON dbo.reeler(assign_to_inspect_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_reeler_type_master_id' AND object_id = OBJECT_ID('dbo.reeler'))
    CREATE NONCLUSTERED INDEX idx_reeler_type_master_id ON dbo.reeler(reeler_type_master_id);
GO

/* -----------------------------------------------------------------------------
   REELER_LICENSE_TRANSACTION
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_rlt_reeler_id_active' AND object_id = OBJECT_ID('dbo.reeler_license_transaction'))
    CREATE NONCLUSTERED INDEX idx_rlt_reeler_id_active ON dbo.reeler_license_transaction(REELER_ID, ACTIVE);
GO

/* -----------------------------------------------------------------------------
   REELER_VIRTUAL_BANK_ACCOUNT
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_rvba_reeler_id_active' AND object_id = OBJECT_ID('dbo.reeler_virtual_bank_account'))
    CREATE NONCLUSTERED INDEX idx_rvba_reeler_id_active ON dbo.reeler_virtual_bank_account(reeler_id, ACTIVE);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_rvba_market_master_id' AND object_id = OBJECT_ID('dbo.reeler_virtual_bank_account'))
    CREATE NONCLUSTERED INDEX idx_rvba_market_master_id ON dbo.reeler_virtual_bank_account(market_master_id);
GO

/* -----------------------------------------------------------------------------
   EXTERNAL_UNIT_REGISTRATION
   - filters by raceMasterId, externalUnitTypeId
   - joins on market_master_id, tsc_master_id, district_id, taluk_id
   - searches by license_number, organisation_name
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_eur_active' AND object_id = OBJECT_ID('dbo.external_unit_registration'))
    CREATE NONCLUSTERED INDEX idx_eur_active ON dbo.external_unit_registration(ACTIVE) WHERE ACTIVE = 1;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_eur_unit_type_id' AND object_id = OBJECT_ID('dbo.external_unit_registration'))
    CREATE NONCLUSTERED INDEX idx_eur_unit_type_id ON dbo.external_unit_registration(external_unit_type_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_eur_race_id' AND object_id = OBJECT_ID('dbo.external_unit_registration'))
    CREATE NONCLUSTERED INDEX idx_eur_race_id ON dbo.external_unit_registration(race_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_eur_market_master_id' AND object_id = OBJECT_ID('dbo.external_unit_registration'))
    CREATE NONCLUSTERED INDEX idx_eur_market_master_id ON dbo.external_unit_registration(market_master_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_eur_tsc_master_id' AND object_id = OBJECT_ID('dbo.external_unit_registration'))
    CREATE NONCLUSTERED INDEX idx_eur_tsc_master_id ON dbo.external_unit_registration(tsc_master_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_eur_district_id' AND object_id = OBJECT_ID('dbo.external_unit_registration'))
    CREATE NONCLUSTERED INDEX idx_eur_district_id ON dbo.external_unit_registration(DISTRICT_ID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_eur_taluk_id' AND object_id = OBJECT_ID('dbo.external_unit_registration'))
    CREATE NONCLUSTERED INDEX idx_eur_taluk_id ON dbo.external_unit_registration(TALUK_ID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_eur_license_number' AND object_id = OBJECT_ID('dbo.external_unit_registration'))
    CREATE NONCLUSTERED INDEX idx_eur_license_number ON dbo.external_unit_registration(license_number);
GO

/* -----------------------------------------------------------------------------
   EU_VIRTUAL_BANK_ACCOUNT
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_euvba_eu_id_active' AND object_id = OBJECT_ID('dbo.eu_virtual_bank_account'))
    CREATE NONCLUSTERED INDEX idx_euvba_eu_id_active ON dbo.eu_virtual_bank_account(eu_id, active);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_euvba_market_master_id' AND object_id = OBJECT_ID('dbo.eu_virtual_bank_account'))
    CREATE NONCLUSTERED INDEX idx_euvba_market_master_id ON dbo.eu_virtual_bank_account(market_master_id);
GO

/* -----------------------------------------------------------------------------
   TRADER_LICENSE
   - filters by districtId, silkType, traderTypeMasterId
   - lookup by marketMasterId + traderLicenseNumber / mobileNumber
   - search by arn_number / first_name / trader_type
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_trader_license_active' AND object_id = OBJECT_ID('dbo.trader_license'))
    CREATE NONCLUSTERED INDEX idx_trader_license_active ON dbo.trader_license(ACTIVE) WHERE ACTIVE = 1;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_trader_license_type_id' AND object_id = OBJECT_ID('dbo.trader_license'))
    CREATE NONCLUSTERED INDEX idx_trader_license_type_id ON dbo.trader_license(trader_type_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_trader_license_market_id' AND object_id = OBJECT_ID('dbo.trader_license'))
    CREATE NONCLUSTERED INDEX idx_trader_license_market_id ON dbo.trader_license(market_master_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_trader_license_district_id' AND object_id = OBJECT_ID('dbo.trader_license'))
    CREATE NONCLUSTERED INDEX idx_trader_license_district_id ON dbo.trader_license(district_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_trader_license_state_id' AND object_id = OBJECT_ID('dbo.trader_license'))
    CREATE NONCLUSTERED INDEX idx_trader_license_state_id ON dbo.trader_license(state_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_trader_license_number' AND object_id = OBJECT_ID('dbo.trader_license'))
    CREATE NONCLUSTERED INDEX idx_trader_license_number ON dbo.trader_license(trader_license_number);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_trader_license_mobile' AND object_id = OBJECT_ID('dbo.trader_license'))
    CREATE NONCLUSTERED INDEX idx_trader_license_mobile ON dbo.trader_license(mobile_number);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_trader_license_arn_number' AND object_id = OBJECT_ID('dbo.trader_license'))
    CREATE NONCLUSTERED INDEX idx_trader_license_arn_number ON dbo.trader_license(arn_number);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_trader_license_silk_type' AND object_id = OBJECT_ID('dbo.trader_license'))
    CREATE NONCLUSTERED INDEX idx_trader_license_silk_type ON dbo.trader_license(silk_type);
GO

/* -----------------------------------------------------------------------------
   INSPECTION_TASK
   - findByActive
   - querying by inspector_id / status / request_type_id is common in flows
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inspection_task_inspector_id' AND object_id = OBJECT_ID('dbo.inspection_task'))
    CREATE NONCLUSTERED INDEX idx_inspection_task_inspector_id ON dbo.inspection_task(inspector_id, ACTIVE);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inspection_task_status' AND object_id = OBJECT_ID('dbo.inspection_task'))
    CREATE NONCLUSTERED INDEX idx_inspection_task_status ON dbo.inspection_task(status);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inspection_task_request_type_id' AND object_id = OBJECT_ID('dbo.inspection_task'))
    CREATE NONCLUSTERED INDEX idx_inspection_task_request_type_id ON dbo.inspection_task(request_type_id);
GO

/* -----------------------------------------------------------------------------
   USER_MASTER
   - login (findByUsername / findByUsernameAndActive)
   - reportee hierarchy (manager_id)
   - filter by role_id / user_type_id / market_id / tsc_master_id
   - district_id / designation_id joined in user-detail reports
   - first_name has unique=true at JPA level but explicit index helps if not enforced in DB
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_user_master_username' AND object_id = OBJECT_ID('dbo.user_master'))
    CREATE NONCLUSTERED INDEX idx_user_master_username ON dbo.user_master(username);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_user_master_manager_id' AND object_id = OBJECT_ID('dbo.user_master'))
    CREATE NONCLUSTERED INDEX idx_user_master_manager_id ON dbo.user_master(manager_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_user_master_role_id' AND object_id = OBJECT_ID('dbo.user_master'))
    CREATE NONCLUSTERED INDEX idx_user_master_role_id ON dbo.user_master(role_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_user_master_user_type_id' AND object_id = OBJECT_ID('dbo.user_master'))
    CREATE NONCLUSTERED INDEX idx_user_master_user_type_id ON dbo.user_master(user_type_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_user_master_market_id' AND object_id = OBJECT_ID('dbo.user_master'))
    CREATE NONCLUSTERED INDEX idx_user_master_market_id ON dbo.user_master(market_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_user_master_tsc_master_id' AND object_id = OBJECT_ID('dbo.user_master'))
    CREATE NONCLUSTERED INDEX idx_user_master_tsc_master_id ON dbo.user_master(tsc_master_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_user_master_district_id' AND object_id = OBJECT_ID('dbo.user_master'))
    CREATE NONCLUSTERED INDEX idx_user_master_district_id ON dbo.user_master(district_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_user_master_designation_id' AND object_id = OBJECT_ID('dbo.user_master'))
    CREATE NONCLUSTERED INDEX idx_user_master_designation_id ON dbo.user_master(designation_id);
GO

/* -----------------------------------------------------------------------------
   CHOWKI_MANAGEMENT
   - getChowkiDetails filters on district / taluk / village / tsc
   - join on farmer_id / fruits_id
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_chowki_mgmt_district' AND object_id = OBJECT_ID('dbo.chowki_management'))
    CREATE NONCLUSTERED INDEX idx_chowki_mgmt_district ON dbo.chowki_management(district, taluk, village, tsc);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_chowki_mgmt_farmer_id' AND object_id = OBJECT_ID('dbo.chowki_management'))
    CREATE NONCLUSTERED INDEX idx_chowki_mgmt_farmer_id ON dbo.chowki_management(farmer_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_chowki_mgmt_fruits_id' AND object_id = OBJECT_ID('dbo.chowki_management'))
    CREATE NONCLUSTERED INDEX idx_chowki_mgmt_fruits_id ON dbo.chowki_management(fruits_id);
GO

/* -----------------------------------------------------------------------------
   CHAWKI_DISTRIBUTION
   ----------------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_chawki_dist_district' AND object_id = OBJECT_ID('dbo.chawki_distribution'))
    CREATE NONCLUSTERED INDEX idx_chawki_dist_district ON dbo.chawki_distribution(district, taluk, village, tsc);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_chawki_dist_fruits_id' AND object_id = OBJECT_ID('dbo.chawki_distribution'))
    CREATE NONCLUSTERED INDEX idx_chawki_dist_fruits_id ON dbo.chawki_distribution(fruits_id);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_chawki_dist_active' AND object_id = OBJECT_ID('dbo.chawki_distribution'))
    CREATE NONCLUSTERED INDEX idx_chawki_dist_active ON dbo.chawki_distribution(ACTIVE) WHERE ACTIVE = 1;
GO

PRINT 'farmer-registration performance indexes installed';
GO
