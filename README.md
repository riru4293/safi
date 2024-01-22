# Simple And Flexible Idm

## Setup database (MariaDB)

```SQL
DROP   USER     IF EXISTS 'safi'@'localhost';
DROP   USER     IF EXISTS 'safi-batch'@'localhost';
DROP   DATABASE IF EXISTS `safi`;
CREATE DATABASE           `safi` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'Simple And Flexible Idm';
CREATE USER               'safi-batch'@'localhost' IDENTIFIED BY 'A secret makes a woman woman.';
GRANT ALL ON              `safi`.*
                       TO 'safi-batch'@'localhost';
CREATE USER               'safi'@'localhost' IDENTIFIED BY 'Golden Hammer';
GRANT ALL ON              `safi`.*
                       TO 'safi'@'localhost';
USE                       `safi`;


DROP   TABLE IF EXISTS `w_import`;
CREATE TABLE           `w_import` (
-- ---------------------+--------+--------+--------+---
    `id`                     CHAR(     36) NOT NULL
  , `digest`                 CHAR(    128) NOT NULL
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT
  COMMENT='Work table for identity content import process.'
;


DROP   TABLE IF EXISTS `t_user`;
CREATE TABLE           `t_user` (
-- ---------------------+--------+--------+--------+---
    `id`                     CHAR(     36) NOT NULL
  , `enabled`             BOOLEAN          NOT NULL
  , `name`                VARCHAR(    100)          COLLATE utf8mb4_unicode_ci
  , `att01`               VARCHAR(    200)          COLLATE utf8mb4_unicode_ci
  , `att02`               VARCHAR(    200)          COLLATE utf8mb4_unicode_ci
  , `att03`               VARCHAR(    200)          COLLATE utf8mb4_unicode_ci
  , `att04`               VARCHAR(    200)          COLLATE utf8mb4_unicode_ci
  , `att05`               VARCHAR(    200)          COLLATE utf8mb4_unicode_ci
  , `att06`               VARCHAR(    200)          COLLATE utf8mb4_unicode_ci
  , `att07`               VARCHAR(    200)          COLLATE utf8mb4_unicode_ci
  , `att08`               VARCHAR(    200)          COLLATE utf8mb4_unicode_ci
  , `att09`               VARCHAR(    200)          COLLATE utf8mb4_unicode_ci
  , `att10`               VARCHAR(    200)          COLLATE utf8mb4_unicode_ci
  , `from_ts`            DATETIME          NOT NULL
  , `to_ts`              DATETIME          NOT NULL
  , `ban`                 BOOLEAN          NOT NULL
  , `digest`                 CHAR(    128) NOT NULL
-- ---------------------+--------+--------+--------+---
  , `txt_enabled`            CHAR(      5) GENERATED ALWAYS AS (CASE WHEN `enabled` THEN 'true' ELSE 'false' END) VIRTUAL
  , `txt_from_ts`            CHAR(     19) GENERATED ALWAYS AS (DATE_FORMAT(`from_ts`, '%Y-%m-%dT%H:%i:%s')     ) VIRTUAL
  , `txt_to_ts`              CHAR(     19) GENERATED ALWAYS AS (DATE_FORMAT(`to_ts`  , '%Y-%m-%dT%H:%i:%s')     ) VIRTUAL
  , `txt_ban`                CHAR(      5) GENERATED ALWAYS AS (CASE WHEN `ban`     THEN 'true' ELSE 'false' END) VIRTUAL
-- ---------------------+--------+--------+--------+---
  , `note`                   TEXT                   COLLATE utf8mb4_unicode_ci
  , `version`                 INT          NOT NULL
  , `reg_ts`             DATETIME          NOT NULL
  , `reg_id`              VARCHAR(     36) NOT NULL
  , `reg_ap`              VARCHAR(     36) NOT NULL
  , `upd_ts`             DATETIME          NOT NULL
  , `upd_id`              VARCHAR(     36) NOT NULL
  , `upd_ap`              VARCHAR(     36) NOT NULL
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC
  COMMENT='Identity content of the user.'
;

DROP   TABLE IF EXISTS `m_time`;
CREATE TABLE           `m_time` (
-- ---------------------+--------+--------+--------+---
    `kind`                VARCHAR(     20) NOT NULL
  , `value`              DATETIME
  , `from_ts`            DATETIME          NOT NULL
  , `to_ts`              DATETIME          NOT NULL
  , `ban`                 BOOLEAN          NOT NULL
-- ---------------------+--------+--------+--------+---
  , `note`                   TEXT                   COLLATE utf8mb4_unicode_ci
  , `version`                 INT          NOT NULL
  , `reg_ts`             DATETIME          NOT NULL
  , `reg_id`              VARCHAR(     36) NOT NULL
  , `reg_ap`              VARCHAR(     36) NOT NULL
  , `upd_ts`             DATETIME          NOT NULL
  , `upd_id`              VARCHAR(     36) NOT NULL
  , `upd_ap`              VARCHAR(     36) NOT NULL
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`kind`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC
  COMMENT='Time definitions for application.'
;


```

## Setup GlassFish

### Pool SafiPool
Pool Name: SafiPool
Resource Type: javax.sql.XADataSource
Database Driver Vendor: MariaDB
User: safi
Url: jdbc:mariadb://localhost/safi?sessionVariables=innodb_lock_wait_timeout=3
Password: Golden Hammer


### Pool SafiBatchPool
Pool Name: SafiBatchPool
Resource Type: javax.sql.XADataSource
Database Driver Vendor: MariaDB
User: safi-batch
Url: jdbc:mariadb://localhost/safi?sessionVariables=innodb_lock_wait_timeout=60
Password: A secret makes a woman woman.


### JDBC Resource jdbc/safi
JNDI Name: jdbc/safi


### JDBC Resource jdbc/safi-batch
JNDI Name: jdbc/safi-batch

### JVM Options
-Dsafi.root.dir=/path-of-dist/safi

## files
cd ${dist}
mkdir safi
mkdir safi/tmp
mkdir safi/var
mkdir safi/var/job
mkdir safi/var/plugin
mkdir safi/var/plugin/function
mkdir safi/var/plugin/importer

