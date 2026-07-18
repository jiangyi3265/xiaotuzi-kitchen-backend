package com.ruoyi.kitchen.mapper;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenComment;

/**
 * 成品评论Mapper接口
 *
 * @author ruoyi
 */
public interface KitchenCommentMapper
{
    public KitchenComment selectKitchenCommentById(Long id);

    public List<KitchenComment> selectKitchenCommentList(KitchenComment kitchenComment);

    public int insertKitchenComment(KitchenComment kitchenComment);

    public int updateKitchenComment(KitchenComment kitchenComment);

    public int deleteKitchenCommentById(Long id);

    public int deleteKitchenCommentByIds(Long[] ids);

    /** 统计某帖通过审核的评论数（用于回填 comment_count） */
    public int selectCountByPost(Long postId);

    /** 按实际已审核、未删除评论重算帖子评论数 */
    public int syncCommentCount(Long postId);
}
