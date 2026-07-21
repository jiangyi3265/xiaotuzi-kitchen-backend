-- 分享大厅默认免审核后，放行历史待审核内容。
-- 仅处理待审核状态；已驳回、已删除内容保持不变。脚本可重复执行。
UPDATE kitchen_share_post
SET audit_status = '1'
WHERE audit_status = '0' AND del_flag = '0';

UPDATE kitchen_comment
SET audit_status = '1'
WHERE audit_status = '0' AND del_flag = '0';
