package com.ruoyi.kitchen.domain;

import java.io.Serializable;

/**
 * 菜品做法步骤对象 kitchen_dish_step
 *
 * @author ruoyi
 */
public class KitchenDishStep implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 步骤ID */
    private Long id;

    /** 菜品ID */
    private Long dishId;

    /** 步骤序号 */
    private Integer stepNo;

    /** 步骤图 */
    private String image;

    /** 步骤说明 */
    private String content;

    /** 定时器(秒) */
    private Integer timer;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDishId() { return dishId; }
    public void setDishId(Long dishId) { this.dishId = dishId; }

    public Integer getStepNo() { return stepNo; }
    public void setStepNo(Integer stepNo) { this.stepNo = stepNo; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getTimer() { return timer; }
    public void setTimer(Integer timer) { this.timer = timer; }
}
