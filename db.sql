SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'TRADITIONAL';

DROP SCHEMA IF EXISTS `core_db`;
CREATE SCHEMA IF NOT EXISTS `core_db`
  DEFAULT CHARACTER SET utf8
  COLLATE utf8_general_ci;
USE `core_db`;

-- -----------------------------------------------------
-- Table `core_db`.`cd_region_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `core_db`.`cd_region_type`;

CREATE TABLE IF NOT EXISTS `core_db`.`cd_region_type` (
  `record_id`   BIGINT      NOT NULL AUTO_INCREMENT,
  `record_key`  CHAR(36)    NOT NULL,
  `name`        VARCHAR(45) NOT NULL,
  `active_flag` TINYINT     NOT NULL DEFAULT 1,
  PRIMARY KEY (`record_id`),
  UNIQUE INDEX `uix_record_key` (`record_key` ASC)
)
  ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `core_db`.`cd_region`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `core_db`.`cd_region`;

CREATE TABLE IF NOT EXISTS `core_db`.`cd_region` (
  `record_id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `record_key`        CHAR(36)     NOT NULL,
  `region_type_rid`   BIGINT       NOT NULL,
  `parent_region_rid` BIGINT       NULL,
  `name`              VARCHAR(255) NOT NULL
  COMMENT 'ISO3166-1 Short name\nor\nISO3166-2 Subdivision name',
  `name_lc`           VARCHAR(255) NULL
  COMMENT 'ISO3166 Short name lower case',
  `name_full`         VARCHAR(255) NULL
  COMMENT 'ISO3166 Full name',
  `name_common`       VARCHAR(255) NULL
  COMMENT 'Based on ISO3166-1 Short name lower case or ISO3166-2 Subdivision name. Data modified to remove parenthesis, brackets and massage naming to better meet common country representations.',
  `code_alpha`        VARCHAR(10)  NULL
  COMMENT 'ISO 3166-1 alpha-2 code \nor\nISO 3166-2 code',
  `code_alpha_alt`    VARCHAR(10)  NULL
  COMMENT 'ISO 3166-1 alpha-3 code',
  `code_numeric`      VARCHAR(10)  NULL
  COMMENT 'ISO 3166-1 numeric code',
  `active_flag`       TINYINT      NOT NULL DEFAULT 1,
  PRIMARY KEY (`record_id`),
  INDEX `fk_region_type` (`region_type_rid` ASC),
  INDEX `fk_region_parent` (`parent_region_rid` ASC),
  UNIQUE INDEX `uix_record_key` (`record_key` ASC),
  INDEX `ix_active_flag` (`active_flag` ASC),
  CONSTRAINT `fk_region_type`
  FOREIGN KEY (`region_type_rid`)
  REFERENCES `core_db`.`cd_region_type` (`record_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_parent`
  FOREIGN KEY (`parent_region_rid`)
  REFERENCES `core_db`.`cd_region` (`record_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `core_db`.`cd_region_path`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `core_db`.`cd_region_path`;

CREATE TABLE IF NOT EXISTS `core_db`.`cd_region_path` (
  `region_rid` BIGINT NOT NULL,
  `node_rid`   BIGINT NOT NULL,
  `node_level` BIGINT NOT NULL,
  PRIMARY KEY (`region_rid`, `node_level`, `node_rid`),
  INDEX `fk_region_branch` (`node_rid` ASC),
  CONSTRAINT `fk_region_leaf`
  FOREIGN KEY (`region_rid`)
  REFERENCES `core_db`.`cd_region` (`record_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_region_branch`
  FOREIGN KEY (`node_rid`)
  REFERENCES `core_db`.`cd_region` (`record_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE = InnoDB;

-- -----------------------------------------------------
-- Placeholder table for view `core_db`.`cd_region_path_v`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `core_db`.`cd_region_path_v` (
  `record_id`            INT,
  `record_key`           INT,
  `name_common`          INT,
  `type_record_id`       INT,
  `type_record_key`      INT,
  `type_name`            INT,
  `node_record_id`       INT,
  `node_record_key`      INT,
  `node_name`            INT,
  `node_type_record_id`  INT,
  `node_type_record_key` INT,
  `node_type_name`       INT,
  `node_level`           INT
);

-- -----------------------------------------------------
-- Placeholder table for view `core_db`.`cd_region_region_type_v`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `core_db`.`cd_region_region_type_v` (
  `record_id`        INT,
  `record_key`       INT,
  `name_common`      INT,
  `region_type_rid`  INT,
  `region_type_key`  INT,
  `region_type_name` INT
);

-- -----------------------------------------------------
-- View `core_db`.`cd_region_path_v`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `core_db`.`cd_region_path_v`;
DROP TABLE IF EXISTS `core_db`.`cd_region_path_v`;
USE `core_db`;
CREATE OR REPLACE VIEW `core_db`.`cd_region_path_v` AS
  SELECT
    leaf.record_id,
    leaf.record_key,
    leaf.name_common,
    leaf_type.record_id    AS type_record_id,
    leaf_type.record_key   AS type_record_key,
    leaf_type.name         AS type_name,
    branch.record_id       AS node_record_id,
    branch.record_key      AS node_record_key,
    branch.name_common     AS node_name,
    branch_type.record_id  AS node_type_record_id,
    branch_type.record_key AS node_type_record_key,
    branch_type.name       AS node_type_name,
    path.node_level
  FROM
    cd_region_path AS path INNER JOIN
    cd_region AS leaf ON path.region_rid = leaf.record_id
    INNER JOIN
    cd_region_type AS leaf_type ON leaf.region_type_rid = leaf_type.record_id
    INNER JOIN
    cd_region AS branch ON path.node_rid = branch.record_id
    INNER JOIN
    cd_region_type AS branch_type ON branch.region_type_rid = branch_type.record_id;

-- -----------------------------------------------------
-- View `core_db`.`cd_region_region_type_v`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `core_db`.`cd_region_region_type_v`;
DROP TABLE IF EXISTS `core_db`.`cd_region_region_type_v`;
USE `core_db`;
CREATE OR REPLACE VIEW `core_db`.`cd_region_region_type_v` AS
  SELECT
    region.record_id,
    region.record_key,
    region.name_common,
    region_type.record_id  AS region_type_rid,
    region_type.record_key AS region_type_key,
    region_type.name       AS region_type_name
  FROM
    cd_region region
    INNER JOIN cd_region_type region_type ON region.region_type_rid = region_type.record_id;

SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
