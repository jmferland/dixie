delimiter //
-- ----------------------------------------------------------------------------
-- createLink
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Create a link and return its id.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS createLink//
CREATE PROCEDURE createLink(IN aUserId INT UNSIGNED,
                            IN aUrl TEXT CHARACTER SET utf8,
                            IN aUrlHash BINARY(20),
                            IN aSourceThumbUrl TEXT CHARACTER SET utf8,
                            IN aFormat INT UNSIGNED,
                            IN aCategory INT UNSIGNED,
                            IN aTitle TEXT CHARACTER SET utf8,
                            IN aNotes TEXT CHARACTER SET utf8,
                            IN aCreatedOn BIGINT SIGNED,
                            OUT aLinkId INT UNSIGNED)
BEGIN
  INSERT INTO `link`
    SET `user_id`=aUserId,
        `url`=aUrl,
        `url_hash`=aUrlHash,
        `thumb_url`=aSourceThumbUrl,
        `format_id`=aFormat,
        `category_id`=aCategory,
        `title`=aTitle,
        `notes`=aNotes,
        `created_on`=aCreatedOn,
        `comments`=0,
        `views`=0,
        `promotions`=0;
  SELECT LAST_INSERT_ID() INTO aLinkId;
END//

-- ----------------------------------------------------------------------------
-- readLink
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Get/read the requested link by id (along with the associated user
-- and tag information).
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS readLink//
CREATE PROCEDURE readLink(IN aLinkId INT UNSIGNED)
BEGIN
  SELECT `link`.*, `user`.*,
         GROUP_CONCAT(`tag` ORDER BY `position`) AS link_tags
    FROM `link`
      INNER JOIN `user` ON `user`.`id`=`link`.`user_id`
      INNER JOIN `link_to_tag` ON `link_to_tag`.`link_id`=`link`.`id`
      INNER JOIN `tag` ON `tag`.`id`=`link_to_tag`.`tag_id`
    WHERE `link`.`id`=aLinkId
    GROUP BY `link`.`id`;
END//

-- ----------------------------------------------------------------------------
-- readLinkByUrlHash
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 20, 2009
-- \brief Get/read the requested link by url hash (along with the associated
-- user and tag information). I don't like that this is essentially a
-- duplication of readLink with the WHERE condition changed - I WANT MACROS!
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS readLinkByUrlHash//
CREATE PROCEDURE readLinkByUrlHash(IN aUrlHash BINARY(20))
BEGIN
  SELECT `link`.*, `user`.*,
         GROUP_CONCAT(`tag` ORDER BY `position`) AS link_tags
    FROM `link`
      INNER JOIN `user` ON `user`.`id`=`link`.`user_id`
      INNER JOIN `link_to_tag` ON `link_to_tag`.`link_id`=`link`.`id`
      INNER JOIN `tag` ON `tag`.`id`=`link_to_tag`.`tag_id`
    WHERE `link`.`url_hash`=aUrlHash
    GROUP BY `link`.`id`;
END//

-- ----------------------------------------------------------------------------
-- updateLink
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Update a link.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS updateLink//
CREATE PROCEDURE updateLink(IN aLinkId INT UNSIGNED,
                            IN aUserId INT UNSIGNED,
                            IN aUrl TEXT CHARACTER SET utf8,
                            IN aUrlHash BINARY(20),
                            IN aSourceThumbUrl TEXT CHARACTER SET utf8,
                            IN aFormat INT UNSIGNED,
                            IN aCategory INT UNSIGNED,
                            IN aTitle TEXT CHARACTER SET utf8,
                            IN aNotes TEXT CHARACTER SET utf8)
BEGIN
  UPDATE `link`
    SET `user_id`=aUserId,
        `url`=aUrl,
        `url_hash`=aUrlHash,
        `thumb_url`=aSourceThumbUrl,
        `format_id`=aFormat,
        `category_id`=aCategory,
        `title`=aTitle,
        `notes`=aNotes
    WHERE `id`=aLinkId;
END//

