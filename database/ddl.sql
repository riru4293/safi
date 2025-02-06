
DROP   TABLE IF EXISTS `t_user`;
CREATE TABLE           `t_user` (
-- ---------------------+--------+--------+--------+---
    `id`                     CHAR(     36) NOT NULL
  , `name`                VARCHAR(    100)          COLLATE utf8mb4_unicode_ci
  , `enabled`             BOOLEAN          NOT NULL
  , `from_ts`            DATETIME          NOT NULL
  , `to_ts`              DATETIME          NOT NULL
  , `ban`                 BOOLEAN          NOT NULL
  , `atts`                   JSON                   COLLATE utf8mb4_unicode_ci
  , `digest`                 CHAR(    128) NOT NULL
-- ---------------------+--------+--------+--------+---
  , `note`                   TEXT                   COLLATE utf8mb4_unicode_ci
  , `version`                 INT          NOT NULL
  , `reg_ts`             DATETIME          NOT NULL
  , `reg_id`              VARCHAR(    250) NOT NULL
  , `reg_ap`              VARCHAR(    250) NOT NULL
  , `upd_ts`             DATETIME          NOT NULL
  , `upd_id`              VARCHAR(    250) NOT NULL
  , `upd_ap`              VARCHAR(    250) NOT NULL
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC
  COMMENT='Content of the user.'
  ENCRYPTED=YES ENCRYPTION_KEY_ID=5
;