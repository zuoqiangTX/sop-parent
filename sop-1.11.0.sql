DROP TABLE IF EXISTS `isv_keys`;

CREATE TABLE `isv_keys` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `app_key` varchar(128) NOT NULL DEFAULT '应用id（交给开发者）',
  `sign_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1:RSA2,2:MD5',
  `secret` varchar(200) NOT NULL DEFAULT '' COMMENT 'sign_type=2时使用',
  `key_format` tinyint(4) NOT NULL DEFAULT '1' COMMENT '秘钥格式，1：PKCS8(JAVA适用)，2：PKCS1(非JAVA适用)',
  `public_key_isv` text NOT NULL COMMENT '开发者生成的公钥',
  `private_key_isv` text NOT NULL COMMENT '开发者生成的私钥（交给开发者）',
  `public_key_platform` text NOT NULL COMMENT '平台生成的公钥（交给开发者）',
  `private_key_platform` text NOT NULL COMMENT '平台生成的私钥',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_appkey` (`app_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ISV秘钥';

INSERT INTO `sop`.`isv_keys` (`app_key`,`sign_type`, `secret`,`key_format`, `public_key_isv`, `private_key_isv`, `public_key_platform`, `private_key_platform`)
select app_key, sign_type, secret, 1, pub_key,pri_key,'','' from isv_info;