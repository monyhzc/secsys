SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 物业公司基础表 (新增)
-- ----------------------------
DROP TABLE IF EXISTS `sys_company`;
CREATE TABLE `sys_company` (
  `company_id` int NOT NULL AUTO_INCREMENT COMMENT '公司id',
  `company_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '公司名称',
  `contact_person` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系电话',
  `status` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '0' COMMENT '状态 0：启用 1：禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`company_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='物业公司表';

-- ----------------------------
-- 2. 核心业务表 (均已增加 company_id 字段)
-- ----------------------------

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `company_id` int NULL DEFAULT NULL COMMENT '所属物业公司ID',
  `login_name` varchar(36) COMMENT '姓名',
  `password` varchar(64) COMMENT '登录密码',
  `username` varchar(36) COMMENT '登录账号',
  `phone` varchar(16) COMMENT '电话',
  `sex` varchar(2) COMMENT '性别 0：女 1：男',
  `id_card` varchar(36) COMMENT '身份证号码',
  `is_admin` varchar(2) COMMENT '是否是管理员 0：不是 1：是',
  `status` varchar(2) COMMENT '0：在职  1：离职',
  `is_used` varchar(2) COMMENT '0：启用 1：禁用',
  `is_account_non_expired` int,
  `is_account_non_locked` int,
  `is_credentials_non_expired` int,
  `is_enabled` int,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` int NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `company_id` int NULL DEFAULT NULL COMMENT '所属物业公司ID',
  `role_name` varchar(56) COMMENT '角色名称',
  `remark` varchar(56) COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `house_list`;
CREATE TABLE `house_list` (
  `house_id` int NOT NULL AUTO_INCREMENT COMMENT '房屋id',
  `company_id` int NULL DEFAULT NULL COMMENT '所属物业公司ID',
  `unit_id` int NULL DEFAULT NULL COMMENT '单元id',
  `house_num` varchar(64) COMMENT '房屋编号',
  `house_area` varchar(128) COMMENT '房屋面积',
  `status` varchar(2) COMMENT '使用状态 0:未使用 1：已使用',
  PRIMARY KEY (`house_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `house_building`;
CREATE TABLE `house_building` (
  `build_id` int NOT NULL AUTO_INCREMENT COMMENT '栋数id',
  `company_id` int NULL DEFAULT NULL COMMENT '所属物业公司ID',
  `type` varchar(2) COMMENT '0：普通房 1：电梯房',
  `name` varchar(64) COMMENT '栋数名称',
  `order_num` int NULL DEFAULT NULL,
  PRIMARY KEY (`build_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `house_unit`;
CREATE TABLE `house_unit` (
  `unit_id` int NOT NULL AUTO_INCREMENT COMMENT '单元id',
  `company_id` int NULL DEFAULT NULL COMMENT '所属物业公司ID',
  `build_id` int NULL DEFAULT NULL COMMENT '栋数id',
  `unit_name` varchar(64) COMMENT '单元名称',
  PRIMARY KEY (`unit_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `parking_list`;
CREATE TABLE `parking_list` (
  `park_id` int NOT NULL AUTO_INCREMENT COMMENT '车位id',
  `company_id` int NULL DEFAULT NULL COMMENT '所属物业公司ID',
  `park_type` varchar(2) COMMENT '0:地上 1：地下',
  `vehicle_type` varchar(2) COMMENT '0:小汽车 1:货车 2:巴士 3:摩托车',
  `park_name` varchar(36) COMMENT '车位名称',
  `park_status` varchar(2) COMMENT '0：未使用 1：已使用',
  `park_num` int NULL DEFAULT NULL,
  PRIMARY KEY (`park_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `live_user`;
CREATE TABLE `live_user` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '业主id',
  `company_id` int NULL DEFAULT NULL COMMENT '所属物业公司ID',
  `username` varchar(36) COMMENT '登录账号',
  `phone` varchar(16),
  `sex` varchar(2),
  `login_name` varchar(36) COMMENT '姓名',
  `password` varchar(64),
  `status` varchar(2) COMMENT '0：启用 1：禁用',
  `is_account_non_expired` int,
  `is_account_non_locked` int,
  `is_credentials_non_expired` int,
  `is_enabled` int,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `fee_park`;
CREATE TABLE `fee_park` (
  `park_fee_id` int NOT NULL AUTO_INCREMENT,
  `company_id` int NULL DEFAULT NULL COMMENT '所属物业公司ID',
  `user_id` int NULL DEFAULT NULL,
  `park_id` int NULL DEFAULT NULL,
  `pay_park_month` varchar(16),
  `pay_park_money` decimal(18, 2),
  `pay_park_status` varchar(2),
  `pay_park_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`park_fee_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

DROP TABLE IF EXISTS `fee_power`;
CREATE TABLE `fee_power` (
  `power_id` int NOT NULL AUTO_INCREMENT,
  `company_id` int NULL DEFAULT NULL COMMENT '所属物业公司ID',
  `house_id` int NULL DEFAULT NULL,
  `user_id` int NULL DEFAULT NULL,
  `pay_power_month` varchar(16),
  `pay_power_money` decimal(18, 2),
  `power_num` varchar(16),
  `pay_power_status` varchar(2),
  `pay_power_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`power_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

DROP TABLE IF EXISTS `fee_water`;
CREATE TABLE `fee_water` (
  `water_id` int NOT NULL AUTO_INCREMENT,
  `company_id` int NULL DEFAULT NULL COMMENT '所属物业公司ID',
  `house_id` int NULL DEFAULT NULL,
  `user_id` int NULL DEFAULT NULL,
  `pay_water_month` varchar(16),
  `pay_water_money` decimal(18, 2),
  `waterr_num` varchar(16),
  `pay_water_status` varchar(2),
  `pay_water_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`water_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- 3. 其他系统辅助表 (无公司隔离字段)
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` int NOT NULL AUTO_INCREMENT,
  `parent_id` int,
  `menu_label` varchar(56),
  `menu_code` varchar(56),
  `name` varchar(56),
  `path` varchar(64),
  `url` varchar(128),
  `type` varchar(2) COMMENT '0：目录 1：菜单 2：按钮',
  `icon` varchar(64),
  `order_num` int,
  `remark` varchar(128),
  `parent_name` varchar(56),
  PRIMARY KEY (`menu_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4;

DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_menu_id` int NOT NULL AUTO_INCREMENT,
  `role_id` int,
  `menu_id` int,
  PRIMARY KEY (`role_menu_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4;

DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_role_id` int NOT NULL AUTO_INCREMENT,
  `role_id` int,
  `user_id` int,
  PRIMARY KEY (`user_role_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;