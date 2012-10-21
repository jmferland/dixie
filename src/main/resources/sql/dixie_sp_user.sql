delimiter //
-- ----------------------------------------------------------------------------
-- createUser
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 10, 2009
-- \brief Create a user and return its id.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS createUser//
CREATE PROCEDURE createUser(IN aUsername TEXT CHARACTER SET utf8,
                            IN aPasswordHash TEXT,
                            IN aEmail TEXT,
                            IN aBirthDate BIGINT SIGNED,
                            IN aFirstName TEXT CHARACTER SET utf8,
                            IN aLastName TEXT CHARACTER SET utf8,
                            IN accountStatus INT UNSIGNED,
                            IN aCreatedOn BIGINT SIGNED,
                            OUT aUserId INT UNSIGNED)
BEGIN
  INSERT INTO `user`
    SET `username`=aUsername,
        `password_hash`=aPasswordHash,
        `email`=aEmail,
        `birth_date`=aBirthDate,
        `first_name`=aFirstName,
        `last_name`=aLastName,
        `account_status`=accountStatus,
        `created_on`=aCreatedOn;
  SELECT LAST_INSERT_ID() INTO aUserId;
END//

-- ----------------------------------------------------------------------------
-- readUser
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 10, 2009
-- \brief Get/read the requested user by id.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS readUser//
CREATE PROCEDURE readUser(IN aUserId INT UNSIGNED)
BEGIN
  SELECT *
    FROM `user`
    WHERE `id`=aUserId;
END//

-- ----------------------------------------------------------------------------
-- readUserByUsername
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 13, 2009
-- \brief Get/read the requested user by username.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS readUserByUsername//
CREATE PROCEDURE readUserByUsername(IN aUsername TEXT CHARACTER SET utf8)
BEGIN
  SELECT *
    FROM `user`
    WHERE `username`=aUsername;
END//

-- ----------------------------------------------------------------------------
-- readUserByEmail
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created May 17, 2009
-- \brief Get/read the requested user by email.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS readUserByEmail//
CREATE PROCEDURE readUserByEmail(IN aEmail TEXT)
BEGIN
  SELECT *
    FROM `user`
    WHERE `email`=aEmail;
END//

-- ----------------------------------------------------------------------------
-- updateUser
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 10, 2009
-- \brief Update a user.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS updateUser//
CREATE PROCEDURE updateUser(IN aUserId INT UNSIGNED,
                            IN aUsername TEXT CHARACTER SET utf8,
                            IN aPasswordHash TEXT,
                            IN aEmail TEXT,
                            IN aBirthDate BIGINT SIGNED,
                            IN aFirstName TEXT CHARACTER SET utf8,
                            IN aLastName TEXT CHARACTER SET utf8,
                            IN accountStatus INT UNSIGNED)
BEGIN
  UPDATE `user`
    SET `username`=aUsername,
        `password_hash`=aPasswordHash,
        `email`=aEmail,
        `birth_date`=aBirthDate,
        `first_name`=aFirstName,
        `last_name`=aLastName,
        `account_status`=accountStatus
    WHERE `id`=aUserId;
END//

-- ----------------------------------------------------------------------------
-- deleteUser
-- ----------------------------------------------------------------------------
-- \author Jonathan Ferland
-- \created April 10, 2009
-- \brief Delete the given user.
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS deleteUser//
CREATE PROCEDURE deleteUser(IN aUserId INT UNSIGNED)
BEGIN
  DELETE
    FROM `user`
    WHERE `id`=aUserId;
END//