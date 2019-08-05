use sop;

CREATE TABLE `config_gray_userkey` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `instance_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'serviceId',
  `user_key_content` text COMMENT '用户key，多个用引文逗号隔开',
  `name_version_content` text COMMENT '需要灰度的接口，goods.get=1.2,order.list=1.2',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0：禁用，1：启用',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_instanceid` (`instance_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='灰度发布用户key';