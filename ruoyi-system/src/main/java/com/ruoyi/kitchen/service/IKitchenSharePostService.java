package com.ruoyi.kitchen.service;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenSharePost;

/**
 * 分享广场Service接口
 *
 * @author ruoyi
 */
public interface IKitchenSharePostService
{
    public KitchenSharePost selectKitchenSharePostById(Long id);

    public List<KitchenSharePost> selectKitchenSharePostList(KitchenSharePost kitchenSharePost);

    public int insertKitchenSharePost(KitchenSharePost kitchenSharePost);

    public int updateKitchenSharePost(KitchenSharePost kitchenSharePost);

    public int deleteKitchenSharePostByIds(Long[] ids);

    public int deleteKitchenSharePostById(Long id);

    /** 审核 */
    public int audit(Long id, String auditStatus);

    /** 点赞 */
    public int addLike(Long id);

    /** 切换点赞（已赞取消/未赞点赞），返回操作后的点赞状态 */
    public boolean toggleLike(Long postId, Long wxUserId);
}
