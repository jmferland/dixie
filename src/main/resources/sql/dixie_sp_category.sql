delimiter //
-- ----------------------------------------------------------------------------
-- category
-- ----------------------------------------------------------------------------
-- This Model works differently from the others since it is mostly read-only.
-- Because changes should be infrequent, require careful consideration, and be
-- performed by an administrator, I do not want to bother writing a program
-- just for this.
--
-- It would be smart to "LOCK `category` WRITE;" before and
-- "UNLOCK TABLES;" after making changes to the `category` table.
--
-- Create & Update: Only the `category` and `parent_id` columns need to be
-- specified. Afterwards, call buildCategoryTree to properly assign `lft` and
-- `rgt` values.
--
-- Delete: Be sure to call buildCategoryTree afterwards.
--
-- Check for orphaned nodes/ trees with:
-- "SELECT * FROM `category` WHERE lft=0;
--
-- Remember that the `link` table (and others?) may reference this table and
-- expect references to leaf nodes. So, be especially careful when deleting a
-- category and also of updating or changing the hierarchy.
-- ----------------------------------------------------------------------------


-- ----------------------------------------------------------------------------
-- crcCategoryTable
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 29, 2009
-- \brief Calculates a 32-bit CRC of the entire category table. This would
-- generally be expensive, but we expect this table to change rarely so this
-- query will most often be cached. This needed to be done since no checksum
-- or Update_time are available for InnoDb tables.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS crcCategoryTable//
CREATE PROCEDURE crcCategoryTable(OUT aCrc INT UNSIGNED)
BEGIN
  SELECT BIT_XOR(CRC32(CONCAT_WS(',',`id`,`parent_id`,`category`,`place`,`lft`,`rgt`)))
    INTO aCrc
    FROM `category`
    WHERE `lft` > 0 AND `rgt` > 0
    GROUP BY 1=1
    ORDER BY `id` ASC;
END//

-- ----------------------------------------------------------------------------
-- readEveryCategoryDepthFirst
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 25, 2009
-- \brief Read EVERY Category in the same order a depth-first search would
-- traverse the tree.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS readEveryCategoryDepthFirst//
CREATE PROCEDURE readEveryCategoryDepthFirst()
BEGIN
  SELECT *
    FROM `category`
    WHERE lft > 0
    ORDER BY `lft` ASC;
END//

-- ----------------------------------------------------------------------------
-- buildCategoryTree
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 25, 2009
-- \brief Builds the category tree (assigns right and left values).
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS buildCategoryTree//
CREATE PROCEDURE buildCategoryTree()
BEGIN
  DECLARE garbage INT;

  -- This will make it easy to find orphaned nodes/ trees afterwards.
  UPDATE `category` SET `lft`=0, `rgt`=0;

  CALL buildCategoryTreeHelper(0, 0, garbage);

  -- Order the category table by lft so sorting by lft is fast, which is the
  -- order we want our result sets to be in the majority of the time.
  ALTER TABLE `category` ORDER BY lft ASC;
END//

-- ----------------------------------------------------------------------------
-- buildCategoryTreeHelper
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 25, 2009
-- \brief Recursive helper function for buildCategoryTree.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS buildCategoryTreeHelper//
CREATE PROCEDURE buildCategoryTreeHelper(IN aParentId INT UNSIGNED,
                                         IN aLft INT UNSIGNED,
                                         OUT aRgt INT UNSIGNED)
BEGIN
  DECLARE childRgt INT;
  DECLARE categoryId INT;
  DECLARE noMore INT DEFAULT 0;

  DECLARE crsr CURSOR FOR
    SELECT `id`
      FROM `category`
      WHERE `parent_id`=aParentId
      ORDER BY `place` ASC;

  DECLARE CONTINUE HANDLER FOR NOT FOUND
    SET noMore=1;

  SET aRgt = aLft + 1;

  OPEN crsr;

  -- Repeat this process for each of the children.
  children: LOOP
    FETCH crsr INTO categoryId;

    IF (noMore = 1) THEN
      LEAVE children;
    END IF;

    CALL buildCategoryTreeHelper(categoryId, aRgt, childRgt);
    SET aRgt = childRgt;
  END LOOP children;

  CLOSE crsr;

  UPDATE `category`
    SET `lft`=aLft,
        `rgt`=aRgt
    WHERE `id`=aParentId;

  SET aRgt = aRgt + 1;
END//

-- ----------------------------------------------------------------------------
-- showCategoryTree
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 27, 2009
-- \brief Displays the category as an indented tree for "easier" viewing. Code
-- from: http://dev.mysql.com/tech-resources/articles/hierarchical-data.html
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS showCategoryTree//
CREATE PROCEDURE showCategoryTree()
BEGIN
  SELECT CONCAT(REPEAT(' ',(COUNT(parent.category)-1)),node.category) AS name
    FROM category AS node,
         category AS parent
    WHERE node.lft BETWEEN parent.lft AND parent.rgt
          -- Added to prevent orphans from showing up
          AND node.lft <> 0
          AND node.rgt <> 0
    GROUP BY node.id
    ORDER BY node.lft;
END//
