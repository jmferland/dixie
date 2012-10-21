delimiter //
-- ----------------------------------------------------------------------------
-- createCaptcha
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created May 23, 2009
-- \brief Create a captcha.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS createCaptcha//
CREATE PROCEDURE createCaptcha(IN a_nMostSigUUIDBits BIGINT SIGNED,
                               IN a_nLeastSigUUIDBits BIGINT SIGNED,
                               IN a_sAnswer TEXT CHARACTER SET utf8,
                               IN a_nSeed BIGINT SIGNED,
                               IN a_nCreatedOn BIGINT SIGNED)
BEGIN
  INSERT INTO `captcha`
    SET `uuid_most_sig`=a_nMostSigUUIDBits,
        `uuid_least_sig`=a_nLeastSigUUIDBits,
        `answer`=a_sAnswer,
        `seed`=a_nSeed,
        `created_on`=a_nCreatedOn;
END//

-- ----------------------------------------------------------------------------
-- readCaptchaByUUID
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created May 23, 2009
-- \brief Get/read the requested captcha by UUID.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS readCaptchaByUUID//
CREATE PROCEDURE readCaptchaByUUID(IN a_nMostSigUUIDBits BIGINT SIGNED,
                                   IN a_nLeastSigUUIDBits BIGINT SIGNED)
BEGIN
  SELECT *
    FROM `captcha`
    WHERE `uuid_most_sig`=a_nMostSigUUIDBits AND
          `uuid_least_sig`=a_nLeastSigUUIDBits;
END//

-- ----------------------------------------------------------------------------
-- deleteCaptcha
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created May 23, 2009
-- \brief Delete the given captcha by UUID.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS deleteCaptchaByUUID//
CREATE PROCEDURE deleteCaptchaByUUID(IN a_nMostSigUUIDBits BIGINT SIGNED,
                                     IN a_nLeastSigUUIDBits BIGINT SIGNED)
BEGIN
  DELETE FROM `captcha`
    WHERE `uuid_most_sig`=a_nMostSigUUIDBits AND
          `uuid_least_sig`=a_nLeastSigUUIDBits;
END//

-- ----------------------------------------------------------------------------
-- deleteCaptchaByCreatedBefore
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created May 24, 2009
-- \brief Delete the given captcha by UUID.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS deleteCaptchaByCreatedBefore//
CREATE PROCEDURE deleteCaptchaByCreatedBefore(IN a_nTime BIGINT SIGNED)
BEGIN
  DELETE FROM `captcha`
    WHERE `created_on` < a_nTime;
END//