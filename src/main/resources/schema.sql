##############################################################
##                                                          ##
## Author: Hardy                                            ##
## Date: 07/14/2026                                         ##
## Version: 1.0                                             ##
##                                                          ##
##############################################################
/*
    --- General Rules ---
    * use underscored_names instead of camelCase
    * Table Names should be plural
    * spell out id fields [item_id instead of id]
    * don't use embiguous column names
    * name foreign key columns the same as the columns they refer to
    * use Caps for all SQL queries
 */

CREATE SCHEMA IF NOT EXISTS fawatir;

SET NAMES 'UTF8MB4';

SET TIME_ZONE = '+1:00';

USE fawatir;

-- =========================================================================
-- STEP 1: Drop all CHILD tables first (tables that hold foreign keys)
-- =========================================================================
DROP TABLE IF EXISTS TwoFactorVerification;
DROP TABLE IF EXISTS ResetPasswordVerification;
DROP TABLE IF EXISTS AccountVerification;
DROP TABLE IF EXISTS UserEvents;
DROP TABLE IF EXISTS UserRoles;

-- =========================================================================
-- STEP 2: Drop all PARENT tables (tables referenced by foreign keys)
-- =========================================================================
DROP TABLE IF EXISTS Events;
DROP TABLE IF EXISTS Roles;
DROP TABLE IF EXISTS Users;


-- =========================================================================
-- STEP 3: Create tables (order here doesn't matter as much, but parents first is best)
-- =========================================================================

CREATE TABLE Users
(
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name   VARCHAR(50)     NOT NULL,
    last_name    VARCHAR(50)     NOT NULL,
    email        VARCHAR(100)    NOT NULL,
    password     VARCHAR(255) DEFAULT NULL,
    address       VARCHAR(255) DEFAULT NULL,
    phone        VARCHAR(30)  DEFAULT NULL,
    title        VARCHAR(50)  DEFAULT NULL,
    bio          VARCHAR(255) DEFAULT NULL,
    enabled      BOOLEAN      DEFAULT FALSE,
    non_locked   BOOLEAN      DEFAULT FALSE,
    using_mfa    BOOLEAN      DEFAULT FALSE,
    created_date DATETIME     DEFAULT CURRENT_TIMESTAMP,
    image_url    VARCHAR(255) DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    CONSTRAINT UQ_Users_Email UNIQUE (email)
);

CREATE TABLE Roles
(
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50)     NOT NULL,
    permission VARCHAR(255)    NOT NULL,
    CONSTRAINT UQ_Roles_Name UNIQUE (name)
);
INSERT INTO Roles (name, permission)
VALUES ('ROLE_USER', 'READ:USER,READ:CUSTOMER'),
       ('ROLE_MANAGER', 'READ:USER,READ:CUSTOMER,UPDATE:USER,UPDATE:CUSTOMER'),
       ('ROLE_ADMIN', 'READ:USER,READ:CUSTOMER,CREATE:USER,CREATE:CUSTOMER,UPDATE:USER,UPDATE:CUSTOMER'),
       ('ROLE_SYSADMIN', 'READ:USER,READ:CUSTOMER,CREATE:USER,CREATE:CUSTOMER,UPDATE:USER,UPDATE:CUSTOMER,DELETE:USER,DELETE:CUSTOMER');

CREATE TABLE UserRoles
(
    id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    role_id BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (role_id) REFERENCES Roles (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT UQ_UserRoles_User_id UNIQUE (user_id)
);

CREATE TABLE Events
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type        VARCHAR(50)     NOT NULL CHECK (type IN
                                                ('LOGIN_ATTEMPT', 'LOGIN_ATTEMPT_FAILURE', 'LOGIN_ATTEMPT_SUCCESS',
                                                 'PROFILE_UPDATE', 'PROFILE_PICTURE_UPDATE', 'ROLE_UPDATE',
                                                 'ACCOUNT_SETTINGS_UPDATE', 'PASSWORD_UPDATE', 'MFA_UPDATE')),
    description VARCHAR(255)    NOT NULL,
    CONSTRAINT UQ_Events_Type UNIQUE (type)
);

CREATE TABLE UserEvents
(
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT UNSIGNED NOT NULL,
    event_id   BIGINT UNSIGNED NOT NULL,
    device     VARCHAR(100)    NOT NULL,
    ip_address VARCHAR(100)    NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (event_id) REFERENCES Events (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE AccountVerification
(
    id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    url     VARCHAR(255)    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT UQ_AccountVerification_User_Id UNIQUE (user_id),
    CONSTRAINT UQ_AccountVerification_Url UNIQUE (url)
);

CREATE TABLE ResetPasswordVerification
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED NOT NULL,
    url             VARCHAR(255)    NOT NULL,
    expiration_date DATETIME        NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT UQ_ResetPasswordVerification_User_Id UNIQUE (user_id),
    CONSTRAINT UQ_ResetPasswordVerification_Url UNIQUE (url)
);

CREATE TABLE TwoFactorVerification
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED NOT NULL,
    code            VARCHAR(10)     NOT NULL,
    expiration_date DATETIME        NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT UQ_TwoFactorVerification_User_Id UNIQUE (user_id),
    CONSTRAINT UQ_TwoFactorVerification_Code UNIQUE (code)
);