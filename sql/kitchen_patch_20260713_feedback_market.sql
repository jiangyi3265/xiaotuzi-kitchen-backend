-- 反馈建议与商家提前备货群配置
set @has_stock_qr=(select count(1) from information_schema.columns where table_schema=database() and table_name='kitchen_shop' and column_name='stock_group_qr');
set @sql=if(@has_stock_qr=0,'alter table kitchen_shop add column stock_group_qr varchar(255) default '''' comment ''商家提前备货群二维码'' after store_phone','select 1'); prepare stmt from @sql; execute stmt; deallocate prepare stmt;
set @has_stock_name=(select count(1) from information_schema.columns where table_schema=database() and table_name='kitchen_shop' and column_name='stock_group_name');
set @sql=if(@has_stock_name=0,'alter table kitchen_shop add column stock_group_name varchar(100) default '''' comment ''商家提前备货群名称'' after stock_group_qr','select 1'); prepare stmt from @sql; execute stmt; deallocate prepare stmt;
set @has_stock_notice=(select count(1) from information_schema.columns where table_schema=database() and table_name='kitchen_shop' and column_name='stock_group_notice');
set @sql=if(@has_stock_notice=0,'alter table kitchen_shop add column stock_group_notice varchar(500) default '''' comment ''商家提前备货群说明'' after stock_group_name','select 1'); prepare stmt from @sql; execute stmt; deallocate prepare stmt;

create table if not exists kitchen_feedback (
  id bigint not null auto_increment,
  wx_user_id bigint not null,
  feedback_type char(1) default '0' comment '0功能建议 1问题反馈 2服务投诉 3其他',
  content varchar(1000) not null,
  contact varchar(100) default '',
  images varchar(1200) default '',
  handle_status char(1) default '0' comment '0待处理 1处理中 2已回复',
  reply varchar(1000) default '',
  create_time datetime default current_timestamp,
  update_time datetime default null,
  primary key(id), key idx_feedback_user(wx_user_id), key idx_feedback_status(handle_status)
) engine=InnoDB default charset=utf8mb4 comment='用户反馈与建议';

set @kitchen_menu_id := (select menu_id from sys_menu where menu_name in ('私房菜管理','厨房管理') order by field(menu_name,'私房菜管理','厨房管理') limit 1);
set @feedback_menu_id := (select menu_id from sys_menu where perms='kitchen:feedback:list' order by menu_id limit 1);
set @feedback_menu_id := coalesce(@feedback_menu_id,(select coalesce(max(menu_id),2100)+1 from sys_menu));
insert into sys_menu(menu_id,menu_name,parent_id,order_num,path,component,query,route_name,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,remark)
select @feedback_menu_id,'反馈与建议',@kitchen_menu_id,14,'feedback','kitchen/feedback/index',null,'Feedback',1,0,'C','0','0','kitchen:feedback:list','message','admin',sysdate(),'小程序用户反馈处理'
where @kitchen_menu_id is not null and not exists(select 1 from sys_menu where perms='kitchen:feedback:list');
set @feedback_menu_id := (select menu_id from sys_menu where perms='kitchen:feedback:list' order by menu_id limit 1);
set @feedback_handle_menu_id := (select menu_id from sys_menu where perms='kitchen:feedback:handle' order by menu_id limit 1);
set @feedback_handle_menu_id := coalesce(@feedback_handle_menu_id,(select coalesce(max(menu_id),2100)+1 from sys_menu));
insert into sys_menu(menu_id,menu_name,parent_id,order_num,path,component,query,route_name,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,remark)
select @feedback_handle_menu_id,'反馈处理',@feedback_menu_id,1,'','','','',1,0,'F','0','0','kitchen:feedback:handle','#','admin',sysdate(),''
where @feedback_menu_id is not null and not exists(select 1 from sys_menu where perms='kitchen:feedback:handle');
insert ignore into sys_role_menu(role_id,menu_id) select 1,menu_id from sys_menu where perms='kitchen:feedback:list';
insert ignore into sys_role_menu(role_id,menu_id) select 1,menu_id from sys_menu where perms='kitchen:feedback:handle';
