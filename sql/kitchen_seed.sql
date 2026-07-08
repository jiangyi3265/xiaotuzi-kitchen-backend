-- ============================================================================
-- 小兔子厨房 · 演示种子数据
-- 用途：让"小程序 / 管理后台 / 后端"三端接通后能立即看到真实数据。
-- 依赖：先执行 kitchen.sql 建表建菜单，再执行本文件。
-- 说明：封面/图片统一使用小程序自带的 /static/*.png，小程序端可直接渲染；
--       如需在管理后台预览图片，可将其替换为后端可访问的图片 URL。
-- 重复执行安全：每段先按主键清理再插入。
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 分类（两级：一级 -> 二级；菜品挂在二级）
-- ----------------------------
DELETE FROM `kitchen_category` WHERE `id` IN (1,2,3,11,12,21,31);
INSERT INTO `kitchen_category` (`id`,`parent_id`,`ancestors`,`cat_name`,`image`,`cat_level`,`order_num`,`status`,`create_by`,`create_time`) VALUES
(1, 0, '0',    '家常菜', '/static/onion_chicken.png', 1, 1, '0', 'admin', NOW()),
(2, 0, '0',    '营养汤', '/static/seaweed_soup.png',  1, 2, '0', 'admin', NOW()),
(3, 0, '0',    '主食',   '/static/egg_pancake.png',   1, 3, '0', 'admin', NOW()),
(11,1, '0,1',  '猪肉类', '/static/minced_beans.png',  2, 1, '0', 'admin', NOW()),
(12,1, '0,1',  '素菜',   '/static/sweet_corn.png',    2, 2, '0', 'admin', NOW()),
(21,2, '0,2',  '家常汤', '/static/seaweed_soup.png',  2, 1, '0', 'admin', NOW()),
(31,3, '0,3',  '面饭',   '/static/pineapple_rice.png',2, 1, '0', 'admin', NOW());

-- ----------------------------
-- 菜品
-- ----------------------------
DELETE FROM `kitchen_dish` WHERE `id` IN (1,2,3,4,5,6);
INSERT INTO `kitchen_dish`
(`id`,`dish_name`,`cover`,`category_id`,`story`,`ingredients`,`virtual_price`,`has_specs`,`recipe_open`,`cooking_exp`,`prep_time`,`cook_time`,`difficulty`,`portions`,`sales`,`order_num`,`status`,`create_by`,`create_time`) VALUES
(1,'红烧肉','/static/minced_beans.png',11,'浓油赤酱、肥而不腻，五花肉先煸出油再上色，最后加一点冰糖，颜色就漂亮很多。','五花肉、冰糖、葱、姜、八角、生抽、老抽、料酒',28.00,'0','1','冰糖炒糖色时小火，颜色枣红即可下肉。',15,45,'中等',3,26,1,'1','admin',NOW()),
(2,'炖猪脚','/static/onion_chicken.png',11,'软糯入味的家常炖猪脚，小火慢炖到软糯，汤汁收得刚刚好，很适合配米饭。','猪脚、冰糖、葱、姜、料酒、八角、桂皮、生抽、老抽',38.00,'1','1','焯水后加葱姜料酒去腥，全程小火。',20,60,'困难',4,18,2,'1','admin',NOW()),
(3,'蒜蓉青菜','/static/sweet_corn.png',12,'清爽快手的一道素菜，蒜香扑鼻，几分钟出锅。','青菜、蒜、盐、食用油',12.00,'0','1','热锅快炒，断生即可保持翠绿。',5,5,'简单',2,40,1,'1','admin',NOW()),
(4,'番茄炒蛋','/static/egg_bubble.png',12,'酸甜开胃的国民下饭菜，番茄炒出沙、鸡蛋嫩滑。','番茄、鸡蛋、葱花、盐、糖、食用油',10.00,'0','0','',5,8,'简单',2,55,2,'1','admin',NOW()),
(5,'紫菜蛋花汤','/static/seaweed_soup.png',21,'十分钟搞定，清淡但很鲜，适合搭配今天的肉菜。','紫菜、鸡蛋、虾皮、葱花、盐、香油',8.00,'0','1','蛋液沿边缘淋入并轻推，蛋花更漂亮。',3,7,'简单',3,32,1,'1','admin',NOW()),
(6,'鸡蛋饼','/static/egg_pancake.png',31,'快手早餐，外脆内软，卷什么都好吃。','鸡蛋、面粉、葱花、盐、清水、食用油',9.00,'0','0','',5,6,'简单',2,21,1,'1','admin',NOW());

-- ----------------------------
-- 菜品做法步骤（仅公开做法的菜品）
-- ----------------------------
DELETE FROM `kitchen_dish_step` WHERE `dish_id` IN (1,2,3,5);
INSERT INTO `kitchen_dish_step` (`dish_id`,`step_no`,`image`,`content`,`timer`) VALUES
(1,1,'/static/minced_beans.png','五花肉切麻将块，冷水下锅焯水去血沫，捞出沥干。',0),
(1,2,'/static/minced_beans.png','小火冰糖炒糖色至枣红，下肉块翻炒上色。',0),
(1,3,'/static/minced_beans.png','加热水没过肉，放葱姜八角，小火炖45分钟收汁。',2700),
(2,1,'/static/onion_chicken.png','猪脚焯水，加葱姜料酒去腥。',0),
(2,2,'/static/onion_chicken.png','炒糖色后下猪脚翻炒，加水与香料小火慢炖。',3600),
(3,1,'/static/sweet_corn.png','青菜洗净沥干，蒜切末。',0),
(3,2,'/static/sweet_corn.png','热锅热油爆香蒜末，下青菜大火快炒断生调味出锅。',0),
(5,1,'/static/seaweed_soup.png','紫菜撕碎入碗，水烧开。',0),
(5,2,'/static/seaweed_soup.png','水开下紫菜，淋入蛋液轻推成蛋花，调味即可。',0);

-- ----------------------------
-- 菜品多规格（示例：炖猪脚 分量）
-- ----------------------------
DELETE FROM `kitchen_dish_spec` WHERE `id` IN (1);
DELETE FROM `kitchen_dish_spec_value` WHERE `spec_id` IN (1);
INSERT INTO `kitchen_dish_spec` (`id`,`dish_id`,`spec_name`,`multiple`,`order_num`) VALUES
(1,2,'分量','0',1);
INSERT INTO `kitchen_dish_spec_value` (`spec_id`,`spec_value`,`order_num`) VALUES
(1,'小份(2人)',1),
(1,'大份(4人)',2);

-- ----------------------------
-- 厨师（代炒）
-- ----------------------------
DELETE FROM `kitchen_chef` WHERE `id` IN (1,2,3);
INSERT INTO `kitchen_chef` (`id`,`chef_name`,`avatar`,`skill_tag`,`intro`,`extra_price`,`est_time`,`status`,`order_num`,`create_by`,`create_time`) VALUES
(1,'林师傅','/static/kitchen_avatar.png','红烧拿手','擅长炖猪脚、红烧肉、家常硬菜',18.00,'约40分钟','0',1,'admin',NOW()),
(2,'陈师傅','/static/kitchen_avatar.png','清淡快炒','擅长小炒肉、蒜蓉青菜、汤羹',12.00,'约30分钟','0',2,'admin',NOW()),
(3,'王师傅','/static/kitchen_avatar.png','川湘风味','擅长香辣菜、下饭菜、肉禽类',16.00,'约35分钟','0',3,'admin',NOW());

-- ----------------------------
-- 示例小程序用户（供分享广场展示昵称/头像）
-- ----------------------------
DELETE FROM `kitchen_wx_user` WHERE `id` IN (1);
INSERT INTO `kitchen_wx_user` (`id`,`openid`,`nickname`,`avatar`,`user_code`,`gender`,`carrot`,`status`,`is_owner`,`create_by`,`create_time`) VALUES
(1,'seed_openid_demo','御厨小兔','/static/kitchen_avatar.png','888888','0',120,'0','1','admin',NOW());

-- ----------------------------
-- 分享广场（已审核通过，小程序端可见）
-- ----------------------------
DELETE FROM `kitchen_share_post` WHERE `id` IN (1,2);
INSERT INTO `kitchen_share_post` (`id`,`wx_user_id`,`title`,`content`,`images`,`tags`,`like_count`,`comment_count`,`audit_status`,`create_by`,`create_time`) VALUES
(1,1,'红烧肉交作业','五花肉先煸出油再上色，最后加一点冰糖，颜色就漂亮很多。','/static/minced_beans.png','红烧拿手,晚餐,下饭菜',42,8,'1','admin',NOW()),
(2,1,'紫菜蛋花汤','十分钟搞定，清淡但很鲜，适合搭配今天的肉菜。','/static/seaweed_soup.png','营养汤,快手菜',26,5,'1','admin',NOW());

-- ----------------------------
-- 配送员（同城配送）
-- ----------------------------
DELETE FROM `kitchen_rider` WHERE `id` IN (1,2);
INSERT INTO `kitchen_rider` (`id`,`rider_name`,`avatar`,`tag`,`intro`,`delivery_fee`,`est_time`,`status`,`order_num`,`create_by`,`create_time`) VALUES
(1,'赵师傅','/static/kitchen_avatar.png','准时送达','熟悉本地路线，午晚高峰也能快送',6.00,'约30分钟','0',1,'admin',NOW()),
(2,'孙师傅','/static/kitchen_avatar.png','电动车','3公里内优先配送，可代取代送',5.00,'约25分钟','0',2,'admin',NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- 完成：现在小程序「厨房/发现/下单/厨师代炒」均有真实数据；
-- 管理后台「私房菜管理」各页也可看到并管理这些数据。
