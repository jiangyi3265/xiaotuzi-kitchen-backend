package com.ruoyi.kitchen.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 厨房/店铺设置对象 kitchen_shop
 *
 * @author ruoyi
 */
public class KitchenShop extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 厨房ID */
    private Long id;

    /** 厨房名称 */
    private String shopName;

    /** 厨房头像 */
    private String avatar;

    /** 背景图 */
    private String banner;

    /** 副标题/口号 */
    private String subtitle;

    /** 公众号二维码 */
    private String officialAccountQr;

    /** 邀请封面 */
    private String inviteCover;

    /** 邀请文案 */
    private String inviteText;

    /** 微信收款码 */
    private String wechatQr;

    /** 支付宝收款码 */
    private String alipayQr;

    /** 门店名称 */
    private String storeName;

    /** 门店地址 */
    private String storeAddress;

    /** 营业时间 */
    private String businessHours;

    /** 门店电话 */
    private String storePhone;

    /** 商家提前备货群二维码 */
    private String stockGroupQr;

    /** 商家提前备货群名称 */
    private String stockGroupName;

    /** 商家提前备货群说明 */
    private String stockGroupNotice;

    /** 公告开关(0关 1开) */
    private String announceEnabled;

    /** 公告标题 */
    private String announceTitle;

    /** 公告内容 */
    private String announceContent;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAnnounceEnabled() { return announceEnabled; }
    public void setAnnounceEnabled(String announceEnabled) { this.announceEnabled = announceEnabled; }

    public String getAnnounceTitle() { return announceTitle; }
    public void setAnnounceTitle(String announceTitle) { this.announceTitle = announceTitle; }

    public String getAnnounceContent() { return announceContent; }
    public void setAnnounceContent(String announceContent) { this.announceContent = announceContent; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getBanner() { return banner; }
    public void setBanner(String banner) { this.banner = banner; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getOfficialAccountQr() { return officialAccountQr; }
    public void setOfficialAccountQr(String officialAccountQr) { this.officialAccountQr = officialAccountQr; }

    public String getInviteCover() { return inviteCover; }
    public void setInviteCover(String inviteCover) { this.inviteCover = inviteCover; }

    public String getInviteText() { return inviteText; }
    public void setInviteText(String inviteText) { this.inviteText = inviteText; }

    public String getWechatQr() { return wechatQr; }
    public void setWechatQr(String wechatQr) { this.wechatQr = wechatQr; }

    public String getAlipayQr() { return alipayQr; }
    public void setAlipayQr(String alipayQr) { this.alipayQr = alipayQr; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getStoreAddress() { return storeAddress; }
    public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }

    public String getBusinessHours() { return businessHours; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }

    public String getStorePhone() { return storePhone; }
    public void setStorePhone(String storePhone) { this.storePhone = storePhone; }
    public String getStockGroupQr() { return stockGroupQr; }
    public void setStockGroupQr(String stockGroupQr) { this.stockGroupQr = stockGroupQr; }
    public String getStockGroupName() { return stockGroupName; }
    public void setStockGroupName(String stockGroupName) { this.stockGroupName = stockGroupName; }
    public String getStockGroupNotice() { return stockGroupNotice; }
    public void setStockGroupNotice(String stockGroupNotice) { this.stockGroupNotice = stockGroupNotice; }
}
