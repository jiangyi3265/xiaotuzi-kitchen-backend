package com.ruoyi.kitchen.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 分享广场成品对象 kitchen_share_post
 *
 * @author ruoyi
 */
public class KitchenSharePost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 动态ID */
    private Long id;

    /** 发布用户ID */
    private Long wxUserId;

    /** 用户昵称（非DB字段） */
    @Excel(name = "发布用户")
    private String userNickname;

    /** 用户头像（非DB字段） */
    private String userAvatar;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 正文 */
    @Excel(name = "正文")
    private String content;

    /** 图片(逗号分隔) */
    private String images;

    /** 标签(逗号分隔) */
    @Excel(name = "标签")
    private String tags;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Integer likeCount;

    /** 评论数 */
    @Excel(name = "评论数")
    private Integer commentCount;

    /** 审核状态(0待审核 1通过 2驳回) */
    @Excel(name = "审核状态", readConverterExp = "0=待审核,1=通过,2=驳回")
    private String auditStatus;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWxUserId() { return wxUserId; }
    public void setWxUserId(Long wxUserId) { this.wxUserId = wxUserId; }

    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }
}
