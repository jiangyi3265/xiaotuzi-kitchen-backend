package com.ruoyi.kitchen.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        boolean liked = kitchenPostLikeMapper.countLike(postId, wxUserId) > 0;
        if (liked)
        {
            kitchenPostLikeMapper.deleteLike(postId, wxUserId);
            kitchenSharePostMapper.decLike(postId);
            return false;
        }
        else
        {
            kitchenPostLikeMapper.insertLike(postId, wxUserId);
            kitchenSharePostMapper.addLike(postId);
            return true;
        }
    }
}
