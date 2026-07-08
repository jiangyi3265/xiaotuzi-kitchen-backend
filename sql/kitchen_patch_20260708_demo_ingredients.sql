-- Fill demo dish ingredients for existing deployments. Safe to run more than once.
SET NAMES utf8mb4;

UPDATE `kitchen_dish`
SET `ingredients` = '五花肉、冰糖、葱、姜、八角、生抽、老抽、料酒'
WHERE `id` = 1 AND (`ingredients` IS NULL OR `ingredients` = '');

UPDATE `kitchen_dish`
SET `ingredients` = '猪脚、冰糖、葱、姜、料酒、八角、桂皮、生抽、老抽'
WHERE `id` = 2 AND (`ingredients` IS NULL OR `ingredients` = '');

UPDATE `kitchen_dish`
SET `ingredients` = '青菜、蒜、盐、食用油'
WHERE `id` = 3 AND (`ingredients` IS NULL OR `ingredients` = '');

UPDATE `kitchen_dish`
SET `ingredients` = '番茄、鸡蛋、葱花、盐、糖、食用油'
WHERE `id` = 4 AND (`ingredients` IS NULL OR `ingredients` = '');

UPDATE `kitchen_dish`
SET `ingredients` = '紫菜、鸡蛋、虾皮、葱花、盐、香油'
WHERE `id` = 5 AND (`ingredients` IS NULL OR `ingredients` = '');

UPDATE `kitchen_dish`
SET `ingredients` = '鸡蛋、面粉、葱花、盐、清水、食用油'
WHERE `id` = 6 AND (`ingredients` IS NULL OR `ingredients` = '');
