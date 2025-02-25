
DROP   TABLE IF EXISTS `t_user`;
CREATE TABLE           `t_user` (
-- ---------------------+--------+--------+--------+-----------------------------------
    `id`                  VARCHAR(     36) NOT NULL                                    COMMENT 'User ID'
  , `enabled`             BOOLEAN          NOT NULL DEFAULT TRUE                       COMMENT 'Enabled state. It is calculated from three items: from_ts, to_ts, and ignored, but the calculation is asynchronous.'
  , `from_ts`            DATETIME          NOT NULL DEFAULT '2000-01-01 00:00:00'      COMMENT 'Begin date-time of enabled period.'
  , `to_ts`              DATETIME          NOT NULL DEFAULT '2999-12-31 23:59:59'      COMMENT 'End date-time of enabled period.'
  , `ignored`             BOOLEAN          NOT NULL DEFAULT FALSE                      COMMENT 'Ignore flag. True means ignore and the enabled state is disabled.'
  , `name`                VARCHAR(    250)          COLLATE utf8mb4_unicode_ci         COMMENT 'User name'
  , `props`                  JSON          NOT NULL DEFAULT '{}'                       COMMENT 'Properties of user.'
  , `digest`                 CHAR(    128) NOT NULL DEFAULT 'unknown'                  COMMENT 'Hash value of this record.'
-- ---------------------+--------+--------+--------+-----------------------------------
  , `note`                   TEXT                   COLLATE utf8mb4_unicode_ci         COMMENT 'Notes for maintenance use only.'
  , `version`                 INT          NOT NULL DEFAULT 1                          COMMENT 'JPA entity version. Used for mutual exclusion control. Starts at 1 and increments with each update.'
  , `reg_ts`             DATETIME                                                      COMMENT 'Registerd time'
  , `reg_id`              VARCHAR(    250)                                             COMMENT 'The ID of the account that registered.'
  , `reg_ap`              VARCHAR(    250)                                             COMMENT 'The name of the application that registered.'
  , `upd_ts`             DATETIME                                                      COMMENT 'Updated time'
  , `upd_id`              VARCHAR(    250)                                             COMMENT 'The ID of the account that updated.'
  , `upd_ap`              VARCHAR(    250)                                             COMMENT 'The name of the application that updated.'
-- ---------------------+--------+--------+--------+-----------------------------------
  , CHECK (CHAR_LENGTH(`id`) > 0)
  , CHECK (CHAR_LENGTH(`digest`) > 0)
  , CHECK (`version` > 0)
-- ---------------------+--------+--------+--------+-----------------------------------
  , PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT
  COMMENT='Content of the user.'
  ENCRYPTED=YES ENCRYPTION_KEY_ID=5
;

DROP   TABLE IF EXISTS `t_job`;
CREATE TABLE           `t_job` (
-- ---------------------+--------+--------+--------+-----------------------------------
    `id`                  VARCHAR(     36) NOT NULL                                    COMMENT 'Job id'
  , `stat`                VARCHAR(     20) NOT NULL                                    COMMENT 'Job status. Values are SCHEDULE, RUNNING, SUCCESS, FAILURE, ABORT.'
  , `kind`                VARCHAR(     20) NOT NULL                                    COMMENT 'Job kind. Values are IMPORT, EXPORT, REBUILD.'
  , `target`              VARCHAR(     20) NOT NULL                                    COMMENT 'Target content type. Values are USER, ASSET, BELONG_ORG, ORG1, ORG2, BELONG_GRP, GRP, PER_USER, PER_ASSET.'
  , `sche_ts`            DATETIME          NOT NULL                                    COMMENT 'Job scheduling time.'
  , `limit_ts`           DATETIME          NOT NULL                                    COMMENT 'Expiration time for job.'            
  , `begin_ts`           DATETIME                                                      COMMENT 'Begun time of job.'
  , `end_ts`             DATETIME                                                      COMMEnt 'End time of job.'
  , `props`                  JSON          NOT NULL DEFAULT '{}'                       COMMENT 'Option values for job processing.'
  , `jobdef_id`           VARCHAR(     36)                                             COMMENT 'Source job definition id.'
  , `schedef_id`          VARCHAR(     36)                                             COMMENT 'Source schedule definition id.'
  , `srcdefs`                JSON          NOT NULL DEFAULT '{}'                       COMMENT 'Source definitions copy. The format is expected to be {"jobdef": {}, "schedef": {}}. No fields are required.'
  , `results`                JSON          NOT NULL DEFAULT '[]'                       COMMENT 'Result messages array. Expects the format: ["msg1", "msg2"].'
-- ---------------------+--------+--------+--------+-----------------------------------
  , `note`                   TEXT                   COLLATE utf8mb4_unicode_ci         COMMENT 'Notes for maintenance use only.'
  , `version`                 INT          NOT NULL DEFAULT 1                          COMMENT 'JPA entity version. Used for mutual exclusion control. Starts at 1 and increments with each update.'
  , `reg_ts`             DATETIME                                                      COMMENT 'Registerd time'
  , `reg_id`              VARCHAR(    250)                                             COMMENT 'The ID of the account that registered.'
  , `reg_ap`              VARCHAR(    250)                                             COMMENT 'The name of the application that registered.'
  , `upd_ts`             DATETIME                                                      COMMENT 'Updated time'
  , `upd_id`              VARCHAR(    250)                                             COMMENT 'The ID of the account that updated.'
  , `upd_ap`              VARCHAR(    250)                                             COMMENT 'The name of the application that updated.'
-- ---------------------+--------+--------+--------+-----------------------------------
  , CHECK (CHAR_LENGTH(`id`) > 0)
  , CHECK (`version` > 0)
-- ---------------------+--------+--------+--------+-----------------------------------
  , PRIMARY KEY (`id`)
  , KEY t_job_key01 (`stat`)                                                           COMMENT 'For exclusive control of jobs and rescheduling jobs.'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT
  COMMENT='Job schedule and result.'
  ENCRYPTED=YES ENCRYPTION_KEY_ID=5
;
