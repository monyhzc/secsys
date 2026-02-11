-- 同步 database/grad_wygl.sql 的内容
CREATE DATABASE IF NOT EXISTS `grad_wygl` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
USE `grad_wygl`;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `login_name` varchar(36) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `username` varchar(36) DEFAULT NULL,
  `phone` varchar(16) DEFAULT NULL,
  `sex` varchar(2) DEFAULT NULL,
  `id_card` varchar(36) DEFAULT NULL,
  `is_admin` varchar(2) DEFAULT NULL,
  `status` varchar(2) DEFAULT NULL,
  `is_used` varchar(2) DEFAULT NULL,
  `is_account_non_expired` int DEFAULT 1,
  `is_account_non_locked` int DEFAULT 1,
  `is_credentials_non_expired` int DEFAULT 1,
  `is_enabled` int DEFAULT 1,
  `company_id` int DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 100 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

INSERT INTO `sys_user` (user_id, login_name, password, username, phone, sex, id_card, is_admin, status, is_used, company_id) 
VALUES (4, '超级管理员', '$2a$10$/mDcuIn2la6FmjaC6lJfEeOV9ZORcHpuxT3O1yJwh6I6KHyt6YrBO', 'admin', '13800000000', '1', '110101199001011234', '1', '0', '0', NULL);

DROP TABLE IF EXISTS `live_user`;
CREATE TABLE `live_user`  (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(36) DEFAULT NULL,
  `phone` varchar(16) DEFAULT NULL,
  `sex` varchar(2) DEFAULT NULL,
  `login_name` varchar(36) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `status` varchar(2) DEFAULT NULL,
  `is_account_non_expired` int DEFAULT 1,
  `is_account_non_locked` int DEFAULT 1,
  `is_credentials_non_expired` int DEFAULT 1,
  `is_enabled` int DEFAULT 1,
  `company_id` int DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 100 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

INSERT INTO `live_user` (user_id, username, phone, sex, login_name, password, status, company_id) 
VALUES (1, 'yezhu', '13900000000', '1', '张三', '$2a$10$/mDcuIn2la6FmjaC6lJfEeOV9ZORcHpuxT3O1yJwh6I6KHyt6YrBO', '0', 1);

-- 此处省略其他业务表的 CREATE TABLE 语句，为了确保登录，先确保上面的表存在
SET FOREIGN_KEY_CHECKS = 1;