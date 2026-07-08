package com.ruoyi.kitchen.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 成品点赞关系 Mapper（点赞去重、可取消）
 *
 * @author ruoyi
 */
public interface KitchenPostLikeMapper
{
    /** 是否已点赞 */
    public int countLike(@Param("postId") Long postId, @Param("wxUserId") Long wxUserId);

    /** 新增点赞关系 */
    public int insertLike(@Param("postId") Long postId, @Param("wxUserId") Long wxUserId);

    /** 取消点赞关系 */
    public int deleteLike(@Param("postId") Long postId, @Param("wxUserId") Long wxUserId);
}
