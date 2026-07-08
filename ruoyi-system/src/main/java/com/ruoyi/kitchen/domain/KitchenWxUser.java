package com.ruoyi.kitchen.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 小程序用户对象 kitchen_wx_user
 *
 * @author ruoyi
 */
public class KitchenWxUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long id;

    /** 微信openid */
    @Excel(name = "openid")
    private String openid;

    /** 微信unionid */
    private String unionId;

    /** 昵称 */
    @Excel(name = "昵称")
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 用户编码 */
    @Excel(name = "用户编码")
    private String userCode;

    /** 手机号 */
    @Excel(name = "手机号")
    private String phone;

    /** 性别(0未知 1男 2女) */
    @Excel(name = "性别", readConverterExp = "0=未知,1=男,2=女")
    private String gender;

    /** 胡萝卜积分 */
    @Excel(name = "积分")
    private Integer carrot;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 是否店主(0否 1是)：仅店主可在小程序端管理菜品/分类/厨房设置 */
    @Excel(name = "店主", readConverterExp = "0=否,1=是")
    private String isOwner;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIsOwner() { return isOwner; }
    public void setIsOwner(String isOwner) { this.isOwner = isOwner; }

    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }

    public String getUnionId() { return unionId; }
    public void setUnionId(String unionId) { this.unionId = unionId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getUserCode() { return userCode; }
    public void setUserCode(String userCode) { this.userCode = userCode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Integer getCarrot() { return carrot; }
    public void setCarrot(Integer carrot) { this.carrot = carrot; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
