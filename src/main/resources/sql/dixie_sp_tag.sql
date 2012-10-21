delimiter //
-- ----------------------------------------------------------------------------
-- createTag
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Create a tag and return its id. Should/ will not cause an error if
-- the tag already exists.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS createTag//
CREATE PROCEDURE createTag(IN aTag TEXT CHARACTER SET utf8,
                           OUT aTagId INT UNSIGNED)
BEGIN
  -- `created_on` should be the number of milliseconds, but UNIX_TIMESTAMP()
  -- returns the number of seconds since the epoch.
  INSERT IGNORE INTO `tag`
    SET `tag`=aTag,
        `created_on`=UNIX_TIMESTAMP() * 1000;

  -- Do SELECT .. INTO instead of SELECT_LAST_INSERT_ID() here because we want
  -- this procedure to work the same whether the tag already exists or not.
  SELECT `id` INTO aTagId
    FROM `tag`
    WHERE `tag`=aTag;
END//

-- ----------------------------------------------------------------------------
-- readTag
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Get/read the requested tag by id.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS readTag//
CREATE PROCEDURE readTag(IN aTagId INT UNSIGNED)
BEGIN
  SELECT *, `count` AS tag_count
    FROM `tag`
    WHERE `id`=aTagId;
END//

-- ----------------------------------------------------------------------------
-- readTagByName
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created June 12, 2009
-- \brief Get/read the requested tag by name.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS readTagByName//
CREATE PROCEDURE readTagByName(IN aTagName TEXT CHARACTER SET utf8)
BEGIN
  SELECT *, `count` AS tag_count
    FROM `tag`
    WHERE `tag`=aTagName;
END//

-- ----------------------------------------------------------------------------
-- updateTag
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Update a tag.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS updateTag//
CREATE PROCEDURE updateTag(IN aTagId INT UNSIGNED,
                           IN aTag TEXT CHARACTER SET utf8)
BEGIN
  UPDATE `tag`
    SET `tag`=aTag
    WHERE `id`=aTagId;
END//

-- ----------------------------------------------------------------------------
-- deleteTag
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Delete the given tag.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS deleteTag//
CREATE PROCEDURE deleteTag(IN aTagId INT UNSIGNED)
BEGIN
  DELETE
    FROM `tag`
    WHERE `id`=aTagId;
END//

-- ----------------------------------------------------------------------------
-- pageTagsByLinkFormatAndTags
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created July 11, 2009
-- \brief Get a page of tags related to the format and comma-delimited list of
-- tag ids given (excluding those tags), or a page of all tags if no list is
-- given.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS pageTagsByLinkFormatAndTags//
CREATE PROCEDURE pageTagsByLinkFormatAndTags(IN aFormat INT UNSIGNED,
                                             IN aTags TEXT CHARACTER SET utf8,
                                             IN aOffset INT UNSIGNED,
                                             IN aCount INT UNSIGNED,
                                             IN aOrder INT UNSIGNED,
                                             OUT aTotalCount INT UNSIGNED)
