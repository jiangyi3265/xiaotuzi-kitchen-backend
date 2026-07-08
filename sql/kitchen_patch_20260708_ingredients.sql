-- Add dish ingredients for existing deployments. Safe to run more than once.
SET NAMES utf8mb4;

SET @has_ingredients := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'kitchen_dish'
    AND COLUMN_NAME = 'ingredients'
);

SET @ddl := IF(
  @has_ingredients = 0,
  'ALTER TABLE `kitchen_dish` ADD COLUMN `ingredients` varchar(1000) DEFAULT '''' COMMENT ''所用用料'' AFTER `story`',
  'SELECT ''kitchen_dish.ingredients already exists'' AS message'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
