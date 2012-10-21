delimiter //
-- ----------------------------------------------------------------------------
-- category
-- ----------------------------------------------------------------------------
-- This Model works differently from the others since it is mostly read-only.
-- Because changes should be infrequent, require careful consideration, and be
-- performed by an administrator, I do not want to bother writing a program
-- just for this.
-- ----------------------------------------------------------------------------


-- ----------------------------------------------------------------------------
-- crcFormatTable
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created May 10, 2009
-- \brief Calculates a 32-bit CRC of the entire format table. This would
-- generally be expensive, but we expect this table to change rarely so this
-- query will most often be cached. This needed to be done since no checksum
-- or Update_time are available for InnoDb tables.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS crcFormatTable//
CREATE PROCEDURE crcFormatTable(OUT aCrc INT UNSIGNED)
BEGIN
  SELECT BIT_XOR(CRC32(CONCAT_WS(',',`id`,`format`,`folder`,`place`)))
    INTO aCrc
    FROM `format`
    GROUP BY 1=1
    ORDER BY `id` ASC;
END//

-- ----------------------------------------------------------------------------
-- readEveryFormat
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created May 10, 2009
-- \brief Read EVERY Format in a specific order.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS readEveryFormat//
CREATE PROCEDURE readEveryFormat()
BEGIN
  SELECT *
    FROM `format`
    ORDER BY `place` ASC;
END//