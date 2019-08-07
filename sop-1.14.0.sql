use sop;

CREATE TABLE `config_gray` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `service_id` varchar(64) NOT NULL DEFAULT '',
  `user_key_content` text COMMENT '用户key，多个用引文逗号隔开',
  `name_version_content` text COMMENT '需要灰度的接口，goods.get1.0=1.2，多个用英文逗号隔开',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_serviceid` (`service_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='服务灰度配置';


CREATE TABLE `config_gray_instance` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `instance_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'instance_id',
  `service_id` varchar(64) NOT NULL DEFAULT '' COMMENT 'service_id',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0：禁用，1：启用',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_instanceid` (`instance_id`) USING BTREE,
  KEY `idx_serviceid` (`service_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='开启灰度服务器实例';