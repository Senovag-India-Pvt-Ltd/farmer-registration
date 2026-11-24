-- =====================================================================
-- farmer_field_inspection (SR-007, 2026-11-24)
-- GPS-tagged field inspections of a farmer's farm or rearing house. Each row
-- is tied to one land record (farmer_land_details) of the same farmer, so the
-- coordinates stay with the correct farm / rearing-house record. The land
-- record itself (including its gps_lat / gps_lng) is never modified.
-- Optional photo is stored in S3 (photo_s3_key).
-- ddl-auto is none for farmer-registration: run manually with AUTO-COMMIT ON.
-- Idempotent. SQL Server.
-- Rollback: DROP TABLE dbo.farmer_field_inspection; DROP SEQUENCE dbo.FARMER_FIELD_INSPECTION_SEQ;
-- =====================================================================

IF NOT EXISTS (SELECT 1 FROM sys.sequences WHERE name = N'FARMER_FIELD_INSPECTION_SEQ')
BEGIN
    CREATE SEQUENCE dbo.FARMER_FIELD_INSPECTION_SEQ AS BIGINT START WITH 1 INCREMENT BY 1;
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'dbo.farmer_field_inspection') AND type = N'U')
BEGIN
    CREATE TABLE dbo.farmer_field_inspection (
        farmer_field_inspection_id BIGINT NOT NULL PRIMARY KEY,
        farmer_id BIGINT NOT NULL,
        farmer_land_details_id BIGINT NOT NULL,
        site_type VARCHAR(20) NOT NULL,          -- FARM / REARING_HOUSE
        inspection_date DATE NOT NULL,
        latitude DECIMAL(9,6) NOT NULL,
        longitude DECIMAL(9,6) NOT NULL,
        gps_accuracy_m DECIMAL(9,2) NULL,        -- device-reported accuracy (metres)
        gps_source VARCHAR(10) NOT NULL,         -- DEVICE / MANUAL
        gps_captured_at DATETIME NULL,
        observations NVARCHAR(1000) NULL,
        photo_s3_key VARCHAR(300) NULL,
        photo_content_type VARCHAR(50) NULL,
        inspected_by VARCHAR(255) NULL,
        CREATED_BY VARCHAR(255) NULL,
        MODIFIED_BY VARCHAR(255) NULL,
        CREATED_DATE DATETIME NULL,
        MODIFIED_DATE DATETIME NULL,
        ACTIVE TINYINT NOT NULL DEFAULT 1
    );
    CREATE INDEX IX_farmer_field_inspection_farmer ON dbo.farmer_field_inspection(farmer_id, ACTIVE);
    CREATE INDEX IX_farmer_field_inspection_land ON dbo.farmer_field_inspection(farmer_land_details_id, ACTIVE);
END
GO
