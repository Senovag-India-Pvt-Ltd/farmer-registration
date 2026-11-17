-- =====================================================================
-- Seva Sindhu integration log (Seva Sindhu integration, Phase 5)
-- ---------------------------------------------------------------------
-- Records every call made to the internal Seva Sindhu test harness
-- (POST /internal/sevasindhu-test/simulate-redirect) -- the request
-- inputs, what the harness decided (registered vs not, next action),
-- and whether a FRUITS lookup was attempted. This is the audit trail
-- the harness itself writes to; it has no bearing on any existing
-- table and is not read by any other part of the application.
--
-- Mirrors the shape of the existing AuditTransaction table (bank-
-- transaction / farmer-registration modules): request/response payload
-- plus a free-text status, rather than a rigid column-per-field design,
-- since the exact fields a future real Seva Sindhu payload carries are
-- still an open point per the source requirement document.
--
-- ddl-auto is 'none' for the farmer-registration module, so this must
-- be run manually. The script is idempotent -- safe to re-run.
-- (SQL Server syntax.)
-- =====================================================================

IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE object_id = OBJECT_ID(N'dbo.sc_sevasindhu_integration_log') AND type = N'U')
BEGIN
    CREATE TABLE dbo.sc_sevasindhu_integration_log (
        sc_sevasindhu_integration_log_id BIGINT NOT NULL PRIMARY KEY,
        seva_sindhu_reference VARCHAR(100) NULL,
        endpoint_name VARCHAR(100) NULL,
        request_payload VARCHAR(MAX) NULL,
        response_payload VARCHAR(MAX) NULL,
        status VARCHAR(30) NULL,
        CREATED_BY VARCHAR(255) NULL,
        MODIFIED_BY VARCHAR(255) NULL,
        CREATED_DATE DATETIME NULL,
        MODIFIED_DATE DATETIME NULL,
        ACTIVE TINYINT NULL
    );
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.sequences WHERE name = N'sc_sevasindhu_integration_log_seq')
BEGIN
    CREATE SEQUENCE dbo.sc_sevasindhu_integration_log_seq AS BIGINT START WITH 1 INCREMENT BY 1;
END
GO
