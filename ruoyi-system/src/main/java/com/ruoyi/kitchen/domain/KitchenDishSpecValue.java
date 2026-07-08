package com.ruoyi.kitchen.domain;

import java.io.Serializable;

/**
 * 菜品规格值对象 kitchen_dish_spec_value
 *
 * @author ruoyi
 */
public class KitchenDishSpecValue implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 规格值ID */
    private Long id;

    /** 规格组ID */
    private Long specId;

    /** 规格值(如微辣) */
    private String specValue;

    /** 显示顺序 */
    private Integer orderNum;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSpecId() { return specId; }
    public void setSpecId(Long specId) { this.specId = specId; }

    public String getSpecValue() { return specValue; }
    public void setSpecValue(String specValue) { this.specValue = specValue; }

    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }
}
