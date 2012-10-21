-- SET FOREIGN_KEY_CHECKS=0;

--
-- Table structure for table `link`
--
DROP TABLE IF EXISTS `link`;
CREATE TABLE `link`
(
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `url` TEXT NOT NULL,
  `url_hash` BINARY(20) NOT NULL,
  `thumb_url` TEXT NOT NULL,
  `format_id` INT(10) UNSIGNED NOT NULL DEFAULT '0',
  `category_id` INT(10) UNSIGNED NOT NULL DEFAULT '0',
  `title` VARCHAR(65) NOT NULL,
  `notes` TEXT NOT NULL,
  `created_on` BIGINT(20) SIGNED NOT NULL,
  `comments` INT(10) UNSIGNED NOT NULL DEFAULT '0',
  `views` INT(10) UNSIGNED NOT NULL DEFAULT '0',
  `promotions` INT(10) UNSIGNED NOT NULL DEFAULT '0',
  `demotions` INT(10) UNSIGNED NOT NULL DEFAULT '0',
  `favorites` INT(10) UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `url_hash_index` USING BTREE (`url_hash`),
  KEY `id_format_id_index` (`id`,`format_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `link_promotion`
--
DROP TABLE IF EXISTS `link_promotion`;
CREATE TABLE `link_promotion`
(
  `link_id` INT(10) UNSIGNED NOT NULL,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `created_on` BIGINT(20) SIGNED NOT NULL,
  PRIMARY KEY (`link_id`,`user_id`),
  KEY `user_id_link_id_index` (`user_id`,`link_id`)
-- ,CONSTRAINT `FK_link_promotion_1` FOREIGN KEY (`link_id`) REFERENCES `link` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
-- ,CONSTRAINT `FK_link_promotion_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `link_demotion`
-- Avoid use of ENUM since (string) values may change.
--
DROP TABLE IF EXISTS `link_demotion`;
CREATE TABLE `link_demotion`
(
  `link_id` INT(10) UNSIGNED NOT NULL,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `reason` TINYINT(3) UNSIGNED NOT NULL,
  `created_on` BIGINT(20) SIGNED NOT NULL,
  PRIMARY KEY (`link_id`,`user_id`),
  KEY `user_id_link_id_index` (`user_id`,`link_id`)
-- ,CONSTRAINT `FK_link_demotion_1` FOREIGN KEY (`link_id`) REFERENCES `link` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
-- ,CONSTRAINT `FK_link_demotion_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `link_favorite`
--
DROP TABLE IF EXISTS `link_favorite`;
CREATE TABLE `link_favorite`
(
  `link_id` INT(10) UNSIGNED NOT NULL,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `created_on` BIGINT(20) SIGNED NOT NULL,
  PRIMARY KEY (`link_id`,`user_id`),
  KEY `user_id_link_id_index` (`user_id`,`link_id`)
-- ,CONSTRAINT `FK_link_promotion_1` FOREIGN KEY (`link_id`) REFERENCES `link` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
-- ,CONSTRAINT `FK_link_promotion_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `link_to_tag`
--
DROP TABLE IF EXISTS `link_to_tag`;
CREATE TABLE `link_to_tag`
(
  `tag_id` INT(10) UNSIGNED NOT NULL,
  `link_id` INT(10) UNSIGNED NOT NULL,
  `position` TINYINT(3) UNSIGNED NOT NULL,
  PRIMARY KEY (`link_id`,`tag_id`),
  KEY `tag_id_link_id_index` (`tag_id`,`link_id`)
-- ,CONSTRAINT `FK_link_to_tag_1` FOREIGN KEY (`link_id`) REFERENCES `link` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
-- ,CONSTRAINT `FK_link_to_tag_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `link_tag_count`
--
DROP TABLE IF EXISTS `link_tag_count`;
CREATE TABLE `link_tag_count`
(
  `tag_id` INT(10) UNSIGNED NOT NULL,
  `count` INT(10) UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY (`tag_id`),
  KEY `count_tag_id_index` (`count`,`tag_id`)
-- ,CONSTRAINT `FK_link_tag_count_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `tag`
--
-- \brief Start with tags being case-insensitive then move to case-sensitive
-- if we choose (easier than moving from cs->ci).
--
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`
(
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `tag` VARCHAR(45) NOT NULL,
  `count` INT(10) UNSIGNED NOT NULL,
  `created_on` BIGINT(20) SIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tag_index` (`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `format`
--
DROP TABLE IF EXISTS `format`;
CREATE TABLE `format`
(
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `format` VARCHAR(45) NOT NULL,
  `folder` VARCHAR(45) NOT NULL,
  `place` TINYINT(3) UNSIGNED NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE `format_index` (`format`),
  UNIQUE `folder_index` (`folder`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `category`
--
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`
(
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `parent_id` INT(10) UNSIGNED NOT NULL,
  `category` VARCHAR(45) NOT NULL,
  `place` TINYINT(3) UNSIGNED NOT NULL DEFAULT '1',
  `lft` INT(10) UNSIGNED NOT NULL DEFAULT '0',
  `rgt` INT(10) UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `parent_id_category_index` (`parent_id`,`category`),
  KEY `lft_index` (`lft`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `user`
--
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(16) COLLATE utf8_general_ci NOT NULL,
  `password_hash` CHAR(60) CHARSET ascii NOT NULL,
  `email` VARCHAR(40) COLLATE utf8_general_ci NOT NULL,
  `birth_date` BIGINT SIGNED NOT NULL,
  `first_name` VARCHAR(100) NOT NULL,
  `last_name` VARCHAR(100) NOT NULL,
  `account_status` INT(10) UNSIGNED NOT NULL,
  `created_on` BIGINT(20) SIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_index` (`username`),
  UNIQUE KEY `email_index` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `comment`
--
DROP TABLE IF EXISTS `comment`;
CREATE TABLE  `comment`
(
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `parent_id` INT(10) UNSIGNED NOT NULL DEFAULT '0',
  `top_id` INT(10) UNSIGNED NOT NULL DEFAULT '0',
  `user_id` INT(10) UNSIGNED NOT NULL,
  `link_id` INT(10) UNSIGNED NOT NULL,
  `text` TEXT NOT NULL,
  `created_on` BIGINT(20) SIGNED NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id_index` (`user_id`),
  KEY `link_id_parent_id_index` (`link_id`,`parent_id`),
  KEY `top_id_index` (`top_id`)
-- ,CONSTRAINT `FK_comment_1` FOREIGN KEY (`link_id`) REFERENCES `link` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
-- ,CONSTRAINT `FK_comment_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `comment_rating`
--
DROP TABLE IF EXISTS `comment_rating`;
CREATE TABLE `comment_rating`
(
  `comment_id` INT(10) UNSIGNED NOT NULL,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `rating` TINYINT(3) NOT NULL,
  `created_on` BIGINT(20) SIGNED NOT NULL,
  PRIMARY KEY (`comment_id`,`user_id`)
-- ,CONSTRAINT `FK_comment_rating_1` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
-- ,CONSTRAINT `FK_comment_rating_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `captcha`
--
DROP TABLE IF EXISTS `captcha`;
CREATE TABLE `captcha`
(
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `uuid_most_sig` BIGINT(20) SIGNED NOT NULL,
  `uuid_least_sig` BIGINT(20) SIGNED NOT NULL,
  `answer` CHAR(16) NOT NULL,
  `seed` BIGINT(20) SIGNED NOT NULL,
  `created_on` BIGINT(20) SIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid_index` USING BTREE (`uuid_most_sig`, `uuid_least_sig`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- SET FOREIGN_KEY_CHECKS=1;
