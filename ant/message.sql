CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` text COLLATE utf8_bin COMMENT '内容',
  `sender` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '发送人',
  `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
  `creator` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='社区吐槽';
