-- 配送员与代炒厨师申请、后台审核及人员资料联动
create table if not exists kitchen_service_application (
  id bigint not null auto_increment comment '申请ID',
  wx_user_id bigint not null comment '小程序用户ID',
  application_type char(1) not null comment '申请类型(0配送员 1代炒厨师)',
  applicant_name varchar(50) not null comment '申请人姓名',
  phone varchar(20) not null comment '联系电话',
  province varchar(50) not null comment '省',
  city varchar(50) not null comment '市',
  district varchar(50) not null comment '区县',
  address varchar(255) default '' comment '详细地址',
  experience varchar(500) default '' comment '相关经验',
  skill_tag varchar(100) default '' comment '厨师擅长菜系',
  vehicle_type varchar(100) default '' comment '配送交通工具',
  audit_status char(1) default '0' comment '审核状态(0待审核 1通过 2驳回)',
  provider_id bigint default null comment '通过后生成的厨师或配送员ID',
  remark varchar(500) default '' comment '审核备注',
  create_time datetime default current_timestamp,
  update_time datetime default null,
  primary key (id),
  key idx_service_application_user_type (wx_user_id,application_type),
  key idx_service_application_audit (audit_status)
) engine=InnoDB default charset=utf8mb4 comment='配送员与代炒厨师申请';

set @kitchen_menu_id := (select menu_id from sys_menu where menu_name='私厨管理' order by menu_id limit 1);
set @service_menu_id := (select menu_id from sys_menu where perms='kitchen:serviceApplication:list' order by menu_id limit 1);
set @service_menu_id := coalesce(@service_menu_id,(select coalesce(max(menu_id),2100)+1 from sys_menu));
insert into sys_menu(menu_id,menu_name,parent_id,order_num,path,component,query,route_name,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,remark)
select @service_menu_id,'服务人员审核',@kitchen_menu_id,13,'serviceApplication','kitchen/serviceApplication/index',null,'ServiceApplication',1,0,'C','0','0','kitchen:serviceApplication:list','user', 'admin',sysdate(),'配送员与代炒厨师申请审核'
where @kitchen_menu_id is not null and not exists(select 1 from sys_menu where perms='kitchen:serviceApplication:list');
set @service_menu_id := (select menu_id from sys_menu where perms='kitchen:serviceApplication:list' order by menu_id limit 1);
set @service_audit_menu_id := (select menu_id from sys_menu where perms='kitchen:serviceApplication:audit' order by menu_id limit 1);
set @service_audit_menu_id := coalesce(@service_audit_menu_id,(select coalesce(max(menu_id),2100)+1 from sys_menu));
insert into sys_menu(menu_id,menu_name,parent_id,order_num,path,component,query,route_name,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,remark)
select @service_audit_menu_id,'服务人员审核操作',@service_menu_id,1,'','','','',1,0,'F','0','0','kitchen:serviceApplication:audit','#','admin',sysdate(),''
where @service_menu_id is not null and not exists(select 1 from sys_menu where perms='kitchen:serviceApplication:audit');
insert ignore into sys_role_menu(role_id,menu_id) select 1,menu_id from sys_menu where perms='kitchen:serviceApplication:list';
insert ignore into sys_role_menu(role_id,menu_id) select 1,menu_id from sys_menu where perms='kitchen:serviceApplication:audit';
