INSERT INTO `m_jobdef` (
    `id`
  , `from_ts`
  , `to_ts`
  , `ignored`
  , `job_kind`
  , `job_target`
  , `timeout`
  , `name`
  , `plugin`
  , `trnsdef`
  , `filtdef`
  , `job_props`
  , `note`
  , `version`
  , `reg_ts`
  , `reg_id`
  , `reg_ap`
  , `upd_ts`
  , `upd_id`
  , `upd_ap`
) VALUES (
    'test'
  , '2000-01-01 00:00:00'
  , '2999-12-31 23:59:59'
  , FALSE
  , 'IMPORT'
  , 'USER'
  , 'PT10M'
  , 'test-definition'
  , 'PluginName'
  , '{}'
  , '{"trnsdef": {}, "condition": {"operation": "AND", "children": []}}'
  , '{}'
  , 'for test'
  , 1
  , UTC_TIMESTAMP()
  , 'admin'
  , 'SQL'
  , NULL
  , NULL
  , NULL
);
