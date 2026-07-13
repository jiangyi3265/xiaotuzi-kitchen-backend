package com.ruoyi.kitchen.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class KitchenFeedback extends BaseEntity {
    private Long id;
    private Long wxUserId;
    private String userNickname;
    private String feedbackType;
    private String content;
    private String contact;
    private String images;
    private String handleStatus;
    private String reply;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWxUserId() { return wxUserId; }
    public void setWxUserId(Long wxUserId) { this.wxUserId = wxUserId; }
    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
    public String getFeedbackType() { return feedbackType; }
    public void setFeedbackType(String feedbackType) { this.feedbackType = feedbackType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public String getHandleStatus() { return handleStatus; }
    public void setHandleStatus(String handleStatus) { this.handleStatus = handleStatus; }
    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
}
