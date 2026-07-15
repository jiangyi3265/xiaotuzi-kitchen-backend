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

insert into sys_menu
  (menu_name, parent_id, order_num, path, component, query, route_name,
   is_frame, is_cache, menu_type, visible, status, perms, icon,
   create_by, create_time, update_by, update_time, remark)
select
  '小程序功能开关',
  coalesce((
    select menu_id
    from sys_menu
    where menu_name = '系统管理' and menu_type = 'M'
    order by menu_id
    limit 1
  ), 1),
  100,
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
where not exists (
  select 1 from sys_menu where component = 'applet/switch/index'
);
