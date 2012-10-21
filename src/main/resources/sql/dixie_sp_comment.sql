delimiter //
-- ----------------------------------------------------------------------------
-- createComment
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created March 4, 2009
-- \brief Create a comment and return its id.  Also return the top id
-- since it cannot be known if this is a top-level comment.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS createComment//
CREATE PROCEDURE createComment(IN aUserId INT UNSIGNED,
                               IN aLinkId INT UNSIGNED,
                               IN aParentId INT UNSIGNED,
                               INOUT aTopId INT UNSIGNED,
                               IN aText TEXT CHARACTER SET utf8,
                               IN aCreatedOn BIGINT SIGNED,
                               OUT aCommentId INT UNSIGNED)
BEGIN
  INSERT INTO comment (user_id,link_id,parent_id,top_id,text,created_on)
    VALUES (aUserId,aLinkId,aParentId,aTopId,aText,aCreatedOn);
  SELECT LAST_INSERT_ID() INTO aCommentId;

  -- Do this here instead of a trigger so we can return the aTopId we actually
  -- insert.
  IF (aParentId = 0) THEN
    UPDATE comment
      SET top_id=aCommentId
      WHERE id=aCommentId;
    SELECT aCommentId INTO aTopId;
  END IF;

  -- Actually, I'd rather just avoid triggers altogether. I can only see a
  -- benefit for INSERT .. VALUES (), (), ... queries and for ON UPDATE and
  -- DELETE CASCADE relationships. For the former, this will probably never
  -- happen with stored procedures. For the latter, I read somewhere that these
  -- relationships slow, so I avoid them.
  UPDATE `link`
    SET `comments`=`comments`+1
    WHERE `id`=aLinkId;
END//

-- ----------------------------------------------------------------------------
-- deleteComment
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created March 4, 2009
-- \brief Delete the given comment.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS deleteComment//
CREATE PROCEDURE deleteComment(IN aCommentId INT UNSIGNED)
BEGIN
  DELETE
    FROM comment
    WHERE id=aCommentId;

  UPDATE `link`
    SET `comments`=`comments`-1
    WHERE `id`=aLinkId;
END//

-- ----------------------------------------------------------------------------
-- readComment
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created March 8, 2009
-- \brief Get/read the requested comment by id.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS readComment//
CREATE PROCEDURE readComment(IN aCommentId INT UNSIGNED)
BEGIN
  SELECT *
    FROM `comment`
    INNER JOIN `user` ON `user`.`id`=`user_id`
    WHERE `comment`.`id`=aCommentId;
END//

-- ----------------------------------------------------------------------------
-- rateComment
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created March 4, 2009
-- \brief Rate a comment and write out the net result of the given user's
-- rating on the given comment.  For example, if they had previously rated the
-- comment -1 and they are now rating it +1 the net result is +2.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS rateComment//
CREATE PROCEDURE rateComment(IN aCommentId INT UNSIGNED,
                             IN aUserId INT UNSIGNED,
                             IN aNewRating INT UNSIGNED,
                             OUT aNetRating INT UNSIGNED,
                             IN aCreatedOn BIGINT SIGNED)
BEGIN
  -- Simultaneous calls (by the same user) could result in false
  -- aNetRating values; however, this is of little consequence.
  SELECT aNewRating INTO aNetRating;

  SELECT aNewRating-rating INTO aNetRating
    FROM comment_rating
    WHERE comment_id=aCommentId
      AND user_id=aUserId
    LIMIT 1;

  INSERT INTO comment_rating (comment_id, user_id, rating, created_on)
    VALUES (aCommentId, aUserId, aNewRating, aCreatedOn)
    ON DUPLICATE KEY UPDATE rating=aNewRating, created_on=aCreatedOn;
END//

-- ----------------------------------------------------------------------------
-- pageComments
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created March 8, 2009
-- \brief Paging will be done on root-level comments only; however, the result
-- set will include all the children (replies) of the root-level comments. This
-- will avoid orphaning entirely and allow for the most consistent and correct
-- paging possible.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS pageComments//
CREATE PROCEDURE pageComments(IN aLinkId INT UNSIGNED,
                              IN aOffset INT UNSIGNED,
                              IN aCount INT UNSIGNED,
                              IN aOrder INT UNSIGNED,
                              OUT aTotalCount INT UNSIGNED)
