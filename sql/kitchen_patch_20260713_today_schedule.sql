-- 今日安排由后台菜品管理配置，空值表示不在“其他”页展示。
set @has_today_type=(select count(1) from information_schema.columns where table_schema=database() and table_name='kitchen_dish' and column_name='today_type');
set @sql=if(@has_today_type=0,
  'alter table kitchen_dish add column today_type varchar(16) default null comment ''今日安排类型：hotpot火锅类 barbecue烧烤'' after status',
  'select 1');
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
