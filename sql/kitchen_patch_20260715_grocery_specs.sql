-- Store the user-confirmed grocery quantities/specifications on each order.
-- Safe to run more than once.
SET @has_grocery_json := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'kitchen_order'
    AND COLUMN_NAME = 'grocery_json'
);

SET @grocery_sql := IF(
  @has_grocery_json = 0,
  'ALTER TABLE `kitchen_order` ADD COLUMN `grocery_json` text DEFAULT NULL COMMENT ''用户确认后的采购清单JSON'' AFTER `share_flag`',
  'SELECT ''kitchen_order.grocery_json already exists'' AS message'
);

PREPARE grocery_stmt FROM @grocery_sql;
EXECUTE grocery_stmt;
DEALLOCATE PREPARE grocery_stmt;
