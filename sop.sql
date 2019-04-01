/*
 Navicat Premium Data Transfer

 Source Server         : mysql-localhost
 Source Server Type    : MySQL
 Source Server Version : 50724
 Source Host           : localhost:3306
 Source Schema         : sop

 Target Server Type    : MySQL
 Target Server Version : 50724
 File Encoding         : 65001

 Date: 01/04/2019 19:04:48
*/

CREATE DATABASE /*!32312 IF NOT EXISTS*/`sop` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `sop`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for isv_info
-- ----------------------------
DROP TABLE IF EXISTS `isv_info`;
CREATE TABLE `isv_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `app_key` varchar(100) NOT NULL COMMENT 'appKey',
  `secret` varchar(200) NOT NULL COMMENT 'secret',
  `pub_key` text COMMENT '公钥',
  `pri_key` text COMMENT '私钥',
  `status` tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT '1启用，2禁用',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_key` (`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='isv信息表';

-- ----------------------------
-- Records of isv_info
-- ----------------------------
BEGIN;
INSERT INTO `isv_info` VALUES (1, '2019032617262200001', '', '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', 1, '2019-03-27 10:10:34', '2019-04-01 19:01:58');
INSERT INTO `isv_info` VALUES (3, 'asdfasdf', '222', '333', '4444', 1, '2019-03-27 11:01:11', '2019-04-01 16:23:06');
INSERT INTO `isv_info` VALUES (5, '20190331562013861008375808', '29864b93427447f5ac6c44df746f84ef', 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4aMIx2q8rCVu5z6dgNQQSX2vwhvIcRb7FaqSk0ZK8AV9qQeE1TvfFVlAzOHlysE1yTRb0Mb6W2aw7IAS7Bkc3onYBQR4zNQYjYoDBzLukjF8o84hoVFRnh7sV8zszid2vb5H/YQr3M+5sYhlXY8KfILk3vhdbWpHM/umplcrxlwIDAQAB', 'MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALhowjHarysJW7nPp2A1BBJfa/CG8hxFvsVqpKTRkrwBX2pB4TVO98VWUDM4eXKwTXJNFvQxvpbZrDsgBLsGRzeidgFBHjM1BiNigMHMu6SMXyjziGhUVGeHuxXzOzOJ3a9vkf9hCvcz7mxiGVdjwp8guTe+F1takcz+6amVyvGXAgMBAAECgYBj40LFVGoryp7n0CYeg7kX5p4GJGKCk/jY4IIcUPTFZ4zydorxoDuvpag9hmqqh/r7XeyAC23sMi4LvLUzRRxPh+7PuwL6nLce7vytsMCZQTPpBgz7dUfbi2HAxsuMOLjH3sVGycutARJsz6bT+9PyBEuVtUqwBrDGpFvwT0z6yQJBAOANC8nysb+O4rn/fbJtHIhtQoV74yu00mLnfwv8/J1+WyAEc32WZ4KYINqCe8ft1UknhPQx9UV6JaPCnlpF6w0CQQDStJhd38uQ7dVUQZHGP24xS/K38AYiSheEr7uewhkJfC2cKqE/lBk3oEG4s7asjhwlFLWLWSBLVM/Ta9Yj0hYzAkBe82hxl1bY9bcEWFBu02rqLlOouk4V8bXPkIf5DqgIHsqDkR9Ys+r+H3ac4/uNSS/ApuzjiGCHpzJYalwtqb/pAkEAvAKlSm5dCC8QAaSYXJtQyfAI3hPwhTwzjBP6iAiNqqcBU62+QCr37Wiz/Alv4LzVZEj8TSDz7gP5hZ9dbo0RfQJBALJ7NhTaeMN4jxBJ6Xg4rNZPb4yhAXuFxCp+a+FyXTsbWnW/ar8KJ3LHox0GOao6wne4qN3h7eqLOrYnnvOSFl0=', 1, '2019-03-31 20:34:12', '2019-03-31 22:07:50');
INSERT INTO `isv_info` VALUES (6, '20190331562037310372184064', 'd6b2a6603236491f87eed958292be136', 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcZTAaR5K7mk1epfLwK6XRrBiakiwIJK3wtUd8EzpcAnS7Yr8uq5U4yJKZQqu5BtFiWmTFFmAm4aBQKXH1u+6kfcRduYZDLC0cfBPA7IfLnumdrR8uiCgQgGSF71Q4NhebrOqzTAftxZ1vYMCDay1DY2BQxEiGICoCQ0abtvrfNQIDAQAB', 'MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAJxlMBpHkruaTV6l8vArpdGsGJqSLAgkrfC1R3wTOlwCdLtivy6rlTjIkplCq7kG0WJaZMUWYCbhoFApcfW77qR9xF25hkMsLRx8E8Dsh8ue6Z2tHy6IKBCAZIXvVDg2F5us6rNMB+3FnW9gwINrLUNjYFDESIYgKgJDRpu2+t81AgMBAAECgYEAiu/FzQLakvkgVL4eVUihVeSKMv86SL21HMsex1YZmVXBOBsgdqiNt1VQDwFQpt4sszBUp1Yac1Ar6Cr5h3G+LVS17n517MjVeEihrmxK6VL1bUoC9Mpj4ZwuLQ0jeFYVBWEhESV/O59O8YRvk0YpIzUUaLj0+qEQBsPKLPSp8MECQQDY3A9Z+i19fPpd5hBl5x7cyzx9KeX2aru0kUMMmm3JeL1dSPSUuqmnTFdtveQrIUab9OYdJgxLtKoh3J8YqDqFAkEAuJ9jn1VNCIpXF8xsI4w1U5QFdXKnGGZ8PePHqWG1B8A9afj2o7T6gDW/xzX/k8g/2belAR3y4nXGo0EikMso8QJBANXWqYygFZtYUcmlwyW48cXm4o7Jcem69bzoQKV84iV42cHS3tqJ9iDyNoQQa53cAjRnGUJE8nr0e49IbdlyicECQQCz8vdhJ/1ro5t8IM4OX+ziR9aCQXxItivHDytfF1Mh+OhjUDzmF9JKARmqDCHOY1KI3QzBZ/WDcZRpL2WHh92hAkEAyetUnYn1F5zVJWxo+yC9PH6WYssZahNy22GdBcZblmDc/YQWw+sa9d55/5aMoYWn6AVO9Tb2gsMW2W0jM2/zCw==', 2, '2019-03-31 22:07:20', '2019-03-31 22:07:26');
COMMIT;

-- ----------------------------
-- Table structure for perm_isv_role
-- ----------------------------
DROP TABLE IF EXISTS `perm_isv_role`;
CREATE TABLE `perm_isv_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `isv_id` bigint(20) NOT NULL COMMENT 'isv_info表id',
  `role_code` varchar(64) NOT NULL COMMENT '角色code',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`isv_id`,`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8 COMMENT='isv角色';

-- ----------------------------
-- Records of perm_isv_role
-- ----------------------------
BEGIN;
INSERT INTO `perm_isv_role` VALUES (16, 6, 'normal', '2019-03-31 22:07:26', '2019-03-31 22:07:26');
INSERT INTO `perm_isv_role` VALUES (17, 6, 'vip', '2019-03-31 22:07:26', '2019-03-31 22:07:26');
INSERT INTO `perm_isv_role` VALUES (18, 5, 'normal', '2019-03-31 22:07:50', '2019-03-31 22:07:50');
INSERT INTO `perm_isv_role` VALUES (29, 1, 'normal', '2019-04-01 19:01:58', '2019-04-01 19:01:58');
COMMIT;

-- ----------------------------
-- Table structure for perm_role
-- ----------------------------
DROP TABLE IF EXISTS `perm_role`;
CREATE TABLE `perm_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `role_code` varchar(64) NOT NULL COMMENT '角色代码',
  `description` varchar(64) NOT NULL COMMENT '角色描述',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Records of perm_role
-- ----------------------------
BEGIN;
INSERT INTO `perm_role` VALUES (1, 'normal', '普通权限', '2019-03-29 15:00:10', '2019-03-29 15:00:10');
INSERT INTO `perm_role` VALUES (2, 'vip', 'VIP权限', '2019-03-29 15:00:27', '2019-03-29 15:00:27');
COMMIT;

-- ----------------------------
-- Table structure for perm_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `perm_role_permission`;
CREATE TABLE `perm_role_permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `role_code` varchar(64) NOT NULL COMMENT '角色表code',
  `route_id` varchar(64) NOT NULL COMMENT 'api_id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_code`,`route_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='角色权限表';

-- ----------------------------
-- Records of perm_role_permission
-- ----------------------------
BEGIN;
INSERT INTO `perm_role_permission` VALUES (8, 'normal', 'permission.story.get1.0', '2019-04-01 19:02:31', '2019-04-01 19:02:31');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
