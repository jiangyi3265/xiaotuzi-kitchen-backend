package com.ruoyi.kitchen.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.kitchen.domain.KitchenComment;
import com.ruoyi.kitchen.mapper.KitchenCommentMapper;
import com.ruoyi.kitchen.service.IKitchenCommentService;

/**
 * 成品评论Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class KitchenCommentServiceImpl implements IKitchenCommentService
{
    @Autowired
    private KitchenCommentMapper kitchenCommentMapper;

    @Override
    public KitchenComment selectKitchenCommentById(Long id)
    {
        return kitchenCommentMapper.selectKitchenCommentById(id);
    }

    @Override
    public List<KitchenComment> selectKitchenCommentList(KitchenComment kitchenComment)
    {
        return kitchenCommentMapper.selectKitchenCommentList(kitchenComment);
    }

    @Override
    public int insertKitchenComment(KitchenComment kitchenComment)
    {
        return kitchenCommentMapper.insertKitchenComment(kitchenComment);
    }

    @Override
    public int updateKitchenComment(KitchenComment kitchenComment)
    {
        return kitchenCommentMapper.updateKitchenComment(kitchenComment);
    }

    @Override
    public int deleteKitchenCommentByIds(Long[] ids)
    {
        return kitchenCommentMapper.deleteKitchenCommentByIds(ids);
    }

    @Override
    public int deleteKitchenCommentById(Long id)
    {
        return kitchenCommentMapper.deleteKitchenCommentById(id);
    }

    @Override
    public int audit(Long id, String auditStatus)
    {
        KitchenComment comment = new KitchenComment();
        comment.setId(id);
        comment.setAuditStatus(auditStatus);
        return kitchenCommentMapper.updateKitchenComment(comment);
    }

    @Override
    @Transactional
    public int addComment(KitchenComment kitchenComment)
    {
        int rows = kitchenCommentMapper.insertKitchenComment(kitchenComment);
        // 审核通过的评论，回填帖子评论数 +1
        if (rows > 0 && "1".equals(kitchenComment.getAuditStatus()) && kitchenComment.getPostId() != null)
        {
            kitchenCommentMapper.increaseCommentCount(kitchenComment.getPostId());
        }
        return rows;
    }
}
