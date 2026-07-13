package com.ruoyi.kitchen.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 菜品对象 kitchen_dish
 *
 * @author ruoyi
 */
public class KitchenDish extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 菜品ID */
    private Long id;

    /** 菜品名称 */
    @Excel(name = "菜品名称")
    private String dishName;

    /** 封面图 */
    private String cover;

    /** 分类ID */
    private Long categoryId;

    /** 分类名称（非DB字段，列表展示用） */
    @Excel(name = "所属分类")
    private String categoryName;

    /** 菜品描述/故事 */
    private String story;

    /** 所用用料 */
    private String ingredients;

    /** 虚拟金额 */
    @Excel(name = "虚拟金额")
    private BigDecimal virtualPrice;

    /** 是否多规格(0否 1是) */
    private String hasSpecs;

    /** 做法是否公开(0否 1是) */
    private String recipeOpen;

    /** 烹饪经验 */
    private String cookingExp;

    /** 准备时间(分钟) */
    private Integer prepTime;

    /** 烹饪时间(分钟) */
    private Integer cookTime;

    /** 难度 */
    @Excel(name = "难度")
    private String difficulty;

    /** 适合人数 */
    private Integer portions;

    /** 销量 */
    @Excel(name = "销量")
    private Integer sales;

    /** 显示顺序 */
    private Integer orderNum;

    /** 状态(0下架 1上架) */
    @Excel(name = "状态", readConverterExp = "0=下架,1=上架")
    private String status;

    /** 今日安排类型（空=不展示 hotpot=火锅类 barbecue=烧烤） */
    @Excel(name = "今日安排", readConverterExp = "hotpot=火锅类,barbecue=烧烤")
    private String todayType;

    /** 规格组列表（非DB字段） */
    private List<KitchenDishSpec> specs = new ArrayList<>();

    /** 做法步骤列表（非DB字段） */
    private List<KitchenDishStep> steps = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDishName() { return dishName; }
    public void setDishName(String dishName) { this.dishName = dishName; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getStory() { return story; }
    public void setStory(String story) { this.story = story; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public BigDecimal getVirtualPrice() { return virtualPrice; }
    public void setVirtualPrice(BigDecimal virtualPrice) { this.virtualPrice = virtualPrice; }

    public String getHasSpecs() { return hasSpecs; }
    public void setHasSpecs(String hasSpecs) { this.hasSpecs = hasSpecs; }

    public String getRecipeOpen() { return recipeOpen; }
    public void setRecipeOpen(String recipeOpen) { this.recipeOpen = recipeOpen; }

    public String getCookingExp() { return cookingExp; }
    public void setCookingExp(String cookingExp) { this.cookingExp = cookingExp; }

    public Integer getPrepTime() { return prepTime; }
    public void setPrepTime(Integer prepTime) { this.prepTime = prepTime; }

    public Integer getCookTime() { return cookTime; }
    public void setCookTime(Integer cookTime) { this.cookTime = cookTime; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getPortions() { return portions; }
    public void setPortions(Integer portions) { this.portions = portions; }

    public Integer getSales() { return sales; }
    public void setSales(Integer sales) { this.sales = sales; }

    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTodayType() { return todayType; }
    public void setTodayType(String todayType) { this.todayType = todayType; }

    public List<KitchenDishSpec> getSpecs() { return specs; }
    public void setSpecs(List<KitchenDishSpec> specs) { this.specs = specs; }

    public List<KitchenDishStep> getSteps() { return steps; }
    public void setSteps(List<KitchenDishStep> steps) { this.steps = steps; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("dishName", getDishName())
                .append("categoryId", getCategoryId())
                .append("ingredients", getIngredients())
                .append("status", getStatus())
                .append("todayType", getTodayType())
                .append("sales", getSales())
                .toString();
    }
}
