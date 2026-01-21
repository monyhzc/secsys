USE grad_wygl;

-- 1. Create sys_company table
CREATE TABLE IF NOT EXISTS `sys_company` (
  `company_id` int NOT NULL AUTO_INCREMENT COMMENT '公司id',
  `company_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '公司名称',
  `contact_person` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系电话',
  `status` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '0' COMMENT '状态 0：启用 1：禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`company_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='物业公司表';

-- 2. Insert Default Companies
-- Check if exists to avoid dupes on re-run (simple check)
INSERT INTO `sys_company` (company_id, company_name, contact_person, contact_phone, status)
SELECT 1, '默认物业公司', '管理员', '13800000000', '0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_company` WHERE company_id = 1);

-- 3. Add company_id to tables
-- sys_user
SET @exist := (SELECT count(*) FROM information_schema.columns WHERE table_schema = 'grad_wygl' AND table_name = 'sys_user' AND column_name = 'company_id');
SET @sql := IF(@exist=0, 'ALTER TABLE `sys_user` ADD COLUMN `company_id` int NULL COMMENT "所属物业公司ID"', 'SELECT "Column already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;

-- sys_role
SET @exist := (SELECT count(*) FROM information_schema.columns WHERE table_schema = 'grad_wygl' AND table_name = 'sys_role' AND column_name = 'company_id');
SET @sql := IF(@exist=0, 'ALTER TABLE `sys_role` ADD COLUMN `company_id` int NULL COMMENT "所属物业公司ID"', 'SELECT "Column already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;

-- house_list (Domain example)
SET @exist := (SELECT count(*) FROM information_schema.columns WHERE table_schema = 'grad_wygl' AND table_name = 'house_list' AND column_name = 'company_id');
SET @sql := IF(@exist=0, 'ALTER TABLE `house_list` ADD COLUMN `company_id` int NULL COMMENT "所属物业公司ID"', 'SELECT "Column already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;

-- 4. Migrate Data
-- Assign existing users (except platform admin) to Company 1
UPDATE `sys_user` SET `company_id` = 1 WHERE `user_id` != 4 AND `company_id` IS NULL;
-- Assign existing roles to Company 1
UPDATE `sys_role` SET `company_id` = 1 WHERE `company_id` IS NULL;
-- Assign existing houses to Company 1
UPDATE `house_list` SET `company_id` = 1 WHERE `company_id` IS NULL;

-- 5. Update Platform Admin
-- User ID 4 is 'admin'. Set company_id to NULL (Global)
UPDATE `sys_user` SET `company_id` = NULL, `login_name` = '平台运营管理员' WHERE `user_id` = 4;

-- 6. Add a new Admin for the Default Company (for testing isolation)
-- Password is '123456' ($2a$10$/mDcuIn2la6FmjaC6lJfEeOV9ZORcHpuxT3O1yJwh6I6KHyt6YrBO)
INSERT INTO `sys_user` (login_name, password, username, phone, sex, id_card, is_admin, status, is_used, is_account_non_expired, is_account_non_locked, is_credentials_non_expired, is_enabled, company_id)
SELECT '物业A管理员', '$2a$10$/mDcuIn2la6FmjaC6lJfEeOV9ZORcHpuxT3O1yJwh6I6KHyt6YrBO', 'admin_wy_a', '13900000001', '1', '123456789012345678', '1', '0', '0', 1, 1, 1, 1, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_user` WHERE username = 'admin_wy_a');

-- Assign Admin Role (Role ID 5) to new user
SET @new_user_id = (SELECT user_id FROM `sys_user` WHERE username = 'admin_wy_a');
INSERT INTO `sys_user_role` (role_id, user_id)
SELECT 5, @new_user_id FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_user_role` WHERE user_id = @new_user_id AND role_id = 5);

-- 7. Add a second Company for Testing
INSERT INTO `sys_company` (company_id, company_name, contact_person, contact_phone, status)
SELECT 2, '测试物业公司B', '李四', '13900000002', '0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_company` WHERE company_id = 2);

-- Add Admin for Company B
INSERT INTO `sys_user` (login_name, password, username, phone, sex, id_card, is_admin, status, is_used, is_account_non_expired, is_account_non_locked, is_credentials_non_expired, is_enabled, company_id)
SELECT '物业B管理员', '$2a$10$/mDcuIn2la6FmjaC6lJfEeOV9ZORcHpuxT3O1yJwh6I6KHyt6YrBO', 'admin_wy_b', '13900000002', '1', '123456789012345679', '1', '0', '0', 1, 1, 1, 1, 2
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_user` WHERE username = 'admin_wy_b');

SET @user_b_id = (SELECT user_id FROM `sys_user` WHERE username = 'admin_wy_b');

-- Create a new Role for Company B
INSERT INTO `sys_role` (role_name, remark, company_id)
SELECT '管理员角色', '公司B管理员', 2
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role` WHERE company_id = 2 AND role_name = '管理员角色');

SET @role_b_id = (SELECT role_id FROM `sys_role` WHERE company_id = 2 AND role_name = '管理员角色');

INSERT INTO `sys_user_role` (role_id, user_id)
SELECT @role_b_id, @user_b_id
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_user_role` WHERE user_id = @user_b_id AND role_id = @role_b_id);

-- Copy permissions from Role 5 to Role B
INSERT INTO `sys_role_menu` (role_id, menu_id)
SELECT @role_b_id, menu_id FROM `sys_role_menu` WHERE role_id = 5
AND NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE role_id = @role_b_id AND menu_id = `sys_role_menu`.menu_id);

