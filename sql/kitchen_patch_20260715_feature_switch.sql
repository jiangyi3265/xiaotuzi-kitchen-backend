-- 小程序完整功能开关与管理端菜单。
-- 本脚本会在每次后端部署时执行，因此只补充缺失数据，不覆盖管理员已经保存的开关值。

insert into sys_config
  (config_name, config_key, config_value, config_type, create_by, create_time, remark)
select
  '小程序完整功能开关',
  'wx.feature.enabled',
  'false',
  'Y',
  'admin',
  sysdate(),
  '总开关：开启后小程序显示点餐外卖与分享社交功能，关闭则隐藏（无资质时保持关闭）'
from dual
where not exists (
  select 1 from sys_config where config_key = 'wx.feature.enabled'
);

set @kitchen_menu_id := (
  select parent_id
  from sys_menu
  where component = 'kitchen/shop/index'
  order by menu_id
  limit 1
);

set @kitchen_menu_id := coalesce(@kitchen_menu_id, (
  select menu_id
  from sys_menu
  where menu_name in ('私房菜管理', '厨房管理')
  order by field(menu_name, '私房菜管理', '厨房管理'), menu_id
  limit 1
));

insert into sys_menu
  (menu_name, parent_id, order_num, path, component, query, route_name,
   is_frame, is_cache, menu_type, visible, status, perms, icon,
   create_by, create_time, update_by, update_time, remark)
select
  '小程序功能开关',
  @kitchen_menu_id,
  15,
  'appletSwitch',
  'applet/switch/index',
  '',
  '',
  1,
  0,
  'C',
  '0',
  '0',
  'applet:switch:list',
  'switch',
  'admin',
  sysdate(),
  '',
  null,
  '小程序点餐外卖/分享社交功能总开关'
from dual
where @kitchen_menu_id is not null
  and not exists (
  select 1 from sys_menu where component = 'applet/switch/index'
);

-- 修正早期部署中误挂到“系统管理”目录下的菜单。
update sys_menu
set parent_id = @kitchen_menu_id,
    order_num = 15
where component = 'applet/switch/index'
  and @kitchen_menu_id is not null;

-- 修改权限独立于查看权限；运营角色无需获得 system:config:* 即可安全操作固定开关。
set @applet_switch_menu_id := (
  select menu_id
  from sys_menu
  where component = 'applet/switch/index'
  order by menu_id
  limit 1
);

insert into sys_menu
  (menu_name, parent_id, order_num, path, component, query, route_name,
   is_frame, is_cache, menu_type, visible, status, perms, icon,
   create_by, create_time, update_by, update_time, remark)
select
  '小程序开关修改',
  @applet_switch_menu_id,
  1,
  '#',
  '',
  null,
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'applet:switch:edit',
  '#',
  'admin',
  sysdate(),
  '',
  null,
  '允许修改固定配置 wx.feature.enabled'
from dual
where @applet_switch_menu_id is not null
  and not exists (
    select 1 from sys_menu where perms = 'applet:switch:edit'
  );
