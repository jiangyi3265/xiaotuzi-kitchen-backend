-- ----------------------------------------------------------------------------
-- 私房菜厨房管理 业务建表脚本（基于若依 RuoYi-Vue 规范）
-- 所有表均包含若依标准字段：create_by/create_time/update_by/update_time/remark/del_flag
-- 字符集 utf8mb4，主键 bigint，逻辑删除 del_flag(0存在 2删除)
-- ----------------------------------------------------------------------------
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1、小程序用户表
-- ----------------------------
DROP TABLE IF EXISTS `kitchen_wx_user`;
CREATE TABLE `kitchen_wx_user` (
  `id`           bigint(20)   NOT NULL AUTO_INCREMENT      COMMENT '用户ID',
  `openid`       varchar(64)  NOT NULL                     COMMENT '微信openid',
  `union_id`     varchar(64)  DEFAULT ''                   COMMENT '微信unionid',
  `nickname`     varchar(64)  DEFAULT ''                   COMMENT '昵称',
  `avatar`       varchar(255) DEFAULT ''                   COMMENT '头像地址',
  `user_code`    varchar(32)  DEFAULT ''                   COMMENT '用户编码(展示ID)',
  `phone`        varchar(20)  DEFAULT ''                   COMMENT '手机号',
  `gender`       char(1)      DEFAULT '0'                  COMMENT '性别(0未知 1男 2女)',
  `carrot`       int(11)      DEFAULT 0                    COMMENT '胡萝卜积分',
  `status`       char(1)      DEFAULT '0'                  COMMENT '状态(0正常 1停用)',
  `is_owner`     char(1)      DEFAULT '0'                  COMMENT '是否店主(0否 1是)',
  `create_by`    varchar(64)  DEFAULT ''                   COMMENT '创建者',
  `create_time`  datetime                                 COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT ''                   COMMENT '更新者',
  `update_time`  datetime                                 COMMENT '更新时间',
  `remark`       varchar(500) DEFAULT NULL                 COMMENT '备注',
  `del_flag`     char(1)      DEFAULT '0'                  COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='小程序用户表';

-- ----------------------------
-- 2、厨房/店铺设置表
-- ----------------------------
DROP TABLE IF EXISTS `kitchen_shop`;
CREATE TABLE `kitchen_shop` (
  `id`             bigint(20)   NOT NULL AUTO_INCREMENT    COMMENT '厨房ID',
  `shop_name`      varchar(64)  DEFAULT ''                 COMMENT '厨房名称',
  `avatar`         varchar(255) DEFAULT ''                 COMMENT '厨房头像',
  `banner`         varchar(255) DEFAULT ''                 COMMENT '背景图',
  `subtitle`       varchar(128) DEFAULT ''                 COMMENT '副标题/口号',
  `invite_cover`   varchar(255) DEFAULT ''                 COMMENT '邀请封面',
  `invite_text`    varchar(255) DEFAULT ''                 COMMENT '邀请文案',
  `wechat_qr`      varchar(255) DEFAULT ''                 COMMENT '微信收款码',
  `alipay_qr`      varchar(255) DEFAULT ''                 COMMENT '支付宝收款码',
  `store_name`     varchar(64)  DEFAULT ''                 COMMENT '门店名称',
  `store_address`  varchar(255) DEFAULT ''                 COMMENT '门店地址',
  `business_hours` varchar(64)  DEFAULT ''                 COMMENT '营业时间',
  `store_phone`    varchar(20)  DEFAULT ''                 COMMENT '门店电话',
  `announce_enabled` char(1)    DEFAULT '1'                COMMENT '公告开关(0关 1开)',
  `announce_title`   varchar(64)  DEFAULT ''               COMMENT '公告标题',
  `announce_content` varchar(1000) DEFAULT ''              COMMENT '公告内容',
  `create_by`    varchar(64)  DEFAULT ''                   COMMENT '创建者',
  `create_time`  datetime                                 COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT ''                   COMMENT '更新者',
  `update_time`  datetime                                 COMMENT '更新时间',
  `remark`       varchar(500) DEFAULT NULL                 COMMENT '备注',
  `del_flag`     char(1)      DEFAULT '0'                  COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='厨房店铺设置表';

-- ----------------------------
-- 3、菜品分类表（三级树）
-- ----------------------------
DROP TABLE IF EXISTS `kitchen_category`;
CREATE TABLE `kitchen_category` (
  `id`          bigint(20)   NOT NULL AUTO_INCREMENT       COMMENT '分类ID',
  `parent_id`   bigint(20)   DEFAULT 0                     COMMENT '父分类ID',
  `ancestors`   varchar(255) DEFAULT ''                    COMMENT '祖级列表',
  `cat_name`    varchar(64)  NOT NULL                      COMMENT '分类名称',
  `image`       varchar(255) DEFAULT ''                    COMMENT '分类图片',
  `cat_level`   int(2)       DEFAULT 1                     COMMENT '层级(1/2/3)',
  `order_num`   int(4)       DEFAULT 0                     COMMENT '显示顺序',
  `status`      char(1)      DEFAULT '0'                   COMMENT '状态(0正常 1停用)',
  `create_by`    varchar(64)  DEFAULT ''                   COMMENT '创建者',
  `create_time`  datetime                                 COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT ''                   COMMENT '更新者',
  `update_time`  datetime                                 COMMENT '更新时间',
  `remark`       varchar(500) DEFAULT NULL                 COMMENT '备注',
  `del_flag`     char(1)      DEFAULT '0'                  COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='菜品分类表';

-- ----------------------------
-- 4、菜品表
-- ----------------------------
DROP TABLE IF EXISTS `kitchen_dish`;
CREATE TABLE `kitchen_dish` (
  `id`            bigint(20)   NOT NULL AUTO_INCREMENT      COMMENT '菜品ID',
  `dish_name`     varchar(64)  NOT NULL                     COMMENT '菜品名称',
  `cover`         varchar(255) DEFAULT ''                   COMMENT '封面图',
  `category_id`   bigint(20)   DEFAULT NULL                 COMMENT '分类ID',
  `story`         varchar(500) DEFAULT ''                   COMMENT '菜品描述/故事',
  `ingredients`   varchar(1000) DEFAULT ''                  COMMENT '所用用料',
  `virtual_price` decimal(10,2) DEFAULT NULL                COMMENT '虚拟金额(空则不展示)',
  `has_specs`     char(1)      DEFAULT '0'                  COMMENT '是否多规格(0否 1是)',
  `recipe_open`   char(1)      DEFAULT '1'                  COMMENT '做法是否公开(0否 1是)',
  `cooking_exp`   varchar(1000) DEFAULT ''                  COMMENT '烹饪经验',
  `prep_time`     int(4)       DEFAULT 0                    COMMENT '准备时间(分钟)',
  `cook_time`     int(4)       DEFAULT 0                    COMMENT '烹饪时间(分钟)',
  `difficulty`    varchar(8)   DEFAULT '中等'               COMMENT '难度(简单/中等/困难)',
  `portions`      int(4)       DEFAULT 2                    COMMENT '适合人数',
  `sales`         int(11)      DEFAULT 0                    COMMENT '销量',
  `order_num`     int(4)       DEFAULT 0                    COMMENT '显示顺序',
  `status`        char(1)      DEFAULT '1'                  COMMENT '状态(0下架 1上架)',
  `create_by`    varchar(64)  DEFAULT ''                   COMMENT '创建者',
  `create_time`  datetime                                 COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT ''                   COMMENT '更新者',
  `update_time`  datetime                                 COMMENT '更新时间',
  `remark`       varchar(500) DEFAULT NULL                 COMMENT '备注',
  `del_flag`     char(1)      DEFAULT '0'                  COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- ----------------------------
-- 5、菜品规格组表
-- ----------------------------
DROP TABLE IF EXISTS `kitchen_dish_spec`;
CREATE TABLE `kitchen_dish_spec` (
  `id`         bigint(20)  NOT NULL AUTO_INCREMENT          COMMENT '规格ID',
  `dish_id`    bigint(20)  NOT NULL                         COMMENT '菜品ID',
  `spec_name`  varchar(32) NOT NULL                         COMMENT '规格名(如辣度)',
  `multiple`   char(1)     DEFAULT '0'                      COMMENT '是否多选(0单选 1多选)',
  `order_num`  int(4)      DEFAULT 0                        COMMENT '显示顺序',
  PRIMARY KEY (`id`),
  KEY `idx_dish` (`dish_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='菜品规格组表';

-- ----------------------------
-- 6、菜品规格值表
-- ----------------------------
DROP TABLE IF EXISTS `kitchen_dish_spec_value`;
CREATE TABLE `kitchen_dish_spec_value` (
  `id`          bigint(20)  NOT NULL AUTO_INCREMENT         COMMENT '规格值ID',
  `spec_id`     bigint(20)  NOT NULL                        COMMENT '规格组ID',
  `spec_value`  varchar(32) NOT NULL                        COMMENT '规格值(如微辣)',
  `order_num`   int(4)      DEFAULT 0                       COMMENT '显示顺序',
  PRIMARY KEY (`id`),
  KEY `idx_spec` (`spec_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='菜品规格值表';

-- ----------------------------
-- 7、菜品做法步骤表
-- ----------------------------
DROP TABLE IF EXISTS `kitchen_dish_step`;
CREATE TABLE `kitchen_dish_step` (
  `id`        bigint(20)    NOT NULL AUTO_INCREMENT         COMMENT '步骤ID',
  `dish_id`   bigint(20)    NOT NULL                        COMMENT '菜品ID',
  `step_no`   int(4)        DEFAULT 1                       COMMENT '步骤序号',
  `image`     varchar(255)  DEFAULT ''                      COMMENT '步骤图',
  `content`   varchar(500)  DEFAULT ''                      COMMENT '步骤说明',
  `timer`     int(11)       DEFAULT 0                       COMMENT '定时器(秒)',
  PRIMARY KEY (`id`),
  KEY `idx_dish` (`dish_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='菜品做法步骤表';

-- ----------------------------
-- 8、厨师表
-- ----------------------------
DROP TABLE IF EXISTS `kitchen_chef`;
CREATE TABLE `kitchen_chef` (
  `id`          bigint(20)    NOT NULL AUTO_INCREMENT       COMMENT '厨师ID',
  `chef_name`   varchar(32)   NOT NULL                      COMMENT '厨师名称',
  `avatar`      varchar(255)  DEFAULT ''                    COMMENT '头像',
  `skill_tag`   varchar(32)   DEFAULT ''                    COMMENT '擅长标签',
  `intro`       varchar(255)  DEFAULT ''                    COMMENT '简介',
  `extra_price` decimal(10,2) DEFAULT 0                     COMMENT '代炒加价',
  `est_time`    varchar(32)   DEFAULT ''                    COMMENT '预计时长',
  `status`      char(1)       DEFAULT '0'                   COMMENT '状态(0正常 1停用)',
  `order_num`   int(4)        DEFAULT 0                     COMMENT '显示顺序',
  `create_by`    varchar(64)  DEFAULT ''                   COMMENT '创建者',
  `create_time`  datetime                                 COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT ''                   COMMENT '更新者',
  `update_time`  datetime                                 COMMENT '更新时间',
  `remark`       varchar(500) DEFAULT NULL                 COMMENT '备注',
  `del_flag`     char(1)      DEFAULT '0'                  COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='厨师表';

-- ----------------------------
-- 9、订单表
-- ----------------------------
DROP TABLE IF EXISTS `kitchen_order`;
CREATE TABLE `kitchen_order` (
  `id`              bigint(20)    NOT NULL AUTO_INCREMENT   COMMENT '订单ID',
  `order_no`        varchar(32)   NOT NULL                  COMMENT '订单号',
  `wx_user_id`      bigint(20)    DEFAULT NULL              COMMENT '小程序用户ID',
  `service_type`    char(1)       DEFAULT '0'               COMMENT '服务方式(0同城配送 1厨师代炒 2店内自提)',
  `chef_id`         bigint(20)    DEFAULT NULL              COMMENT '厨师ID(代炒)',
  `rider_id`        bigint(20)    DEFAULT NULL              COMMENT '配送员ID(同城配送)',
  `receiver_name`   varchar(32)   DEFAULT ''                COMMENT '收货人',
  `receiver_phone`  varchar(20)   DEFAULT ''                COMMENT '收货电话',
  `receiver_address` varchar(255) DEFAULT ''                COMMENT '收货地址',
  `total_count`     int(11)       DEFAULT 0                 COMMENT '商品总件数',
  `total_amount`    decimal(10,2) DEFAULT 0                 COMMENT '订单金额',
  `order_status`    char(1)       DEFAULT '0'               COMMENT '订单状态(0待处理 1已接单 2制作中 3已完成 4已取消)',
  `pay_status`      char(1)       DEFAULT '0'               COMMENT '支付状态(0未支付 1已支付)',
  `share_flag`      char(1)       DEFAULT '0'               COMMENT '是否分享成品(0否 1是)',
  `create_by`    varchar(64)  DEFAULT ''                   COMMENT '创建者',
  `create_time`  datetime                                 COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT ''                   COMMENT '更新者',
  `update_time`  datetime                                 COMMENT '更新时间',
  `remark`       varchar(500) DEFAULT NULL                 COMMENT '备注',
  `del_flag`     char(1)      DEFAULT '0'                  COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_wx_user` (`wx_user_id`),
  KEY `idx_chef` (`chef_id`),
  KEY `idx_rider` (`rider_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- 10、订单明细表
-- ----------------------------
DROP TABLE IF EXISTS `kitchen_order_item`;
CREATE TABLE `kitchen_order_item` (
  `id`         bigint(20)    NOT NULL AUTO_INCREMENT        COMMENT '明细ID',
  `order_id`   bigint(20)    NOT NULL                       COMMENT '订单ID',
  `dish_id`    bigint(20)    DEFAULT NULL                   COMMENT '菜品ID',
  `dish_name`  varchar(64)   DEFAULT ''                     COMMENT '菜品名称(快照)',
  `dish_cover` varchar(255)  DEFAULT ''                     COMMENT '菜品封面(快照)',
  `spec_json`  varchar(500)  DEFAULT ''                     COMMENT '已选规格JSON',
  `quantity`   int(11)       DEFAULT 1                      COMMENT '数量',
  `price`      decimal(10,2) DEFAULT 0                      COMMENT '单价',
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`),
  KEY `idx_dish` (`dish_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ----------------------------
-- 11、分享广场成品表
-- ----------------------------
DROP TABLE IF EXISTS `kitchen_share_post`;
CREATE TABLE `kitchen_share_post` (
  `id`            bigint(20)   NOT NULL AUTO_INCREMENT      COMMENT '动态ID',
  `wx_user_id`    bigint(20)   DEFAULT NULL                 COMMENT '发布用户ID',
  `title`         varchar(64)  DEFAULT ''                   COMMENT '标题',
  `content`       varchar(500) DEFAULT ''                   COMMENT '正文',
  `images`        varchar(1000) DEFAULT ''                  COMMENT '图片(逗号分隔)',
  `tags`          varchar(255) DEFAULT ''                   COMMENT '标签(逗号分隔)',
  `like_count`    int(11)      DEFAULT 0                    COMMENT '点赞数',
  `comment_count` int(11)      DEFAULT 0                    COMMENT '评论数',
  `audit_status`  char(1)      DEFAULT '0'                  COMMENT '审核状态(0待审核 1通过 2驳回)',
  `create_by`    varchar(64)  DEFAULT ''                   COMMENT '创建者',
  `create_time`  datetime                                 COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT ''                   COMMENT '更新者',
  `update_time`  datetime                                 COMMENT '更新时间',
  `remark`       varchar(500) DEFAULT NULL                 COMMENT '备注',
  `del_flag`     char(1)      DEFAULT '0'                  COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (`id`),
  KEY `idx_wx_user` (`wx_user_id`),
  KEY `idx_audit` (`audit_status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='分享广场成品表';

-- ----------------------------
-- 12、配送员表（同城配送）
-- ----------------------------
DROP TABLE IF EXISTS `kitchen_rider`;
CREATE TABLE `kitchen_rider` (
  `id`           bigint(20)    NOT NULL AUTO_INCREMENT      COMMENT '配送员ID',
  `rider_name`   varchar(32)   NOT NULL                     COMMENT '配送员名称',
  `avatar`       varchar(255)  DEFAULT ''                   COMMENT '头像',
  `tag`          varchar(32)   DEFAULT ''                   COMMENT '标签(如准时送达)',
  `intro`        varchar(255)  DEFAULT ''                   COMMENT '简介',
  `delivery_fee` decimal(10,2) DEFAULT 0                    COMMENT '配送费',
  `est_time`     varchar(32)   DEFAULT ''                   COMMENT '预计时长',
  `status`       char(1)       DEFAULT '0'                  COMMENT '状态(0正常 1停用)',
  `order_num`    int(4)        DEFAULT 0                    COMMENT '显示顺序',
  `create_by`    varchar(64)  DEFAULT ''                    COMMENT '创建者',
  `create_time`  datetime                                  COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT ''                    COMMENT '更新者',
  `update_time`  datetime                                  COMMENT '更新时间',
  `remark`       varchar(500) DEFAULT NULL                  COMMENT '备注',
  `del_flag`     char(1)      DEFAULT '0'                   COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='配送员表';

-- ----------------------------
-- 13、成品点赞关系表（点赞去重/可取消）
-- ----------------------------
DROP TABLE IF EXISTS `kitchen_post_like`;
CREATE TABLE `kitchen_post_like` (
  `id`          bigint(20)   NOT NULL AUTO_INCREMENT        COMMENT '主键',
  `post_id`     bigint(20)   NOT NULL                       COMMENT '成品ID',
  `wx_user_id`  bigint(20)   NOT NULL                       COMMENT '点赞用户ID',
  `create_time` datetime                                   COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `wx_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='成品点赞关系表';

-- ----------------------------
-- 14、成品评论表
-- ----------------------------
DROP TABLE IF EXISTS `kitchen_comment`;
CREATE TABLE `kitchen_comment` (
  `id`          bigint(20)   NOT NULL AUTO_INCREMENT        COMMENT '评论ID',
  `post_id`     bigint(20)   NOT NULL                       COMMENT '成品ID',
  `wx_user_id`  bigint(20)   NOT NULL                       COMMENT '评论用户ID',
  `content`     varchar(500) NOT NULL                       COMMENT '评论内容',
  `audit_status` char(1)     DEFAULT '1'                    COMMENT '审核状态(0待审核 1通过 2驳回)',
  `create_by`    varchar(64)  DEFAULT ''                    COMMENT '创建者',
  `create_time`  datetime                                  COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT ''                    COMMENT '更新者',
  `update_time`  datetime                                  COMMENT '更新时间',
  `remark`       varchar(500) DEFAULT NULL                  COMMENT '备注',
  `del_flag`     char(1)      DEFAULT '0'                   COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (`id`),
  KEY `idx_post` (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='成品评论表';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- 菜单 & 权限初始化（menu_id 从 2000 起，避免与若依自带菜单冲突）
-- 幂等：先清理本模块菜单/字典再插入，可安全重复执行
-- ============================================================================
DELETE FROM sys_role_menu WHERE menu_id BETWEEN 2000 AND 2099;
DELETE FROM sys_menu WHERE menu_id BETWEEN 2000 AND 2099;
DELETE FROM sys_dict_data WHERE dict_type IN
  ('kitchen_dish_status','kitchen_difficulty','kitchen_service_type','kitchen_order_status','kitchen_pay_status','kitchen_audit_status');
DELETE FROM sys_dict_type WHERE dict_type IN
  ('kitchen_dish_status','kitchen_difficulty','kitchen_service_type','kitchen_order_status','kitchen_pay_status','kitchen_audit_status');

-- 主目录：私房菜管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2000, '私房菜管理', 0, 5, 'kitchen', NULL, 1, 0, 'M', '0', '0', '', 'cascader', 'admin', sysdate(), '私房菜厨房管理目录');

-- 分类管理 菜单 + 按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2010, '分类管理', 2000, 1, 'category', 'kitchen/category/index', 1, 0, 'C', '0', '0', 'kitchen:category:list', 'list', 'admin', sysdate(), '菜品分类菜单');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2011, '分类查询', 2010, 1, 'F', '0', '0', 'kitchen:category:query',  'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2012, '分类新增', 2010, 2, 'F', '0', '0', 'kitchen:category:add',    'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2013, '分类修改', 2010, 3, 'F', '0', '0', 'kitchen:category:edit',   'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2014, '分类删除', 2010, 4, 'F', '0', '0', 'kitchen:category:remove', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2015, '分类导出', 2010, 5, 'F', '0', '0', 'kitchen:category:export', 'admin', sysdate());

-- 菜品管理 菜单 + 按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2020, '菜品管理', 2000, 2, 'dish', 'kitchen/dish/index', 1, 0, 'C', '0', '0', 'kitchen:dish:list', 'shopping', 'admin', sysdate(), '菜品管理菜单');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2021, '菜品查询', 2020, 1, 'F', '0', '0', 'kitchen:dish:query',  'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2022, '菜品新增', 2020, 2, 'F', '0', '0', 'kitchen:dish:add',    'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2023, '菜品修改', 2020, 3, 'F', '0', '0', 'kitchen:dish:edit',   'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2024, '菜品删除', 2020, 4, 'F', '0', '0', 'kitchen:dish:remove', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2025, '菜品导出', 2020, 5, 'F', '0', '0', 'kitchen:dish:export', 'admin', sysdate());

-- 其余模块菜单（厨师/订单/分享广场/厨房/小程序用户）可按上面同构补充，menu_id 预留 2030~2090

-- ============================================================================
-- 字典类型 & 字典数据
-- ============================================================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) VALUES ('菜品上架状态', 'kitchen_dish_status', '0', 'admin', sysdate(), '菜品上下架状态');
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) VALUES ('菜品难度',     'kitchen_difficulty', '0', 'admin', sysdate(), '菜品制作难度');
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) VALUES ('订单服务方式', 'kitchen_service_type', '0', 'admin', sysdate(), '同城配送/代炒/自提');
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) VALUES ('订单状态',     'kitchen_order_status', '0', 'admin', sysdate(), '订单流转状态');
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) VALUES ('支付状态',     'kitchen_pay_status', '0', 'admin', sysdate(), '支付状态');
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) VALUES ('内容审核状态', 'kitchen_audit_status', '0', 'admin', sysdate(), '分享广场审核状态');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time) VALUES (1, '上架', '1', 'kitchen_dish_status', '', 'success', 'Y', '0', 'admin', sysdate());
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time) VALUES (2, '下架', '0', 'kitchen_dish_status', '', 'danger',  'N', '0', 'admin', sysdate());

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (1, '简单', '简单', 'kitchen_difficulty', 'success', 'N', '0', 'admin', sysdate());
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (2, '中等', '中等', 'kitchen_difficulty', 'warning', 'Y', '0', 'admin', sysdate());
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (3, '困难', '困难', 'kitchen_difficulty', 'danger',  'N', '0', 'admin', sysdate());

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (1, '同城配送', '0', 'kitchen_service_type', 'primary', 'Y', '0', 'admin', sysdate());
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (2, '厨师代炒', '1', 'kitchen_service_type', 'warning', 'N', '0', 'admin', sysdate());
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (3, '店内自提', '2', 'kitchen_service_type', 'info',    'N', '0', 'admin', sysdate());

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (1, '待处理', '0', 'kitchen_order_status', 'info',    'Y', '0', 'admin', sysdate());
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (2, '已接单', '1', 'kitchen_order_status', 'primary', 'N', '0', 'admin', sysdate());
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (3, '制作中', '2', 'kitchen_order_status', 'warning', 'N', '0', 'admin', sysdate());
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (4, '已完成', '3', 'kitchen_order_status', 'success', 'N', '0', 'admin', sysdate());
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (5, '已取消', '4', 'kitchen_order_status', 'danger',  'N', '0', 'admin', sysdate());

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (1, '未支付', '0', 'kitchen_pay_status', 'danger',  'Y', '0', 'admin', sysdate());
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (2, '已支付', '1', 'kitchen_pay_status', 'success', 'N', '0', 'admin', sysdate());

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (1, '待审核', '0', 'kitchen_audit_status', 'info',    'Y', '0', 'admin', sysdate());
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (2, '通过',   '1', 'kitchen_audit_status', 'success', 'N', '0', 'admin', sysdate());
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) VALUES (3, '驳回',   '2', 'kitchen_audit_status', 'danger',  'N', '0', 'admin', sysdate());

-- ============================================================================
-- 其余模块菜单 & 权限（厨师 2030 / 订单 2040 / 分享广场 2050 / 厨房设置 2060 / 小程序用户 2070）
-- ============================================================================
-- 厨师管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2030, '厨师管理', 2000, 3, 'chef', 'kitchen/chef/index', 1, 0, 'C', '0', '0', 'kitchen:chef:list', 'peoples', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2031, '厨师查询', 2030, 1, 'F', '0', '0', 'kitchen:chef:query',  'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2032, '厨师新增', 2030, 2, 'F', '0', '0', 'kitchen:chef:add',    'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2033, '厨师修改', 2030, 3, 'F', '0', '0', 'kitchen:chef:edit',   'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2034, '厨师删除', 2030, 4, 'F', '0', '0', 'kitchen:chef:remove', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2035, '厨师导出', 2030, 5, 'F', '0', '0', 'kitchen:chef:export', 'admin', sysdate());

-- 订单管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2040, '订单管理', 2000, 4, 'order', 'kitchen/order/index', 1, 0, 'C', '0', '0', 'kitchen:order:list', 'form', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2041, '订单查询', 2040, 1, 'F', '0', '0', 'kitchen:order:query',  'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2043, '订单修改', 2040, 3, 'F', '0', '0', 'kitchen:order:edit',   'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2044, '订单删除', 2040, 4, 'F', '0', '0', 'kitchen:order:remove', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2045, '订单导出', 2040, 5, 'F', '0', '0', 'kitchen:order:export', 'admin', sysdate());

-- 分享广场管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2050, '分享广场', 2000, 5, 'sharePost', 'kitchen/sharePost/index', 1, 0, 'C', '0', '0', 'kitchen:sharePost:list', 'message', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2051, '动态查询', 2050, 1, 'F', '0', '0', 'kitchen:sharePost:query',  'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2053, '动态修改', 2050, 3, 'F', '0', '0', 'kitchen:sharePost:edit',   'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2054, '动态删除', 2050, 4, 'F', '0', '0', 'kitchen:sharePost:remove', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2055, '动态导出', 2050, 5, 'F', '0', '0', 'kitchen:sharePost:export', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2056, '内容审核', 2050, 6, 'F', '0', '0', 'kitchen:sharePost:audit',  'admin', sysdate());

-- 厨房设置
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2060, '厨房设置', 2000, 6, 'shop', 'kitchen/shop/index', 1, 0, 'C', '0', '0', 'kitchen:shop:query', 'edit', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2061, '设置保存', 2060, 1, 'F', '0', '0', 'kitchen:shop:edit', 'admin', sysdate());

-- 小程序用户
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2070, '小程序用户', 2000, 7, 'wxUser', 'kitchen/wxUser/index', 1, 0, 'C', '0', '0', 'kitchen:wxUser:list', 'user', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2071, '用户查询', 2070, 1, 'F', '0', '0', 'kitchen:wxUser:query',  'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2073, '用户修改', 2070, 3, 'F', '0', '0', 'kitchen:wxUser:edit',   'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2074, '用户删除', 2070, 4, 'F', '0', '0', 'kitchen:wxUser:remove', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2075, '用户导出', 2070, 5, 'F', '0', '0', 'kitchen:wxUser:export', 'admin', sysdate());

-- 配送员管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2080, '配送员管理', 2000, 8, 'rider', 'kitchen/rider/index', 1, 0, 'C', '0', '0', 'kitchen:rider:list', 'guide', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2081, '配送员查询', 2080, 1, 'F', '0', '0', 'kitchen:rider:query',  'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2082, '配送员新增', 2080, 2, 'F', '0', '0', 'kitchen:rider:add',    'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2083, '配送员修改', 2080, 3, 'F', '0', '0', 'kitchen:rider:edit',   'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2084, '配送员删除', 2080, 4, 'F', '0', '0', 'kitchen:rider:remove', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2085, '配送员导出', 2080, 5, 'F', '0', '0', 'kitchen:rider:export', 'admin', sysdate());

-- 评论管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2090, '评论管理', 2000, 9, 'comment', 'kitchen/comment/index', 1, 0, 'C', '0', '0', 'kitchen:comment:list', 'education', 'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2091, '评论查询', 2090, 1, 'F', '0', '0', 'kitchen:comment:query',  'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2093, '评论修改', 2090, 3, 'F', '0', '0', 'kitchen:comment:edit',   'admin', sysdate());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time) VALUES (2094, '评论删除', 2090, 4, 'F', '0', '0', 'kitchen:comment:remove', 'admin', sysdate());

-- 初始化一条厨房配置默认数据
INSERT INTO kitchen_shop (id, shop_name, subtitle, store_name, store_address, business_hours, store_phone, create_by, create_time, remark)
VALUES (1, '馆家私厨', '世间万物，唯有美食不可辜负', '馆家私厨', '广东省广州市天河区华夏路 10 号 1 层 102 铺', '10:00 - 21:00', '020-8888 6666', 'admin', sysdate(), '默认厨房配置');
