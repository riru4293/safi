# Simple And Flexible Idm

## Java-Doc

<i>固有名詞</i>

## Setup

We will build an https-compatible web server, Jakarta EE server, and MariaDB.

### Creates a self signed certificates

[[install]](setup/SelfSignedCertificate/install.md)

## Install MariaDB

## Setup database (MariaDB)

```SQL
DROP   USER     IF EXISTS 'safi'@'localhost';
DROP   DATABASE IF EXISTS `safi`;
CREATE DATABASE           `safi` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'Simple And Flexible IDM';
CREATE USER               'safi'@'localhost' IDENTIFIED BY 'Golden Hammer';
GRANT ALL ON              `safi`.*
                       TO 'safi'@'localhost';
USE                       `safi`;


DROP   TABLE IF EXISTS `w_content`;
CREATE TABLE           `w_content` (
-- ---------------------+--------+--------+--------+---
    `id`                     CHAR(     36) NOT NULL
  , `digest`                 CHAR(    128) NOT NULL
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT
  COMMENT='Working table for calculating content differences.'
;


DROP   TABLE IF EXISTS `t_user`;
CREATE TABLE           `t_user` (
-- ---------------------+--------+--------+--------+---
    `id`                     CHAR(     36) NOT NULL
  , `enabled`             BOOLEAN          NOT NULL
  , `name`                VARCHAR(    100)          COLLATE utf8mb4_unicode_ci
  , `atts`                   JSON                   COLLATE utf8mb4_unicode_ci
  , `from_ts`            DATETIME          NOT NULL
  , `to_ts`              DATETIME          NOT NULL
  , `ban`                 BOOLEAN          NOT NULL
  , `digest`                 CHAR(    128) NOT NULL
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
  COMMENT='Content of the user.'
;


```

## Setup GlassFish

### Pool SafiPool
Pool Name: SafiPool
Resource Type: javax.sql.XADataSource
Database Driver Vendor: MariaDB
User: safi
Url: jdbc:mariadb://localhost/safi?sessionVariables=innodb_lock_wait_timeout=60
Password: Golden Hammer


### JDBC Resource jdbc/safi
JNDI Name: jdbc/safi

### JVM Options
-Dsafi.home=/opt/safi



