DROP TABLE IF EXISTS `config_common`;

CREATE TABLE `config_common` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `config_group` varchar(64) NOT NULL DEFAULT '' COMMENT '配置分组',
  `config_key` varchar(64) NOT NULL DEFAULT '' COMMENT '配置key',
  `content` varchar(128) NOT NULL DEFAULT '' COMMENT '内容',
  `remark` varchar(128) DEFAULT NULL COMMENT '备注',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_groupkey` (`config_group`,`config_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='通用配置表';
