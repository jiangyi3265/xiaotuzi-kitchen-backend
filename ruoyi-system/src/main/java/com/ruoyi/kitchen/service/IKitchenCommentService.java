package com.ruoyi.kitchen.service;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenComment;

/**
 * 成品评论Service接口
 *
 * @author ruoyi
 */
public interface IKitchenCommentService
{
    public KitchenComment selectKitchenCommentById(Long id);

    public List<KitchenComment> selectKitchenCommentList(KitchenComment kitchenComment);

    public int insertKitchenComment(KitchenComment kitchenComment);

    public int updateKitchenComment(KitchenComment kitchenComment);

    public int deleteKitchenCommentByIds(Long[] ids);

    public int deleteKitchenCommentById(Long id);

    /** 审核 */
    public int audit(Long id, String auditStatus);

    /** 新增评论（小程序端）：插入评论，若审核通过则回填帖子评论数 +1 */
    public int addComment(KitchenComment kitchenComment);
}