-- ----------------------------------------------------------------------------
-- deleteLink
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Delete the given link.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS deleteLink//
CREATE PROCEDURE deleteLink(IN aLinkId INT UNSIGNED)
BEGIN
  DELETE
    FROM `link`
    WHERE `id`=aLinkId;
END//

-- ----------------------------------------------------------------------------
-- incrementLinkViews
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Increments the number of views this link has gotten.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS incrementLinkViews//
CREATE PROCEDURE incrementLinkViews(IN aLinkId INT UNSIGNED)
BEGIN
  UPDATE `link`
    SET `views`=`views`+1
    WHERE `id`=aLinkId;
END//

-- ----------------------------------------------------------------------------
-- promoteLink
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Allow a user to promote a link.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS promoteLink//
CREATE PROCEDURE promoteLink(IN aLinkId INT UNSIGNED,
                             IN aUserId INT UNSIGNED,
                             IN aCreatedOn BIGINT SIGNED)
BEGIN
  INSERT IGNORE INTO `link_promotion`
    SET `link_id`=aLinkId,
        `user_id`=aUserId,
        `created_on`=aCreatedOn;

  UPDATE `link`
    SET `promotions`=`promotions`+1;
END//

-- ----------------------------------------------------------------------------
-- unPromoteLink
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Remove a user's link promotion.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS unPromoteLink//
CREATE PROCEDURE unPromoteLink(IN aLinkId INT UNSIGNED,
                               IN aUserId INT UNSIGNED)
BEGIN
  DELETE FROM `link_promotion`
    WHERE `link_id`=aLinkId AND `user_id`=aUserId;

  UPDATE `link`
    SET `promotions`=`promotions`-1;
END//

-- ----------------------------------------------------------------------------
-- demoteLink
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Allow a user to demote a link, with a reason.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS demoteLink//
CREATE PROCEDURE demoteLink(IN aLinkId INT UNSIGNED,
                            IN aUserId INT UNSIGNED,
                            IN aReason INT UNSIGNED,
                            IN aCreatedOn BIGINT SIGNED)
BEGIN
  INSERT INTO `link_demotion`
    SET `link_id`=aLinkId,
        `user_id`=aUserId,
        `reason`=aReason,
        `created_on`=aCreatedOn
    ON DUPLICATE KEY
      UPDATE `reason`=aReason,
             `created_on`=aCreatedOn;

  UPDATE `link`
    SET `demotions`=`demotions`+1;
END//

-- ----------------------------------------------------------------------------
-- unDemoteLink
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Remove a user's link demotion.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS unDemoteLink//
CREATE PROCEDURE unDemoteLink(IN aLinkId INT UNSIGNED,
                              IN aUserId INT UNSIGNED)
BEGIN
  DELETE FROM `link_demotion`
    WHERE `link_id`=aLinkId AND `user_id`=aUserId;

  UPDATE `link`
    SET `demotions`=`demotions`-1;
END//

-- ----------------------------------------------------------------------------
-- favoriteLink
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created July 17, 2009
-- \brief Allow a user to favorite a link.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS favoriteLink//
CREATE PROCEDURE favoriteLink(IN aLinkId INT UNSIGNED,
                              IN aUserId INT UNSIGNED,
                              IN aCreatedOn BIGINT SIGNED)
BEGIN
  INSERT IGNORE INTO `link_favorite`
    SET `link_id`=aLinkId,
        `user_id`=aUserId,
        `created_on`=aCreatedOn;

  UPDATE `link`
    SET `favorites`=`favorites`+1;
END//

-- ----------------------------------------------------------------------------
-- unFavoriteLink
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created July 17, 2009
-- \brief Remove a user's favorite.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS unFavoriteLink//
CREATE PROCEDURE unFavoriteLink(IN aLinkId INT UNSIGNED,
                                IN aUserId INT UNSIGNED)
BEGIN
  DELETE FROM `link_favorite`
    WHERE `link_id`=aLinkId AND `user_id`=aUserId;

  UPDATE `link`
    SET `favorites`=`favorites`-1;
END//

