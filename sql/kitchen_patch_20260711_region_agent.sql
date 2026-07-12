-- 区域代理申请及服务开通状态
create table if not exists kitchen_region_application (
  id bigint not null auto_increment comment '申请ID',
  wx_user_id bigint not null comment '微信用户ID',
  applicant_name varchar(50) not null comment '申请人姓名',
  phone varchar(20) not null comment '联系电话',
  province varchar(50) not null comment '省',
  city varchar(50) not null comment '市',
  district varchar(50) not null comment '区县',
  address varchar(255) default '' comment '详细地址',
  experience varchar(500) default '' comment '相关经验/申请说明',
  audit_status char(1) default '0' comment '审核状态 0待审核 1通过 2驳回',
  enabled char(1) default '0' comment '区域服务状态 0未开通 1已开通',
  create_time datetime default current_timestamp,
  update_time datetime default null,
  remark varchar(500) default '',
  primary key (id),
  key idx_region (province,city,district,audit_status,enabled),
  key idx_wx_user (wx_user_id)
) engine=InnoDB default charset=utf8mb4 comment='区域代理申请';

-- 后台菜单（父菜单 kitchenId 请按实际“厨房管理”菜单ID替换）
set @kitchen_menu_id = (select menu_id from sys_menu where menu_name in ('私房菜管理','厨房管理') order by field(menu_name,'私房菜管理','厨房管理') limit 1);
insert into sys_menu(menu_name,parent_id,order_num,path,component,query,route_name,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,remark)
select '区域代理审核',@kitchen_menu_id,12,'regionApplication','kitchen/regionApplication/index',null,'RegionApplication',1,0,'C','0','0','kitchen:regionApplication:list','map','admin',sysdate(),'区域开通及代理申请审核'
where @kitchen_menu_id is not null and not exists(select 1 from sys_menu where perms='kitchen:regionApplication:list');
set @region_menu_id = (select menu_id from sys_menu where perms='kitchen:regionApplication:list' limit 1);
insert into sys_menu(menu_name,parent_id,order_num,path,component,query,route_name,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,remark)
select '区域代理审核',@region_menu_id,1,'#','',null,'',1,0,'F','0','0','kitchen:regionApplication:audit','#','admin',sysdate(),'' where @region_menu_id is not null and not exists(select 1 from sys_menu where perms='kitchen:regionApplication:audit');
insert into sys_menu(menu_name,parent_id,order_num,path,component,query,route_name,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,remark)
select '区域服务启停',@region_menu_id,2,'#','',null,'',1,0,'F','0','0','kitchen:regionApplication:edit','#','admin',sysdate(),'' where @region_menu_id is not null and not exists(select 1 from sys_menu where perms='kitchen:regionApplication:edit');
