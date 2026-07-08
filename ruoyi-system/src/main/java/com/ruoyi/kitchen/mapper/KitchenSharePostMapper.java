package com.ruoyi.kitchen.mapper;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenSharePost;

/**
 * 分享广场Mapper接口
 *
 * @author ruoyi
 */
public interface KitchenSharePostMapper
{
    public KitchenSharePost selectKitchenSharePostById(Long id);

    public List<KitchenSharePost> selectKitchenSharePostList(KitchenSharePost kitchenSharePost);

    public int insertKitchenSharePost(KitchenSharePost kitchenSharePost);

    public int updateKitchenSharePost(KitchenSharePost kitchenSharePost);

    public int deleteKitchenSharePostById(Long id);

    public int deleteKitchenSharePostByIds(Long[] ids);

    /** 点赞 +1 */
    public int addLike(Long id);

    /** 点赞 -1（不低于 0） */
    public int decLike(Long id);
}
