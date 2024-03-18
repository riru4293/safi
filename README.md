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

DROP   TABLE IF EXISTS `m_jobdef`;
CREATE TABLE           `m_jobdef` (
-- ---------------------+--------+--------+--------+---
    `id`                     CHAR(     36) NOT NULL
  , `job_kind`            VARCHAR(     20) NOT NULL
  , `content_kind`        VARCHAR(     20) NOT NULL
  , `name`                TINYTEXT         NOT NULL COLLATE utf8mb4_unicode_ci
  , `timeout`             VARCHAR(     50) NOT NULL
  , `plugdef`                JSON
  , `filtdef`                JSON
  , `trnsdef`                JSON
  , `options`                JSON          NOT NULL
  , `from_ts`            DATETIME          NOT NULL
  , `to_ts`              DATETIME          NOT NULL
  , `ban`                 BOOLEAN          NOT NULL
-- ---------------------+--------+--------+--------+---
  , `note`                   TEXT                   COLLATE utf8mb4_unicode_ci
  , `version`                 INT          NOT NULL
  , `reg_ts`             DATETIME          NOT NULL
  , `reg_id`             TINYTEXT          NOT NULL
  , `reg_ap`             TINYTEXT          NOT NULL
  , `upd_ts`             DATETIME          NOT NULL
  , `upd_id`             TINYTEXT          NOT NULL
  , `upd_ap`             TINYTEXT          NOT NULL
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC
  COMMENT='Definition of the Job creation.'
;


DROP   TABLE IF EXISTS `m_schedef`;
CREATE TABLE           `m_schedef` (
-- ---------------------+--------+--------+--------+---
    `id`                     CHAR(     36) NOT NULL
  , `kind`                VARCHAR(     20) NOT NULL
  , `jobdef_id`              CHAR(     36) NOT NULL
  , `priority`               CHAR(      1) NOT NULL
  , `name`               TINYTEXT          NOT NULL COLLATE utf8mb4_unicode_ci
  , `value`                  JSON          NOT NULL
  , `from_ts`            DATETIME          NOT NULL
  , `to_ts`              DATETIME          NOT NULL
  , `ban`                 BOOLEAN          NOT NULL
-- ---------------------+--------+--------+--------+---
  , `note`                   TEXT                   COLLATE utf8mb4_unicode_ci
  , `version`                 INT          NOT NULL
  , `reg_ts`             DATETIME          NOT NULL
  , `reg_id`             TINYTEXT          NOT NULL
  , `reg_ap`             TINYTEXT          NOT NULL
  , `upd_ts`             DATETIME          NOT NULL
  , `upd_id`             TINYTEXT          NOT NULL
  , `upd_ap`             TINYTEXT          NOT NULL
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT
  COMMENT='Definition of the Job scheduling.'
;


DROP   TABLE IF EXISTS `t_job`;
CREATE TABLE           `t_job` (
-- ---------------------+--------+--------+--------+---
    `id`                     CHAR(     26) NOT NULL
  , `status`              VARCHAR(     20) NOT NULL
  , `schedule_ts`        DATETIME          NOT NULL
  , `limit_ts`           DATETIME          NOT NULL
  , `kind`                VARCHAR(     20) NOT NULL
  , `content_kind`        VARCHAR(     20) NOT NULL
  , `plugdef`                JSON
  , `filtdef`                JSON
  , `trnsdef`                JSON
  , `begin_ts`           DATETIME
  , `end_ts`             DATETIME
  , `messages`               JSON                   COLLATE utf8mb4_unicode_ci
  , `schedef_id`             CHAR(     36)
  , `jobdef_id`              CHAR(     36) NOT NULL
  , `options`                JSON          NOT NULL
-- ---------------------+--------+--------+--------+---
  , `note`                   TEXT                   COLLATE utf8mb4_unicode_ci
  , `version`                 INT          NOT NULL
  , `reg_ts`             DATETIME          NOT NULL
  , `reg_id`             TINYTEXT          NOT NULL
  , `reg_ap`             TINYTEXT          NOT NULL
  , `upd_ts`             DATETIME          NOT NULL
  , `upd_id`             TINYTEXT          NOT NULL
  , `upd_ap`             TINYTEXT          NOT NULL
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`id`)
  , KEY t_job_key01 (`status`, `schedule_ts`) COMMENT 'Use to find runnable jobs.'
  , KEY t_job_key02 (`status`, `limit_ts`) COMMENT 'Use to find expired jobs'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC
  COMMENT='Batch processing schedules, life-cycle management, and results.'
;


DROP   TABLE IF EXISTS `t_job_record`;
CREATE TABLE           `t_job_record` (
-- ---------------------+--------+--------+--------+---
    `id`                     CHAR(     26) NOT NULL
  , `job_id`                 CHAR(     26) NOT NULL
  , `content_id`             CHAR(     36)
  , `content_fmt`         VARCHAR(     20) NOT NULL
  , `contant_val`            JSON          NOT NULL
  , `kind`                VARCHAR(     20) NOT NULL
  , `failure_phase`       VARCHAR(     20)
  , `message`                TEXT                   COLLATE utf8mb4_unicode_ci
-- ---------------------+--------+--------+--------+---
  , `note`                   TEXT                   COLLATE utf8mb4_unicode_ci
  , `version`                 INT          NOT NULL
  , `reg_ts`             DATETIME          NOT NULL
  , `reg_id`             TINYTEXT          NOT NULL
  , `reg_ap`             TINYTEXT          NOT NULL
  , `upd_ts`             DATETIME          NOT NULL
  , `upd_id`             TINYTEXT          NOT NULL
  , `upd_ap`             TINYTEXT          NOT NULL
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`id`)
  ,         KEY `t_job_record_key01`(`job_id`, `kind`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT
  COMMENT='Recording that processed content by Job.'
;


DROP   TABLE IF EXISTS `w_import`;
CREATE TABLE           `w_import` (
-- ---------------------+--------+--------+--------+---
    `id`                     CHAR(     36) NOT NULL
  , `digest`                 CHAR(    128) NOT NULL
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT
  COMMENT='Working table for importing processing.'
;


DROP   TABLE IF EXISTS `t_user`;
CREATE TABLE           `t_user` (
-- ---------------------+--------+--------+--------+---
    `id`                     CHAR(     36) NOT NULL
  , `enabled`             BOOLEAN          NOT NULL
  , `name`               TINYTEXT                   COLLATE utf8mb4_unicode_ci
  , `att01`              TINYTEXT                   COLLATE utf8mb4_unicode_ci
  , `att02`              TINYTEXT                   COLLATE utf8mb4_unicode_ci
  , `att03`              TINYTEXT                   COLLATE utf8mb4_unicode_ci
  , `att04`              TINYTEXT                   COLLATE utf8mb4_unicode_ci
  , `att05`              TINYTEXT                   COLLATE utf8mb4_unicode_ci
  , `att06`              TINYTEXT                   COLLATE utf8mb4_unicode_ci
  , `att07`              TINYTEXT                   COLLATE utf8mb4_unicode_ci
  , `att08`              TINYTEXT                   COLLATE utf8mb4_unicode_ci
  , `att09`              TINYTEXT                   COLLATE utf8mb4_unicode_ci
  , `att10`              TINYTEXT                   COLLATE utf8mb4_unicode_ci
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
  , `reg_id`             TINYTEXT          NOT NULL
  , `reg_ap`             TINYTEXT          NOT NULL
  , `upd_ts`             DATETIME          NOT NULL
  , `upd_id`             TINYTEXT          NOT NULL
  , `upd_ap`             TINYTEXT          NOT NULL
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC
  COMMENT='User content.'
;


DROP   TABLE IF EXISTS `m_time`;
CREATE TABLE           `m_time` (
-- ---------------------+--------+--------+--------+---
    `id`                  VARCHAR(     20) NOT NULL
  , `value`              DATETIME
  , `from_ts`            DATETIME          NOT NULL
  , `to_ts`              DATETIME          NOT NULL
  , `ban`                 BOOLEAN          NOT NULL
-- ---------------------+--------+--------+--------+---
  , `note`                   TEXT                   COLLATE utf8mb4_unicode_ci
  , `version`                 INT          NOT NULL
  , `reg_ts`             DATETIME          NOT NULL
  , `reg_id`             TINYTEXT          NOT NULL
  , `reg_ap`             TINYTEXT          NOT NULL
  , `upd_ts`             DATETIME          NOT NULL
  , `upd_id`             TINYTEXT          NOT NULL
  , `upd_ap`             TINYTEXT          NOT NULL
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT
  COMMENT='Time definition on SAFI.'
;

DROP   TABLE IF EXISTS `m_period`;
CREATE TABLE           `m_period` (
-- ---------------------+--------+--------+--------+---
    `id`                  VARCHAR(     20) NOT NULL
  , `value`               VARCHAR(    100)          COMMENT 'PeriodDuration'
  , `from_ts`            DATETIME          NOT NULL
  , `to_ts`              DATETIME          NOT NULL
  , `ban`                 BOOLEAN          NOT NULL
-- ---------------------+--------+--------+--------+---
  , `note`                   TEXT                   COLLATE utf8mb4_unicode_ci
  , `version`                 INT          NOT NULL
  , `reg_ts`             DATETIME          NOT NULL
  , `reg_id`             TINYTEXT          NOT NULL
  , `reg_ap`             TINYTEXT          NOT NULL
  , `upd_ts`             DATETIME          NOT NULL
  , `upd_id`             TINYTEXT          NOT NULL
  , `upd_ap`             TINYTEXT          NOT NULL
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT
  COMMENT='Period definition on SAFI.'
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
mkdir safi/job
mkdir safi/plugin
