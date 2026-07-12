create table if not exists kitchen_group_room(id bigint primary key auto_increment,room_code varchar(12) not null unique,owner_user_id bigint not null,title varchar(100) not null,status char(1) default '1',create_time datetime default current_timestamp) engine=InnoDB default charset=utf8mb4 comment='多人聚餐房间';
create table if not exists kitchen_group_member(id bigint primary key auto_increment,room_id bigint not null,wx_user_id bigint not null,join_time datetime default current_timestamp,unique key uk_room_user(room_id,wx_user_id),key idx_room(room_id)) engine=InnoDB default charset=utf8mb4 comment='聚餐成员';
create table if not exists kitchen_group_item(id bigint primary key auto_increment,room_id bigint not null,dish_id bigint not null,wx_user_id bigint not null,quantity int default 1,create_time datetime default current_timestamp,unique key uk_room_dish(room_id,dish_id),key idx_room(room_id)) engine=InnoDB default charset=utf8mb4 comment='聚餐共同点菜';
create table if not exists kitchen_couple_space(id bigint primary key auto_increment,invite_code varchar(12) not null unique,user_a bigint not null,user_b bigint default null,start_date date not null,status char(1) default '1',feed_count int default 0,bind_time datetime default null,create_time datetime default current_timestamp,key idx_users(user_a,user_b)) engine=InnoDB default charset=utf8mb4 comment='情侣空间';
create table if not exists kitchen_couple_anniversary(id bigint primary key auto_increment,couple_id bigint not null,title varchar(80) not null,anniversary_date date not null,create_time datetime default current_timestamp,key idx_couple(couple_id)) engine=InnoDB default charset=utf8mb4 comment='情侣纪念日';
create table if not exists kitchen_user_notification(id bigint primary key auto_increment,wx_user_id bigint not null,type varchar(30) not null,title varchar(100) not null,content varchar(500) default '',biz_id bigint default null,read_flag char(1) default '0',create_time datetime default current_timestamp,key idx_user_read(wx_user_id,read_flag)) engine=InnoDB default charset=utf8mb4 comment='小程序站内消息';

set @has_couple_space_id=(select count(1) from information_schema.columns where table_schema=database() and table_name='kitchen_order' and column_name='couple_space_id');
set @sql=if(@has_couple_space_id=0,'alter table kitchen_order add column couple_space_id bigint default null comment ''情侣空间ID'' after wx_user_id','select 1'); prepare stmt from @sql; execute stmt; deallocate prepare stmt;
set @has_recipient_id=(select count(1) from information_schema.columns where table_schema=database() and table_name='kitchen_order' and column_name='recipient_wx_user_id');
set @sql=if(@has_recipient_id=0,'alter table kitchen_order add column recipient_wx_user_id bigint default null comment ''异地投喂收餐用户'' after couple_space_id','select 1'); prepare stmt from @sql; execute stmt; deallocate prepare stmt;
set @has_group_room_id=(select count(1) from information_schema.columns where table_schema=database() and table_name='kitchen_order' and column_name='group_room_id');
set @sql=if(@has_group_room_id=0,'alter table kitchen_order add column group_room_id bigint default null comment ''多人聚餐房间ID'' after wx_user_id','select 1'); prepare stmt from @sql; execute stmt; deallocate prepare stmt;

set @kitchen_menu_id = (select menu_id from sys_menu where menu_name in ('私房菜管理','厨房管理') order by field(menu_name,'私房菜管理','厨房管理') limit 1);
insert into sys_menu(menu_name,parent_id,order_num,path,component,query,route_name,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,remark)
select '聚餐与情侣空间',@kitchen_menu_id,13,'socialSpace','kitchen/socialSpace/index',null,'SocialSpace',1,0,'C','0','0','kitchen:social:list','peoples','admin',sysdate(),'多人聚餐与情侣空间管理'
where @kitchen_menu_id is not null and not exists(select 1 from sys_menu where perms='kitchen:social:list');
set @social_menu_id = (select menu_id from sys_menu where perms='kitchen:social:list' limit 1);
insert into sys_menu(menu_name,parent_id,order_num,path,component,query,route_name,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,remark)
select '社交空间状态管理',@social_menu_id,1,'#','',null,'',1,0,'F','0','0','kitchen:social:edit','#','admin',sysdate(),''
where @social_menu_id is not null and not exists(select 1 from sys_menu where perms='kitchen:social:edit');