-- ----------------------------------------------------------------------------
-- tagLink
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Adds a tag to a link.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS tagLink//
CREATE PROCEDURE tagLink(IN aLinkId INT UNSIGNED,
                         IN aTagId INT UNSIGNED,
                         IN aTagPosition INT UNSIGNED)
BEGIN
  -- We want this to croak if the tag has already been added.
  INSERT INTO `link_to_tag`
    SET `tag_id`=aTagId,
        `link_id`=aLinkId,
        `position`=aTagPosition;

  INSERT INTO `link_tag_count`
    SET `tag_id`=aTagId,
        `count`=1
    ON DUPLICATE KEY
      UPDATE`count`=`count`+1;
END//

-- ----------------------------------------------------------------------------
-- unTagAllLink
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 18, 2009
-- \brief Removes all tags from a link.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS unTagAllLink//
CREATE PROCEDURE unTagAllLink(IN aLinkId INT UNSIGNED)
BEGIN
  -- Update counts before we remove the ties/ associations.
  UPDATE `link_tag_count`
    SET `count`=`count`-1
    WHERE `tag_id` IN (SELECT `tag_id`
                         FROM `link_to_tag`
                         WHERE `link_id`=aLinkId);

  -- Remove the tags.
  DELETE FROM `link_to_tag`
    WHERE `link_id`=aLinkId;
END//

-- ----------------------------------------------------------------------------
-- pageLinksByFormatAndTags
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created July 11, 2009
-- \brief Get a page of links of a given format and tagged with everything in
-- the list.
-- \param aFormat a format id (zero means no format).
-- \param aTags a comma-delimited list of tag ids.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS pageLinksByFormatAndTags//
CREATE PROCEDURE pageLinksByFormatAndTags(IN aFormat INT UNSIGNED,
                                          IN aTags TEXT CHARACTER SET utf8,
                                          IN aOffset INT UNSIGNED,
                                          IN aCount INT UNSIGNED,
                                          IN aOrder INT UNSIGNED,
                                          OUT aTotalCount INT UNSIGNED)
