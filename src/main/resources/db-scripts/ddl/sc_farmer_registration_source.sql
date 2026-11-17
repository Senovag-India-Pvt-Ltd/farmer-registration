-- =====================================================================
-- Registration source/channel on farmer (Seva Sindhu integration, Phase 2)
-- ---------------------------------------------------------------------
-- Lets the department distinguish a farmer registered directly in
-- Sericulture from one who arrived via an external channel such as
-- Seva Sindhu. Free-text VARCHAR to match the existing style of
-- Farmer's other lookup-less status/type text columns (e.g. caste_status,
-- gender_status) rather than adding a new one-off lookup master for a
-- handful of values.
--
-- Column stays NULL for every existing row and every future insert that
-- doesn't pass a value -- nothing currently reads or requires it, so this
-- is purely additive and does not change any existing behaviour.
-- Values used: 'SEVA_SINDHU' when the registration originated there;
-- left NULL/blank for a direct registration.
--
-- ddl-auto is 'none' for the farmer-registration module, so this must be
-- run manually. The script is idempotent -- safe to re-run.
-- (SQL Server syntax.)
-- =====================================================================

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.farmer') AND name = 'registration_source')
    ALTER TABLE dbo.farmer ADD registration_source VARCHAR(30) NULL;
GO
