USE grad_wygl;

-- List of tables to add company_id
-- house_building, house_unit, parking_list, live_user, sys_notice, user_complaint, user_repair, fee_park, fee_power, fee_water
-- live_house, live_park (Optional but good for completeness, though they link to user/house which are already isolated)

-- Macro for adding column
DROP PROCEDURE IF EXISTS AddCompanyId;
DELIMITER //
CREATE PROCEDURE AddCompanyId(IN tableName VARCHAR(64))
BEGIN
    SET @exist := (SELECT count(*) FROM information_schema.columns WHERE table_schema = 'grad_wygl' AND table_name = tableName AND column_name = 'company_id');
    IF @exist = 0 THEN
        SET @sql := CONCAT('ALTER TABLE `', tableName, '` ADD COLUMN `company_id` int NULL COMMENT "所属物业公司ID"');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        
        -- Default existing data to Company 1
        SET @sql_update := CONCAT('UPDATE `', tableName, '` SET `company_id` = 1 WHERE `company_id` IS NULL');
        PREPARE stmt_update FROM @sql_update;
        EXECUTE stmt_update;
        DEALLOCATE PREPARE stmt_update;
    END IF;
END //
DELIMITER ;

CALL AddCompanyId('house_building');
CALL AddCompanyId('house_unit');
CALL AddCompanyId('parking_list');
CALL AddCompanyId('live_user');
CALL AddCompanyId('sys_notice');
CALL AddCompanyId('user_complaint');
CALL AddCompanyId('user_repair');
CALL AddCompanyId('fee_park');
CALL AddCompanyId('fee_power');
CALL AddCompanyId('fee_water');
CALL AddCompanyId('live_house');
CALL AddCompanyId('live_park');

DROP PROCEDURE IF EXISTS AddCompanyId;
