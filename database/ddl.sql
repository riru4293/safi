
DROP   TABLE IF EXISTS `t_user`;
CREATE TABLE           `t_user` (
-- ---------------------+--------+--------+--------+---
    `id`                     CHAR(     36) NOT NULL
  , `name`                VARCHAR(    100)          COLLATE utf8mb4_unicode_ci
  , `enabled`             BOOLEAN          NOT NULL DEFAULT TRUE
  , `from_ts`            DATETIME          NOT NULL DEFAULT '2000-01-01T00:00:00'
  , `to_ts`              DATETIME          NOT NULL DEFAULT '2999-12-31T23:59:59'
  , `ignored`             BOOLEAN          NOT NULL DEFAULT FALSE
  , `atts`                   JSON          NOT NULL COLLATE utf8mb4_unicode_ci DEFAULT '{}'
  , `digest`                 CHAR(    128) NOT NULL DEFAULT 'unknown'
-- ---------------------+--------+--------+--------+---
  , `note`                   TEXT                   COLLATE utf8mb4_unicode_ci
  , `version`                 INT          NOT NULL DEFAULT 1
  , `reg_ts`             DATETIME
  , `reg_id`              VARCHAR(    250)
  , `reg_ap`              VARCHAR(    250)
  , `upd_ts`             DATETIME
  , `upd_id`              VARCHAR(    250)
  , `upd_ap`              VARCHAR(    250)
-- ---------------------+--------+--------+--------+---
  , CHECK (CHAR_LENGTH(`id`) > 0)
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC
  COMMENT='Content of the user.'
  ENCRYPTED=YES ENCRYPTION_KEY_ID=5
;
