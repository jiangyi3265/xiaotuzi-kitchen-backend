-- 分类展示栏目由后台维护；同一栏目下的顶级分类会自动显示在小程序中。
SET @has_display_area = (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'kitchen_category'
    AND column_name = 'display_area'
);
SET @sql = IF(
  @has_display_area = 0,
  'ALTER TABLE kitchen_category ADD COLUMN display_area varchar(32) DEFAULT ''私房菜'' COMMENT ''小程序顶部展示栏目'' AFTER image',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE kitchen_category
  MODIFY COLUMN display_area varchar(32) DEFAULT '私房菜' COMMENT '小程序顶部展示栏目';

UPDATE kitchen_category SET display_area = '私房菜' WHERE display_area IS NULL OR TRIM(display_area) = '' OR display_area = '0';
UPDATE kitchen_category SET display_area = '其他' WHERE display_area = '1';

ALTER TABLE kitchen_dish
  MODIFY COLUMN today_type varchar(32) DEFAULT NULL COMMENT '其他区域分类ID';

INSERT INTO kitchen_category
  (parent_id, ancestors, cat_name, image, display_area, cat_level, order_num, status, create_by, create_time, del_flag)
SELECT 0, '0', '火锅类', '', '其他', 1, 1, '0', 'system', SYSDATE(), '0'
WHERE @has_display_area = 0 AND NOT EXISTS (
  SELECT 1 FROM kitchen_category
  WHERE cat_name = '火锅类' AND display_area = '其他' AND parent_id = 0 AND del_flag = '0'
);

INSERT INTO kitchen_category
  (parent_id, ancestors, cat_name, image, display_area, cat_level, order_num, status, create_by, create_time, del_flag)
SELECT 0, '0', '烧烤', '', '其他', 1, 2, '0', 'system', SYSDATE(), '0'
WHERE @has_display_area = 0 AND NOT EXISTS (
  SELECT 1 FROM kitchen_category
  WHERE cat_name = '烧烤' AND display_area = '其他' AND parent_id = 0 AND del_flag = '0'
);

SET @hotpot_id = (
  SELECT id FROM kitchen_category
  WHERE cat_name = '火锅类' AND display_area = '其他' AND parent_id = 0 AND del_flag = '0'
  ORDER BY id LIMIT 1
);
SET @barbecue_id = (
  SELECT id FROM kitchen_category
  WHERE cat_name = '烧烤' AND display_area = '其他' AND parent_id = 0 AND del_flag = '0'
  ORDER BY id LIMIT 1
);

UPDATE kitchen_dish SET today_type = CAST(@hotpot_id AS CHAR) WHERE today_type = 'hotpot';
UPDATE kitchen_dish SET today_type = CAST(@barbecue_id AS CHAR) WHERE today_type = 'barbecue';
