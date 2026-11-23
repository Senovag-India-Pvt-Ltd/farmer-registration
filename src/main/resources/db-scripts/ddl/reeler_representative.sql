-- =====================================================================
-- reeler_representative (SR-013, 2026-11-21)
-- Authorised representatives / agents of a reeler. The maximum number of
-- active representatives per reeler is configurable in farmer-registration
-- (REELER_MAX_REPRESENTATIVES, default 3 per RFP) and enforced by the API.
-- The old single free-text column reeler.representative_name_address is kept
-- unchanged; this table is in addition to it.
-- Aadhaar numbers are stored masked (last 4 digits only).
-- ddl-auto is none for farmer-registration: run manually. Idempotent. SQL Server.
-- Rollback: DROP TABLE dbo.reeler_representative; DROP SEQUENCE dbo.REELER_REPRESENTATIVE_SEQ;
-- =====================================================================

IF NOT EXISTS (SELECT 1 FROM sys.sequences WHERE name = N'REELER_REPRESENTATIVE_SEQ')
BEGIN
    CREATE SEQUENCE dbo.REELER_REPRESENTATIVE_SEQ AS BIGINT START WITH 1 INCREMENT BY 1;
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'dbo.reeler_representative') AND type = N'U')
BEGIN
    CREATE TABLE dbo.reeler_representative (
        reeler_representative_id BIGINT NOT NULL PRIMARY KEY,
        reeler_id BIGINT NOT NULL,
        name NVARCHAR(150) NOT NULL,
        mobile_number VARCHAR(10) NOT NULL,
        address NVARCHAR(500) NOT NULL,
        relationship_id BIGINT NULL,
        id_proof_type VARCHAR(30) NULL,        -- AADHAAR / VOTER_ID / DRIVING_LICENCE / PAN / RATION_CARD / OTHER
        id_proof_number VARCHAR(30) NULL,      -- AADHAAR stored masked: XXXXXXXX1234
        CREATED_BY VARCHAR(255) NULL,
        MODIFIED_BY VARCHAR(255) NULL,
        CREATED_DATE DATETIME NULL,
        MODIFIED_DATE DATETIME NULL,
        ACTIVE TINYINT NOT NULL DEFAULT 1
    );
    CREATE INDEX IX_reeler_representative_reeler ON dbo.reeler_representative(reeler_id, ACTIVE);
END
GO
