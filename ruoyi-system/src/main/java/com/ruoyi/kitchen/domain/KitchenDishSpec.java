package com.ruoyi.kitchen.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜品规格组对象 kitchen_dish_spec
 *
 * @author ruoyi
 */
public class KitchenDishSpec implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 规格ID */
    private Long id;

    /** 菜品ID */
    private Long dishId;

    /** 规格名(如辣度) */
    private String specName;

    /** 是否多选(0单选 1多选) */
    private String multiple;

    /** 显示顺序 */
    private Integer orderNum;

    /** 规格值列表 */
    private List<KitchenDishSpecValue> values = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDishId() { return dishId; }
    public void setDishId(Long dishId) { this.dishId = dishId; }

    public String getSpecName() { return specName; }
    public void setSpecName(String specName) { this.specName = specName; }

    public String getMultiple() { return multiple; }
    public void setMultiple(String multiple) { this.multiple = multiple; }

    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }

    public List<KitchenDishSpecValue> getValues() { return values; }
    public void setValues(List<KitchenDishSpecValue> values) { this.values = values; }
}
