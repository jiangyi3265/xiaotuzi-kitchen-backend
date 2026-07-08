package com.ruoyi.kitchen.domain;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 厨师对象 kitchen_chef
 *
 * @author ruoyi
 */
public class KitchenChef extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 厨师ID */
    private Long id;

    /** 厨师名称 */
    @Excel(name = "厨师名称")
    private String chefName;

    /** 头像 */
    private String avatar;

    /** 擅长标签 */
    @Excel(name = "擅长标签")
    private String skillTag;

    /** 简介 */
    private String intro;

    /** 代炒加价 */
    @Excel(name = "代炒加价")
    private BigDecimal extraPrice;

    /** 预计时长 */
    @Excel(name = "预计时长")
    private String estTime;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 显示顺序 */
    private Integer orderNum;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getChefName() { return chefName; }
    public void setChefName(String chefName) { this.chefName = chefName; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getSkillTag() { return skillTag; }
    public void setSkillTag(String skillTag) { this.skillTag = skillTag; }

    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }

    public BigDecimal getExtraPrice() { return extraPrice; }
    public void setExtraPrice(BigDecimal extraPrice) { this.extraPrice = extraPrice; }

    public String getEstTime() { return estTime; }
    public void setEstTime(String estTime) { this.estTime = estTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }
}
