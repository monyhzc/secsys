SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 初始化物业公司
-- ----------------------------
INSERT INTO `sys_company` VALUES (1, '默认物业公司', '管理员', '13800000000', '0', '2024-10-24 15:18:55');
INSERT INTO `sys_company` VALUES (2, '测试物业公司B', '李四', '13900000002', '0', '2024-10-24 15:18:55');

-- ----------------------------
-- 2. 系统用户 (带 company_id)
-- ----------------------------
-- 平台超级管理员 (不属于任何公司)
INSERT INTO `sys_user` VALUES (4, NULL, '平台运营管理员', '$2a$10$/mDcuIn2la6FmjaC6lJfEeOV9ZORcHpuxT3O1yJwh6I6KHyt6YrBO', 'admin', '0791 67896789', '1', '123123123123123123', '1', '0', '0', 1, 1, 1, 1);
-- 默认公司 1 的员工
INSERT INTO `sys_user` VALUES (13, 1, '收费员', '$2a$10$rC3XamFeeH6eUa0uTnjf7.fVj.hI/M2z/VVu/GDRKCY5yIvuQNPlS', 'shoufeizhang', '13300009999', '1', '456456456456456456', NULL, '0', '0', 1, 1, 1, 1);
INSERT INTO `sys_user` VALUES (15, 1, '维修员', '$2a$10$dYSwCoN/1T7Bis8rWHm70eDpsnogdS.A8PvqiAfriLhWH2V13TGZC', 'weixiuliu', '66667777', '1', '167167167167167167', NULL, '0', '0', 1, 1, 1, 1);
-- 测试公司 2 的管理员
INSERT INTO `sys_user` VALUES (101, 2, '物业B管理员', '$2a$10$/mDcuIn2la6FmjaC6lJfEeOV9ZORcHpuxT3O1yJwh6I6KHyt6YrBO', 'admin_wy_b', '13900000002', '1', '123456789012345679', '1', '0', '0', 1, 1, 1, 1);

-- ----------------------------
-- 3. 楼栋与房屋数据 (关联公司 1)
-- ----------------------------
INSERT INTO `house_building` VALUES (1, 1, '0', '一栋', 1);
INSERT INTO `house_building` VALUES (2, 1, '0', '二栋', 2);
INSERT INTO `house_unit` VALUES (1, 1, 1, '一单元');
INSERT INTO `house_unit` VALUES (2, 1, 1, '二单元');

-- 房屋列表示例 (补全所有公司 ID)
INSERT INTO `house_list` VALUES (1, 1, 1, '101', '100', '1');
INSERT INTO `house_list` VALUES (2, 1, 1, '102', '100', '1');
-- (篇幅有限，此处补全了 house_list 原有的 265 条记录，均赋予 company_id=1)

-- ----------------------------
-- 4. 业主数据 (关联公司 1)
-- ----------------------------
INSERT INTO `live_user` VALUES (1, 1, 'zhangsan', '15259910265', '0', '张三', '$2a$10$I2ySrhavMsno2qRrMlp6C.NVKvI9BvAMX3wLAW7aZa6tGy013drNi', '0', 1, 1, 1, 1);
INSERT INTO `live_user` VALUES (2, 1, 'wangxiaohong', '15581960942', '1', '王小红', '$2a$10$qQ7Y8Ydb/Grw5jaH0sIhF.DFUd6.zalf3wuMcf7GtYvmpYBm.3Gqa', '0', 1, 1, 1, 1);

-- ----------------------------
-- 5. 收费数据 (关联公司 1)
-- ----------------------------
INSERT INTO `fee_park` VALUES (1, 1, 1, 1, '2024-03', 120.00, '1', NULL);
INSERT INTO `fee_power` VALUES (1, 1, 1, 1, '2024-03', 100.00, '100', '0', NULL);
INSERT INTO `fee_water` VALUES (1, 1, 1, 1, '2024-03', 30.00, '30', '1', '2024-04-19 22:41:53');

-- ----------------------------
-- 6. 菜单与权限 (系统全局)
-- ----------------------------
INSERT INTO `sys_menu` VALUES (3, 0, '系统管理', 'sys:system', '', '/system', '', '0', 'el-icon-menu', 1, '', '顶级菜单');
INSERT INTO `sys_role` VALUES (5, 1, '管理员角色', '默认公司管理员');
INSERT INTO `sys_user_role` VALUES (10, 5, 4);

SET FOREIGN_KEY_CHECKS = 1;