package com.ruoyi.kitchen.domain;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 配送员对象 kitchen_rider
 *
 * @author ruoyi
 */
public class KitchenRider extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配送员ID */
    private Long id;

    /** 配送员名称 */
    @Excel(name = "配送员名称")
    private String riderName;

    /** 头像 */
    private String avatar;

    /** 标签 */
    @Excel(name = "标签")
    private String tag;

    /** 简介 */
    private String intro;

    /** 配送费 */
    @Excel(name = "配送费")
    private BigDecimal deliveryFee;

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

    public String getRiderName() { return riderName; }
    public void setRiderName(String riderName) { this.riderName = riderName; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }

    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }

    public String getEstTime() { return estTime; }
    public void setEstTime(String estTime) { this.estTime = estTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }
}
