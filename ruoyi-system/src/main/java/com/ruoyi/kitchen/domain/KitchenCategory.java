package com.ruoyi.kitchen.domain;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 菜品分类对象 kitchen_category
 *
 * @author ruoyi
 */
public class KitchenCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分类ID */
    private Long id;

    /** 父分类ID */
    private Long parentId;

    /** 祖级列表 */
    private String ancestors;

    /** 分类名称 */
    @Excel(name = "分类名称")
    private String catName;

    /** 分类图片 */
    private String image;

    /** 小程序顶部展示栏目（例如：私房菜、其他、同城） */
    @Excel(name = "展示栏目")
    private String displayArea;

    /** 层级(1/2/3) */
    @Excel(name = "层级")
    private Integer catLevel;

    /** 显示顺序 */
    private Integer orderNum;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 子分类（构建树用，非数据库字段） */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<KitchenCategory> children = new ArrayList<>();

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Long getParentId() { return parentId; }

    public void setAncestors(String ancestors) { this.ancestors = ancestors; }
    public String getAncestors() { return ancestors; }

    public void setCatName(String catName) { this.catName = catName; }
    public String getCatName() { return catName; }

    public void setImage(String image) { this.image = image; }
    public String getImage() { return image; }

    public void setDisplayArea(String displayArea) { this.displayArea = displayArea; }
    public String getDisplayArea() { return displayArea; }

    public void setCatLevel(Integer catLevel) { this.catLevel = catLevel; }
    public Integer getCatLevel() { return catLevel; }

    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }
    public Integer getOrderNum() { return orderNum; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public List<KitchenCategory> getChildren() { return children; }
    public void setChildren(List<KitchenCategory> children) { this.children = children; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("parentId", getParentId())
                .append("ancestors", getAncestors())
                .append("catName", getCatName())
                .append("image", getImage())
                .append("displayArea", getDisplayArea())
                .append("catLevel", getCatLevel())
                .append("orderNum", getOrderNum())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
