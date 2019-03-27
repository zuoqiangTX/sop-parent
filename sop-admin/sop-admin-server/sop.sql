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

 Date: 27/03/2019 20:16:41
*/

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
  `status` tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT '0启用，1禁用',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_key` (`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='isv信息表';

-- ----------------------------
-- Table structure for perm_isv_role
-- ----------------------------
DROP TABLE IF EXISTS `perm_isv_role`;
CREATE TABLE `perm_isv_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `isv_info_id` bigint(20) NOT NULL COMMENT 'isv_info.id',
  `role_code` varchar(50) NOT NULL COMMENT '角色code',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`isv_info_id`,`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='isv角色';

-- ----------------------------
-- Table structure for perm_role
-- ----------------------------
DROP TABLE IF EXISTS `perm_role`;
CREATE TABLE `perm_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `role_code` varchar(50) NOT NULL COMMENT '角色代码',
  `description` varchar(50) NOT NULL COMMENT '角色描述',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Table structure for perm_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `perm_role_permission`;
CREATE TABLE `perm_role_permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `role_code` varchar(50) NOT NULL COMMENT '角色表code',
  `route_id` bigint(20) NOT NULL COMMENT 'api_id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_code`,`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色权限表';

SET FOREIGN_KEY_CHECKS = 1;
