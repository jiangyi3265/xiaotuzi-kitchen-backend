package com.ruoyi.kitchen.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单明细对象 kitchen_order_item
 *
 * @author ruoyi
 */
public class KitchenOrderItem implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 菜品ID */
    private Long dishId;

    /** 菜品名称(快照) */
    private String dishName;

    /** 菜品封面(快照) */
    private String dishCover;

    /** 已选规格JSON */
    private String specJson;

    /** 数量 */
    private Integer quantity;

    /** 单价 */
    private BigDecimal price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getDishId() { return dishId; }
    public void setDishId(Long dishId) { this.dishId = dishId; }

    public String getDishName() { return dishName; }
    public void setDishName(String dishName) { this.dishName = dishName; }

    public String getDishCover() { return dishCover; }
    public void setDishCover(String dishCover) { this.dishCover = dishCover; }

    public String getSpecJson() { return specJson; }
    public void setSpecJson(String specJson) { this.specJson = specJson; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
