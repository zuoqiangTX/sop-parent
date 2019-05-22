
DROP TABLE IF EXISTS `config_limit`;


CREATE TABLE `config_limit` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `route_id` varchar(128) DEFAULT NULL COMMENT '路由id',
  `app_key` varchar(128) DEFAULT NULL,
  `limit_ip` varchar(300) DEFAULT NULL COMMENT '限流ip，多个用英文逗号隔开',
  `service_id` varchar(64) NOT NULL DEFAULT '' COMMENT '服务id',
  `limit_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '限流策略，1：漏桶策略，2：令牌桶策略',
  `exec_count_per_second` int(11) DEFAULT NULL COMMENT '每秒可处理请求数',
  `limit_code` varchar(64) DEFAULT NULL COMMENT '返回的错误码',
  `limit_msg` varchar(100) DEFAULT NULL COMMENT '返回的错误信息',
  `token_bucket_count` int(11) DEFAULT NULL COMMENT '令牌桶容量',
  `limit_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '限流开启状态，1:开启，0关闭',
  `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '顺序，值小的优先执行',
  `remark` varchar(128) DEFAULT NULL COMMENT '备注',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='限流配置';

INSERT INTO `config_limit` (`id`, `route_id`, `app_key`, `limit_ip`, `service_id`, `limit_type`, `exec_count_per_second`, `limit_code`, `limit_msg`, `token_bucket_count`, `limit_status`, `order_index`, `remark`, `gmt_create`, `gmt_modified`) VALUES
	(1,'alipay.story.get1.0','','192.168.1.1,172.2.2.3','story-service',2,5,'','',6,1,3,NULL,'2019-05-17 19:21:35','2019-05-21 09:12:15'),
	(2,'alipay.story.get1.0','2019032617262200001','','story-service',1,5,'service-budy','服务器忙',5,1,0,NULL,'2019-05-17 19:39:30','2019-05-21 15:36:52'),
	(3,'alipay.story.find1.0','20190331562013861008375808','','story-service',1,3,'service-busy','服务器忙',5,1,1,NULL,'2019-05-17 20:20:32','2019-05-20 17:40:17'),
	(4,'alipay.story.get1.2','','','story-service',2,5,'','',3,1,1,NULL,'2019-05-20 16:27:21','2019-05-21 15:53:10'),
	(5,'','20190401562373796095328256','','story-service',1,5,'service-busy','服务器忙',5,1,0,'这个appKey调用很频繁，重点照顾','2019-05-21 15:48:08','2019-05-21 18:45:32'),
	(6,'','','10.1.30.54','story-service',1,5,'service-busy','服务器忙',5,1,0,'这个ip在攻击我们','2019-05-21 15:55:33','2019-05-21 18:17:29'),
	(7,'story.get1.1','','10.1.30.54','story-service',1,5,'service-busy','服务器忙',5,1,0,NULL,'2019-05-21 16:30:48','2019-05-21 16:30:48'),
	(8,'','20190513577548661718777857','10.1.30.54','story-service',1,5,'service-busy','服务器忙',5,1,0,NULL,'2019-05-21 17:10:45','2019-05-21 17:10:52');