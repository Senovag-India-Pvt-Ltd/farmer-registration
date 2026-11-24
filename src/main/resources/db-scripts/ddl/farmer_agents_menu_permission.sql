-- =====================================================================
-- SR-005: menu permission for the "Farmer Agents" screen (UI route
-- /seriui/farmer-agents, map code Registration_Farmer_Agents) under
-- Registration.
--
-- 1. Adds the page to rp_page_permission (parent = Registration group,
--    map code Registration) so it shows in Role Config.
-- 2. Grants it to every role that already has Farmer Registration
--    (Registration_Farmer_Registration); change per role in Role Config.
--
-- rp_page_permission / rp_role_association ids are not IDENTITY: the next id
-- is taken under a lock held only for this short transaction. Idempotent
-- (keyed on map_code / role_id + value). SQL Server.
--
-- IMPORTANT: run with AUTO-COMMIT ON (sqlcmd / SSMS default). In DBeaver with
-- manual commit the locks stay held until you press Commit, every menu load
-- (rp_role_association) blocks and the whole menu goes empty.
--
-- Rollback:
--   DELETE a FROM dbo.rp_role_association a JOIN dbo.rp_page_permission p
--     ON a.value = CAST(p.rp_page_permission_id AS VARCHAR(20))
--     WHERE p.map_code = 'Registration_Farmer_Agents';
--   DELETE FROM dbo.rp_page_permission WHERE map_code = 'Registration_Farmer_Agents';
-- =====================================================================

SET XACT_ABORT ON;
BEGIN TRAN;

DECLARE @parentId INT = (SELECT TOP 1 rp_page_permission_id FROM dbo.rp_page_permission
                         WHERE map_code = 'Registration' AND active = 1);
DECLARE @root INT = (SELECT root FROM dbo.rp_page_permission WHERE rp_page_permission_id = @parentId);
DECLARE @farmerRegId INT = (SELECT TOP 1 rp_page_permission_id FROM dbo.rp_page_permission
                            WHERE map_code = 'Registration_Farmer_Registration' AND active = 1);

IF @parentId IS NULL OR @farmerRegId IS NULL
BEGIN
    RAISERROR('Registration / Registration_Farmer_Registration permission not found; nothing done.', 16, 1);
    ROLLBACK TRAN;
    RETURN;
END

IF NOT EXISTS (SELECT 1 FROM dbo.rp_page_permission WITH (UPDLOCK, HOLDLOCK) WHERE map_code = 'Registration_Farmer_Agents')
BEGIN
    DECLARE @newId INT = (SELECT ISNULL(MAX(rp_page_permission_id), 0) + 1 FROM dbo.rp_page_permission WITH (UPDLOCK, HOLDLOCK));
    INSERT INTO dbo.rp_page_permission
        (rp_page_permission_id, root, parent, page_name, route, is_page, active,
         created_by, modified_by, created_date, modified_date, map_code)
    VALUES
        (@newId, @root, @parentId, 'Farmer Agents', 'farmer-agents', 0, 1,
         'SR-005', 'SR-005', GETDATE(), GETDATE(), 'Registration_Farmer_Agents');
END

DECLARE @pageId VARCHAR(20) = (SELECT CAST(rp_page_permission_id AS VARCHAR(20)) FROM dbo.rp_page_permission
                               WHERE map_code = 'Registration_Farmer_Agents');
DECLARE @farmerRegVal VARCHAR(20) = CAST(@farmerRegId AS VARCHAR(20));
DECLARE @maxAssoc BIGINT = (SELECT ISNULL(MAX(rp_role_association_id), 0) FROM dbo.rp_role_association WITH (UPDLOCK, HOLDLOCK));

INSERT INTO dbo.rp_role_association
    (rp_role_association_id, role_id, value, active, created_by, modified_by, created_date, modified_date, rp_role_permission_id)
SELECT @maxAssoc + ROW_NUMBER() OVER (ORDER BY g.role_id), g.role_id, @pageId, 1,
       'SR-005', 'SR-005', GETDATE(), GETDATE(), g.rp_role_permission_id
FROM (SELECT role_id, MAX(rp_role_permission_id) AS rp_role_permission_id
      FROM dbo.rp_role_association
      WHERE value = @farmerRegVal AND active = 1
      GROUP BY role_id) g
WHERE NOT EXISTS (SELECT 1 FROM dbo.rp_role_association x WHERE x.role_id = g.role_id AND x.value = @pageId);

COMMIT TRAN;
GO
