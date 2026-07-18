package com.ruoyi.kitchen.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.kitchen.domain.KitchenSharePost;
import com.ruoyi.kitchen.mapper.KitchenPostLikeMapper;
import com.ruoyi.kitchen.mapper.KitchenSharePostMapper;
import com.ruoyi.kitchen.service.IKitchenSharePostService;

/**
 * 分享广场Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class KitchenSharePostServiceImpl implements IKitchenSharePostService
{
    @Autowired
    private KitchenSharePostMapper kitchenSharePostMapper;

    @Autowired
    private KitchenPostLikeMapper kitchenPostLikeMapper;

    @Override
    public KitchenSharePost selectKitchenSharePostById(Long id)
    {
        return kitchenSharePostMapper.selectKitchenSharePostById(id);
    }

    @Override
    public List<KitchenSharePost> selectKitchenSharePostList(KitchenSharePost kitchenSharePost)
    {
        return kitchenSharePostMapper.selectKitchenSharePostList(kitchenSharePost);
    }

    @Override
    public int insertKitchenSharePost(KitchenSharePost kitchenSharePost)
    {
        return kitchenSharePostMapper.insertKitchenSharePost(kitchenSharePost);
    }

    @Override
    public int updateKitchenSharePost(KitchenSharePost kitchenSharePost)
    {
        return kitchenSharePostMapper.updateKitchenSharePost(kitchenSharePost);
    }

    @Override
    public int deleteKitchenSharePostByIds(Long[] ids)
    {
        return kitchenSharePostMapper.deleteKitchenSharePostByIds(ids);
    }

    @Override
    public int deleteKitchenSharePostById(Long id)
    {
        return kitchenSharePostMapper.deleteKitchenSharePostById(id);
    }

    @Override
    public int audit(Long id, String auditStatus)
    {
        KitchenSharePost post = new KitchenSharePost();
        post.setId(id);
        post.setAuditStatus(auditStatus);
        return kitchenSharePostMapper.updateKitchenSharePost(post);
    }

    @Override
    public int addLike(Long id)
    {
        return kitchenSharePostMapper.addLike(id);
    }

    /**
     * 切换点赞：已赞则取消(-1)，未赞则点赞(+1)。返回操作后的点赞状态。
     * 基于 kitchen_post_like 唯一键去重，杜绝无限刷赞。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleLike(Long postId, Long wxUserId)
    {
        if (postId == null || wxUserId == null)
        {
            throw new ServiceException("点赞参数不完整");
        }
        // 同一帖子上的点赞切换统一串行化；同时确保帖子仍真实存在且已经公开。
        if (kitchenSharePostMapper.lockPublishedPost(postId) == null)
        {
            throw new ServiceException("动态不存在或尚未公开");
        }
        boolean liked = kitchenPostLikeMapper.countLike(postId, wxUserId) > 0;
        if (liked)
        {
            if (kitchenPostLikeMapper.deleteLike(postId, wxUserId) <= 0
                    || kitchenSharePostMapper.decLike(postId) <= 0)
            {
                throw new ServiceException("取消点赞失败，请稍后重试");
            }
            return false;
        }
        else
        {
            if (kitchenPostLikeMapper.insertLike(postId, wxUserId) <= 0
                    || kitchenSharePostMapper.addLike(postId) <= 0)
            {
                throw new ServiceException("点赞失败，请稍后重试");
            }
            return true;
        }
    }
}
