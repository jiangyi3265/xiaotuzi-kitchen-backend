-- 小程序“我的”页面公众号二维码配置。
-- 部署脚本会重复执行本文件，因此仅在字段不存在时补充。
set @has_official_account_qr=(select count(1) from information_schema.columns where table_schema=database() and table_name='kitchen_shop' and column_name='official_account_qr');
set @sql=if(@has_official_account_qr=0,'alter table kitchen_shop add column official_account_qr varchar(255) default '''' comment ''公众号二维码'' after subtitle','select 1');
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
