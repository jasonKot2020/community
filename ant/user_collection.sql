CREATE TABLE `user_collection` (
  `account_id` INT(11) NOT NULL,
  `article_id` INT(11) NOT NULL,
  `type` INT(1) NOT NULL,
  PRIMARY KEY (`account_id`, `article_id`, `type`))
COMMENT = '用户收藏';