BEGIN
  SET @qry = "
    SELECT SQL_CALC_FOUND_ROWS `tag`.*, COUNT(*) AS tag_count
      FROM `tag`
        INNER JOIN `link_to_tag` ON `link_to_tag`.`tag_id` = `tag`.`id`";

  -- A format of zero means no specific format.
  IF aFormat <> 0 THEN
    SET @qry = CONCAT(@qry,"
        INNER JOIN `link` ON `link`.`id` = `link_to_tag`.`link_id` AND `link`.`format_id` = ");
    SET @qry = CONCAT(@qry, aFormat);
  END IF;

  SET @qry = CONCAT(@qry, getTagTagsInnerJoinString(aTags));

  IF LENGTH(aTags) > 0 THEN
    SET @qry = CONCAT(@qry, "
      WHERE `tag`.`id` NOT IN(");
    SET @qry = CONCAT(@qry, aTags);
    SET @qry = CONCAT(@qry, ")");
  END IF;

  SET @qry = CONCAT(@qry, "
      GROUP BY `tag`.`id`
      ORDER BY ");

  SET @qry = CONCAT(@qry, getTagOrderString(aOrder));

  SET @qry = CONCAT(@qry, "
      LIMIT ?, ?");

  SET @offset = aOffset;
  SET @count = aCount;

  PREPARE STMT FROM @qry;
  EXECUTE STMT USING @offset, @count;

  SELECT FOUND_ROWS() INTO aTotalCount;
END//

-- ----------------------------------------------------------------------------
-- pageTagsByLinkFormatAndTagsWithUserRelation
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created July 19, 2009
-- \brief Get a page of tags where every tag describes links with the given
-- format and links related to the given user (where the relation is also
-- specified).
-- \param aFormat a format id (zero means no format).
-- \param aTags a comma-delimited list of tag ids.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS pageTagsByLinkFormatAndTagsWithUserRelation//
CREATE PROCEDURE pageTagsByLinkFormatAndTagsWithUserRelation(IN aFormat INT UNSIGNED,
                                                             IN aTags TEXT CHARACTER SET utf8,
                                                             IN aUserId INT UNSIGNED,
                                                             IN aRelation INT UNSIGNED,
                                                             IN aOffset INT UNSIGNED,
                                                             IN aCount INT UNSIGNED,
                                                             IN aOrder INT UNSIGNED,
                                                             OUT aTotalCount INT UNSIGNED)
BEGIN
  SET @qry = "
    SELECT SQL_CALC_FOUND_ROWS `tag`.*, COUNT(*) AS tag_count
      FROM `tag`
        INNER JOIN `link_to_tag` ON `link_to_tag`.`tag_id` = `tag`.`id`
        INNER JOIN `link` ON `link`.`id` = `link_to_tag`.`link_id`";

  -- A format of zero means no specific format.
  IF aFormat <> 0 THEN
    SET @qry = CONCAT(@qry,"
                         AND `link`.`format_id` = ");
    SET @qry = CONCAT(@qry, aFormat);
  END IF;

  IF aRelation = 0 THEN
    -- Submitted by this user.
    SET @qry = CONCAT(@qry, "
                         AND `link`.`user_id`=");
    SET @qry = CONCAT(@qry, aUserId);
  ELSEIF aRelation = 1 THEN
    -- Favorited by this user.
    SET @qry = CONCAT(@qry, "
        INNER JOIN `link_favorite` ON `link_favorite`.`link_id` = `link`.`id`");
  ELSEIF aRelation = 2 THEN
    -- Promoted by this user.
    SET @qry = CONCAT(@qry, "
        INNER JOIN `link_promotion` ON `link_promotion`.`link_id` = `link`.`id`");
  END IF;

  SET @qry = CONCAT(@qry, getTagTagsInnerJoinString(aTags));

  -- Exclude tags already in the tag list.
  IF LENGTH(aTags) > 0 THEN
    SET @qry = CONCAT(@qry, "
        AND `tag`.`id` NOT IN(");
    SET @qry = CONCAT(@qry, aTags);
    SET @qry = CONCAT(@qry, ")");
  END IF;

  SET @qry = CONCAT(@qry, "
      GROUP BY `tag`.`id`
      ORDER BY ");

  SET @qry = CONCAT(@qry, getTagOrderString(aOrder));

  SET @qry = CONCAT(@qry, "
      LIMIT ?, ?");

  SET @offset = aOffset;
  SET @count = aCount;

  PREPARE STMT FROM @qry;
  EXECUTE STMT USING @offset, @count;

  SELECT FOUND_ROWS() INTO aTotalCount;
END//

-- ----------------------------------------------------------------------------
-- getTagTagsInnerJoinString
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created July 18, 2009
-- \brief Returns an inner join for each tag in the list of comma-delimited
-- tag ids given.
-- ----------------------------------------------------------------------------
DROP FUNCTION IF EXISTS getTagTagsInnerJoinString//
CREATE FUNCTION getTagTagsInnerJoinString(aTags TEXT CHARACTER SET utf8)
RETURNS TEXT DETERMINISTIC
BEGIN
  DECLARE len INT DEFAULT 0;
  DECLARE lft INT DEFAULT 0;
  DECLARE rgt INT DEFAULT 0;
  DECLARE qry TEXT DEFAULT "";

  SET len = LENGTH(aTags);

  tags: LOOP
    SET lft = rgt + 1;
    SET rgt = LOCATE(',', aTags, lft);

    IF rgt = 0 THEN
      SET rgt = len + 1;
    END IF;

    IF rgt <= lft THEN
      LEAVE tags;
    END IF;

    SET qry = CONCAT(qry, "\n        INNER JOIN `link_to_tag` ");
    SET qry = CONCAT(qry, CONCAT("l2t", rgt));
    SET qry = CONCAT(qry, " ON ");
    SET qry = CONCAT(qry, CONCAT("l2t", rgt));
    SET qry = CONCAT(qry, ".`link_id` = `link_to_tag`.`link_id`");
    SET qry = CONCAT(qry, " AND ");
    SET qry = CONCAT(qry, CONCAT("l2t", rgt));
    SET qry = CONCAT(qry, ".`tag_id` = ");
    SET qry = CONCAT(qry, SUBSTRING(aTags, lft, rgt - lft));
  END LOOP tags;

  RETURN qry;
END//

-- ----------------------------------------------------------------------------
-- getTagOrderString
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created June 12, 2009
-- \brief Determines the order by expression corresponding to the given
-- order code.
-- ----------------------------------------------------------------------------
DROP FUNCTION IF EXISTS getTagOrderString//
CREATE FUNCTION getTagOrderString(aOrder INT)
RETURNS TEXT DETERMINISTIC
BEGIN
  IF (aOrder = 5) THEN
    -- oldest first
    RETURN " tag.id ASC";
  ELSEIF (aOrder = 4) THEN
    -- newest first
    RETURN " tag.id DESC";
  ELSEIF (aOrder = 3) THEN
    -- name reverse alphabetically
    RETURN " tag.tag DESC";
  ELSEIF (aOrder = 2) THEN
    -- name alphabetically
    RETURN " tag.tag ASC";
  ELSEIF (aOrder = 1) THEN
    -- least common first
    RETURN " tag_count ASC";
  ELSE
    -- most common first
    RETURN " tag_count DESC";
  END IF;
END//