BEGIN
  SET @aLinkId = aLinkId;
  SET @aOffset = aOffset;
  SET @aCount = aCount;

  -- TODO: I remember hear/reading somewhere that creating a temp table is
  -- real bad, so we should look into other ways of doing this (;

  -- Create a temporary table to store the comment ids in the requested page.
  DROP TABLE IF EXISTS page_t;
  CREATE TEMPORARY TABLE page_t (
    `id` INT unsigned NOT NULL auto_increment,
    `comment_id` INT unsigned NOT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE=MEMORY;

  -- \todo is a subquery any faster?
  -- Prepare a query to select the root-level comments of the requested page
  -- into the temporary table.
  SET @qry = "
    INSERT INTO page_t (comment_id)
      SELECT SQL_CALC_FOUND_ROWS comment.id
      FROM comment
        LEFT JOIN comment_rating ON comment_id=comment.id
      WHERE comment.link_id=?
        AND comment.parent_id=0
      GROUP BY comment.id
      ORDER BY ";

  SET @qry = CONCAT(@qry, getCommentOrderString(aOrder));
  SET @qry = CONCAT(@qry, " LIMIT ?,?");

  PREPARE STMT FROM @qry;
  EXECUTE STMT USING @aLinkId, @aOffset, @aCount;

  -- We want to know how many root-level comments there are so we can determine
  -- the number of pages.
  SELECT FOUND_ROWS() INTO aTotalCount;

  -- Select and order all the comments in the page (includes the children).
  SET @qry = "
    SELECT comment.*, user.*,
      SUM(IF(comment_rating.rating>0,comment_rating.rating,0)) AS comment_ups,
      SUM(IF(comment_rating.rating<0,comment_rating.rating,0)) AS comment_downs
      FROM comment
        INNER JOIN page_t ON page_t.comment_id=top_id
        INNER JOIN user ON user.id=user_id
        LEFT JOIN comment_rating ON comment_rating.comment_id=comment.id
      GROUP BY comment.id
      ORDER BY page_t.id ASC, ";
  SET @qry = CONCAT(@qry, getCommentOrderString(aOrder));

  PREPARE STMT FROM @qry;
  EXECUTE STMT;

  DROP TABLE page_t;
END//

-- ----------------------------------------------------------------------------
-- getCommentOffset
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created May 29, 2009
-- \brief Determines the offset of the top-level parent of the given comment.
-- Return -1 if there is no offset (ex: the comment does not exist).
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS getCommentOffset//
CREATE PROCEDURE getCommentOffset(IN aCommentId INT UNSIGNED,
                                  IN aOrder INT UNSIGNED,
                                  OUT aOffset BIGINT SIGNED)
BEGIN
  DECLARE linkId, topId INT DEFAULT NULL;

  SET @offset = -1;

  SELECT link_id, top_id
    INTO linkId, topId
    FROM comment
    WHERE id=aCommentId;

  IF (linkId IS NOT NULL AND topId IS NOT NULL) THEN
    SET @n = -1;
    SET @qry = "
      SELECT @n := @n + 1
      INTO @offset
      FROM comment
        LEFT JOIN comment_rating ON comment_id=comment.id
      WHERE comment.link_id=?
        AND comment.parent_id=0
      GROUP BY comment.id
      HAVING comment.id=?
      ORDER BY ";

    SET @qry = CONCAT(@qry, getCommentOrderString(aOrder));

    SET @linkId = linkId;
    SET @topId = topId;

    PREPARE STMT FROM @qry;
    EXECUTE STMT USING @linkId, @topId;
  END IF;

  SELECT @offset INTO aOffset;
END//

-- ----------------------------------------------------------------------------
-- pageCommentsByUser
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created July 19, 2009
-- \brief Get a simple list of all the comments from a specific user.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS pageCommentsByUser//
CREATE PROCEDURE pageCommentsByUser(IN aUserId INT UNSIGNED,
                                    IN aOffset INT UNSIGNED,
                                    IN aCount INT UNSIGNED,
                                    IN aOrder INT UNSIGNED,
                                    OUT aTotalCount INT UNSIGNED)
BEGIN
  SET @qry = "
    SELECT comment.*, user.*,
      SUM(IF(comment_rating.rating>0,comment_rating.rating,0)) AS comment_ups,
      SUM(IF(comment_rating.rating<0,comment_rating.rating,0)) AS comment_downs
      FROM comment
        INNER JOIN user ON user.id=user_id
        LEFT JOIN comment_rating ON comment_rating.comment_id=comment.id
      WHERE comment.user_id=?
      GROUP BY comment.id
      ORDER BY ";

  SET @qry = CONCAT(@qry, getCommentOrderString(aOrder));
  SET @qry = CONCAT(@qry, "
      LIMIT ?,?");

  SET @aUserId = aUserId;
  SET @aOffset = aOffset;
  SET @aCount = aCount;

  PREPARE STMT FROM @qry;
  EXECUTE STMT USING @aUserId, @aOffset, @aCount;

  SELECT FOUND_ROWS() INTO aTotalCount;
END//

-- ----------------------------------------------------------------------------
-- getCommentOrderString
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created May 29, 2009
-- \brief Determines the order by expression corresponding to the given
-- order code.
-- ----------------------------------------------------------------------------
DROP FUNCTION IF EXISTS getCommentOrderString//
CREATE FUNCTION getCommentOrderString(aOrder INT)
RETURNS TEXT DETERMINISTIC
BEGIN
  IF (aOrder = 5) THEN
    -- lowest rated first
    RETURN " SUM(comment_rating.rating) ASC";
  ELSEIF (aOrder = 4) THEN
    -- highest rated first
    RETURN " SUM(comment_rating.rating) DESC";
  ELSEIF (aOrder = 3) THEN
    -- most agreeable (least controversial) first
    RETURN " ABS(SUM(comment_rating.rating)) DESC, COUNT(*) ASC";
  ELSEIF (aOrder = 2) THEN
    -- most controversial first
    RETURN " ABS(SUM(comment_rating.rating)) ASC, COUNT(*) DESC";
  ELSEIF (aOrder = 1) THEN
    -- newest first
    RETURN " comment.id DESC";
  ELSE
    -- oldest first
    RETURN " comment.id ASC";
  END IF;
END//
