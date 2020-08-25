CREATE TABLE `user_like_post` (
  `account_id` int(11) NOT NULL COMMENT '用户ID',
  `comment_id` int(11) NOT NULL COMMENT '关联回复ID',
  `article_id` int(11) NOT NULL,
  PRIMARY KEY (`account_id`,`comment_id`,`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
