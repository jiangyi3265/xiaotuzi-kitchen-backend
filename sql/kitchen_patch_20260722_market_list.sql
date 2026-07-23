-- 多菜市场及每个市场独立备货群；旧单市场配置自动迁移为第一条。
CREATE TABLE IF NOT EXISTS kitchen_market (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜市场ID',
  shop_id bigint(20) NOT NULL COMMENT '厨房ID',
  market_name varchar(100) NOT NULL COMMENT '菜市场名称',
  market_address varchar(255) NOT NULL COMMENT '菜市场地址',
  business_hours varchar(100) DEFAULT '' COMMENT '营业时间',
  phone varchar(30) DEFAULT '' COMMENT '联系电话',
  latitude decimal(10,7) DEFAULT NULL COMMENT '纬度(GCJ-02)',
  longitude decimal(10,7) DEFAULT NULL COMMENT '经度(GCJ-02)',
  stock_group_qr varchar(255) DEFAULT '' COMMENT '备货群二维码',
  stock_group_name varchar(100) DEFAULT '' COMMENT '备货群名称',
  stock_group_notice varchar(500) DEFAULT '' COMMENT '备货群说明',
  order_num int(4) DEFAULT 0 COMMENT '显示顺序',
  status char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  create_time datetime,
  update_time datetime,
  PRIMARY KEY (id),
  KEY idx_market_shop_status (shop_id,status,order_num)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜市场及备货群配置表';

INSERT INTO kitchen_market
  (shop_id, market_name, market_address, business_hours, phone, stock_group_qr, stock_group_name, stock_group_notice, order_num, status, create_time)
SELECT
  s.id, s.store_name, s.store_address, s.business_hours, s.store_phone,
  s.stock_group_qr, s.stock_group_name, s.stock_group_notice, 0, '0', SYSDATE()
FROM kitchen_shop s
WHERE s.del_flag = '0'
  AND (TRIM(COALESCE(s.store_name, '')) <> '' OR TRIM(COALESCE(s.store_address, '')) <> '')
  AND NOT EXISTS (SELECT 1 FROM kitchen_market m WHERE m.shop_id = s.id);