BEGIN
  DECLARE len INT DEFAULT 0;
  DECLARE lft INT DEFAULT 0;
  DECLARE rgt INT DEFAULT 0;

  SET len = LENGTH(aTags);

  SET @qry = "
    SELECT SQL_CALC_FOUND_ROWS `link`.*, `user`.*,
           GROUP_CONCAT(`tag` ORDER BY `link_to_tag`.`position`) AS link_tags
      FROM `link`
        INNER JOIN `user` ON `user`.`id`=`link`.`user_id`
        INNER JOIN `link_to_tag` ON `link_to_tag`.`link_id`=`link`.`id`
        INNER JOIN `tag` ON `tag`.`id`=`link_to_tag`.`tag_id`";

  SET @qry = CONCAT(@qry, getLinkTagsInnerJoinString(aTags));

  IF aFormat <> 0 THEN
    SET @qry = CONCAT(@qry, "
      WHERE `link`.`format_id` = ");
    SET @qry = CONCAT(@qry, aFormat);
  END IF;

  SET @qry = CONCAT(@qry, "
      GROUP BY `link`.`id`
      ORDER BY");

  SET @qry = CONCAT(@qry, getLinkOrderString(aOrder));

  SET @qry = CONCAT(@qry, "
      LIMIT ?, ?");

  SET @offset = aOffset;
  SET @count = aCount;

  PREPARE STMT FROM @qry;
  EXECUTE STMT USING @offset, @count;

  SELECT FOUND_ROWS() INTO aTotalCount;
END//

-- ----------------------------------------------------------------------------
-- pageLinksByFormatAndTagsWithUserRelation
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created July 19, 2009
-- \brief Get a page of links of a given format and tagged with everything in
-- the list, related to a specific user.
-- \param aFormat a format id (zero means no format).
-- \param aTags a comma-delimited list of tag ids.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS pageLinksByFormatAndTagsWithUserRelation//
CREATE PROCEDURE pageLinksByFormatAndTagsWithUserRelation(IN aFormat INT UNSIGNED,
                                                          IN aTags TEXT CHARACTER SET utf8,
                                                          IN aUserId INT UNSIGNED,
                                                          IN aRelation INT UNSIGNED,
                                                          IN aOffset INT UNSIGNED,
                                                          IN aCount INT UNSIGNED,
                                                          IN aOrder INT UNSIGNED,
                                                          OUT aTotalCount INT UNSIGNED)
BEGIN
  SET @qry = "
    SELECT SQL_CALC_FOUND_ROWS `link`.*, `user`.*,
           GROUP_CONCAT(`tag` ORDER BY `link_to_tag`.`position`) AS link_tags
      FROM `link`
        INNER JOIN `user` ON `user`.`id`=`link`.`user_id`
        INNER JOIN `link_to_tag` ON `link_to_tag`.`link_id`=`link`.`id`
        INNER JOIN `tag` ON `tag`.`id`=`link_to_tag`.`tag_id`";

  IF aRelation = 1 THEN
    SET @qry = CONCAT(@qry, "
        INNER JOIN `link_favorite` ON `link_favorite`.`link_id`=`link`.`id`
                                  AND `link_favorite`.`user_id`=");
    SET @qry = CONCAT(@qry, aUserId);
  ELSEIF aRelation = 2 THEN
    SET @qry = CONCAT(@qry, "
        INNER JOIN `link_promotion` ON `link_promotion`.`link_id`=`link`.`id`
                                   AND `link_promotion`.`user_id`=");
    SET @qry = CONCAT(@qry, aUserId);
  END IF;

  SET @qry = CONCAT(@qry, getLinkTagsInnerJoinString(aTags));

  -- Cheap trick so we can guarantee 'WHERE' is included in the query.
  SET @qry = CONCAT(@qry, "
      WHERE 1=1");

  IF aRelation = 0 THEN
    SET @qry = CONCAT(@qry, "
        AND `link`.`user_id` = ");
    SET @qry = CONCAT(@qry, aUserId);
  END IF;

  -- A format of zero means no specific format.
  IF aFormat <> 0 THEN
    SET @qry = CONCAT(@qry, "
        AND `link`.`format_id` = ");
    SET @qry = CONCAT(@qry, aFormat);
  END IF;

  SET @qry = CONCAT(@qry, "
      GROUP BY `link`.`id`
      ORDER BY");

  SET @qry = CONCAT(@qry, getLinkOrderString(aOrder));

  SET @qry = CONCAT(@qry, "
      LIMIT ?, ?");

  SET @offset = aOffset;
  SET @count = aCount;

  PREPARE STMT FROM @qry;
  EXECUTE STMT USING @offset, @count;

  SELECT FOUND_ROWS() INTO aTotalCount;
END//

-- ----------------------------------------------------------------------------
-- getLinkTagsInnerJoinString
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created July 18, 2009
-- \brief Returns an inner join for each tag in the list of comma-delimited
-- tag ids given.
-- ----------------------------------------------------------------------------
DROP FUNCTION IF EXISTS getLinkTagsInnerJoinString//
CREATE FUNCTION getLinkTagsInnerJoinString(aTags TEXT CHARACTER SET utf8)
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
    SET qry = CONCAT(qry, ".link_id = link.id");
    SET qry = CONCAT(qry, " AND ");
    SET qry = CONCAT(qry, CONCAT("l2t", rgt));
    SET qry = CONCAT(qry, ".tag_id = ");
    SET qry = CONCAT(qry, SUBSTRING(aTags, lft, rgt - lft));
  END LOOP tags;

  RETURN qry;
END//

-- ----------------------------------------------------------------------------
-- getLinkOrderString
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created June 12, 2009
-- \brief Determines the order by expression corresponding to the given
-- order code.
-- ----------------------------------------------------------------------------
DROP FUNCTION IF EXISTS getLinkOrderString//
CREATE FUNCTION getLinkOrderString(aOrder INT)
RETURNS TEXT DETERMINISTIC
BEGIN
  IF (aOrder = 1) THEN
    -- oldest first
    RETURN " link.id ASC";
  ELSE
    -- newest first
    RETURN " link.id DESC";
  END IF;
END//