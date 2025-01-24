
Test table for JSON content.

```sql
DROP   TABLE IF EXISTS `test_content`;
CREATE TABLE           `test_content` (
-- ---------------------+--------+--------+--------+---
    `id`                     CHAR(     36) NOT NULL
  , `atts`                   JSON                   COLLATE utf8mb4_unicode_ci
-- ---------------------+--------+--------+--------+---
  , PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC
  COMMENT='Content of the user.'
  ENCRYPTED=YES ENCRYPTION_KEY_ID=8
;
```
