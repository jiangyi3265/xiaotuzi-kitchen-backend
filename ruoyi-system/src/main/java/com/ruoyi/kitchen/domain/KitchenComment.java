package com.ruoyi.kitchen.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 成品评论对象 kitchen_comment
 *
 * @author ruoyi
 */
public class KitchenComment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评论ID */
    private Long id;

    /** 所属动态ID */
    @Excel(name = "动态ID")
    private Long postId;

    /** 评论用户ID */
    private Long wxUserId;

    /** 用户昵称（非DB字段） */
    @Excel(name = "评论用户")
    private String userNickname;

    /** 用户头像（非DB字段） */
    private String userAvatar;

    /** 评论内容 */
    @Excel(name = "评论内容")
    private String content;

    /** 审核状态(0待审核 1通过 2驳回) */
    @Excel(name = "审核状态", readConverterExp = "0=待审核,1=通过,2=驳回")
    private String auditStatus;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getWxUserId() { return wxUserId; }
    public void setWxUserId(Long wxUserId) { this.wxUserId = wxUserId; }

    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }
}
