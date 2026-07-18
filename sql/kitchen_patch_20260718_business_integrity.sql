-- Repair category visibility for every active dish and rebuild share counters.
-- This migration is idempotent and safe to run on every deployment.

DROP TEMPORARY TABLE IF EXISTS tmp_active_dish_category;
CREATE TEMPORARY TABLE tmp_active_dish_category (
  id bigint(20) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=MEMORY;

INSERT IGNORE INTO tmp_active_dish_category (id)
SELECT DISTINCT d.category_id
FROM kitchen_dish d
WHERE d.del_flag = '0' AND d.status = '1' AND d.category_id IS NOT NULL;

INSERT IGNORE INTO tmp_active_dish_category (id)
SELECT DISTINCT c.parent_id
FROM kitchen_dish d
JOIN kitchen_category c ON c.id = d.category_id
WHERE d.del_flag = '0' AND d.status = '1' AND COALESCE(c.parent_id, 0) <> 0;

INSERT IGNORE INTO tmp_active_dish_category (id)
SELECT DISTINCT p.parent_id
FROM kitchen_dish d
JOIN kitchen_category c ON c.id = d.category_id
JOIN kitchen_category p ON p.id = c.parent_id
WHERE d.del_flag = '0' AND d.status = '1' AND COALESCE(p.parent_id, 0) <> 0;

UPDATE kitchen_category c
JOIN tmp_active_dish_category t ON t.id = c.id
SET c.status = '0', c.del_flag = '0';

UPDATE kitchen_category c
JOIN tmp_active_dish_category t ON t.id = c.id
LEFT JOIN kitchen_category p ON p.id = c.parent_id
LEFT JOIN kitchen_category gp ON gp.id = p.parent_id
SET c.ancestors = CASE
      WHEN COALESCE(c.parent_id, 0) = 0 THEN '0'
      WHEN p.id IS NOT NULL AND COALESCE(p.parent_id, 0) = 0 THEN CONCAT('0,', p.id)
      WHEN gp.id IS NOT NULL AND COALESCE(gp.parent_id, 0) = 0 THEN CONCAT('0,', gp.id, ',', p.id)
      ELSE c.ancestors
    END,
    c.cat_level = CASE
      WHEN COALESCE(c.parent_id, 0) = 0 THEN 1
      WHEN p.id IS NOT NULL AND COALESCE(p.parent_id, 0) = 0 THEN 2
      WHEN gp.id IS NOT NULL AND COALESCE(gp.parent_id, 0) = 0 THEN 3
      ELSE c.cat_level
    END;

DROP TEMPORARY TABLE IF EXISTS tmp_active_dish_category;

UPDATE kitchen_share_post p
SET like_count = (
      SELECT COUNT(1) FROM kitchen_post_like l WHERE l.post_id = p.id
    ),
    comment_count = (
      SELECT COUNT(1) FROM kitchen_comment c
      WHERE c.post_id = p.id AND c.audit_status = '1' AND c.del_flag = '0'
    );
