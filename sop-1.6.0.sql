DROP TABLE IF EXISTS `user_info`;

CREATE TABLE `user_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(128) NOT NULL DEFAULT '' COMMENT '密码',
  `nickname` varchar(64) NOT NULL DEFAULT '' COMMENT '昵称',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_unamepwd` (`username`,`password`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';


INSERT INTO `user_info` ( `username`, `password`, `nickname`, `gmt_create`, `gmt_modified`) VALUES
	('zhangsan','123456','张三','2019-04-27 08:32:57','2019-04-27 08:32:57');
