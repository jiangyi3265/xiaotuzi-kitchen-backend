package com.ruoyi.kitchen.service.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
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
    @Transactional(rollbackFor = Exception.class)
    public int insertKitchenComment(KitchenComment kitchenComment)
    {
        int rows = kitchenCommentMapper.insertKitchenComment(kitchenComment);
        if (rows > 0 && kitchenComment.getPostId() != null)
        {
            kitchenCommentMapper.syncCommentCount(kitchenComment.getPostId());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateKitchenComment(KitchenComment kitchenComment)
    {
        KitchenComment before = kitchenComment == null || kitchenComment.getId() == null
                ? null : kitchenCommentMapper.selectKitchenCommentById(kitchenComment.getId());
        if (before == null)
        {
            throw new ServiceException("评论不存在或已删除");
        }
        if (kitchenComment.getPostId() != null && !kitchenComment.getPostId().equals(before.getPostId()))
        {
            throw new ServiceException("不允许变更评论所属动态");
        }
        int rows = kitchenCommentMapper.updateKitchenComment(kitchenComment);
        if (rows > 0 && before.getPostId() != null)
        {
            kitchenCommentMapper.syncCommentCount(before.getPostId());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteKitchenCommentByIds(Long[] ids)
    {
        if (ids == null || ids.length == 0)
        {
            return 0;
        }
        Set<Long> postIds = new LinkedHashSet<>();
        for (Long id : ids)
        {
            KitchenComment comment = kitchenCommentMapper.selectKitchenCommentById(id);
            if (comment != null && comment.getPostId() != null)
            {
                postIds.add(comment.getPostId());
            }
        }
        int rows = kitchenCommentMapper.deleteKitchenCommentByIds(ids);
        if (rows > 0)
        {
            for (Long postId : postIds)
            {
                kitchenCommentMapper.syncCommentCount(postId);
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteKitchenCommentById(Long id)
    {
        KitchenComment before = kitchenCommentMapper.selectKitchenCommentById(id);
        if (before == null)
        {
            return 0;
        }
        int rows = kitchenCommentMapper.deleteKitchenCommentById(id);
        if (rows > 0 && before.getPostId() != null)
        {
            kitchenCommentMapper.syncCommentCount(before.getPostId());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int audit(Long id, String auditStatus)
    {
        if (!"1".equals(auditStatus) && !"2".equals(auditStatus))
        {
            throw new ServiceException("审核状态只能为通过或驳回");
        }
        KitchenComment comment = new KitchenComment();
        comment.setId(id);
        comment.setAuditStatus(auditStatus);
        return updateKitchenComment(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addComment(KitchenComment kitchenComment)
    {
        int rows = kitchenCommentMapper.insertKitchenComment(kitchenComment);
        // 按实际已审核、未删除评论重算帖子评论数
        if (rows > 0 && kitchenComment.getPostId() != null)
        {
            kitchenCommentMapper.syncCommentCount(kitchenComment.getPostId());
        }
        return rows;
    }
}
