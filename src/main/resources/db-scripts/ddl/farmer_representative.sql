-- =====================================================================
-- farmer_representative (SR-005, 2026-11-24)
-- Authorised representatives / agents of a farmer (people allowed to bring
-- and sell the farmer's cocoons at the market on the farmer's behalf).
-- The maximum number of active agents per farmer is configurable in
-- farmer-registration (FARMER_MAX_REPRESENTATIVES, default 3) and enforced
-- by the API. An agent is authorised from authorised_from up to and
-- including authorised_to (open ended when NULL).
-- The old unused column FARMER.representative_id is left unchanged.
-- Aadhaar numbers are stored masked (last 4 digits only).
-- ddl-auto is none for farmer-registration: run manually with AUTO-COMMIT ON.
-- Idempotent. SQL Server.
-- Rollback: DROP TABLE dbo.farmer_representative; DROP SEQUENCE dbo.FARMER_REPRESENTATIVE_SEQ;
-- =====================================================================

IF NOT EXISTS (SELECT 1 FROM sys.sequences WHERE name = N'FARMER_REPRESENTATIVE_SEQ')
BEGIN
    CREATE SEQUENCE dbo.FARMER_REPRESENTATIVE_SEQ AS BIGINT START WITH 1 INCREMENT BY 1;
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'dbo.farmer_representative') AND type = N'U')
BEGIN
    CREATE TABLE dbo.farmer_representative (
        farmer_representative_id BIGINT NOT NULL PRIMARY KEY,
        farmer_id BIGINT NOT NULL,
        name NVARCHAR(150) NOT NULL,
        mobile_number VARCHAR(10) NOT NULL,
        address NVARCHAR(500) NOT NULL,
        relationship_id BIGINT NULL,
        id_proof_type VARCHAR(30) NULL,        -- AADHAAR / VOTER_ID / DRIVING_LICENCE / PAN / RATION_CARD / OTHER
        id_proof_number VARCHAR(30) NULL,      -- AADHAAR stored masked: XXXXXXXX1234
        authorised_from DATE NOT NULL,
        authorised_to DATE NULL,               -- NULL = no end date
        CREATED_BY VARCHAR(255) NULL,
        MODIFIED_BY VARCHAR(255) NULL,
        CREATED_DATE DATETIME NULL,
        MODIFIED_DATE DATETIME NULL,
        ACTIVE TINYINT NOT NULL DEFAULT 1
    );
    CREATE INDEX IX_farmer_representative_farmer ON dbo.farmer_representative(farmer_id, ACTIVE);
    CREATE INDEX IX_farmer_representative_mobile ON dbo.farmer_representative(mobile_number, ACTIVE);
END
GO
