package com.ruoyi.kitchen.mapper;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenCategory;

/**
 * 菜品分类Mapper接口
 *
 * @author ruoyi
 */
public interface KitchenCategoryMapper
{
    /**
     * 查询菜品分类
     */
    public KitchenCategory selectKitchenCategoryById(Long id);

    /**
     * 查询菜品分类列表
     */
    public List<KitchenCategory> selectKitchenCategoryList(KitchenCategory kitchenCategory);

    /**
     * 新增菜品分类
     */
    public int insertKitchenCategory(KitchenCategory kitchenCategory);

    /**
     * 修改菜品分类
     */
    public int updateKitchenCategory(KitchenCategory kitchenCategory);

    /**
     * 删除菜品分类
     */
    public int deleteKitchenCategoryById(Long id);

    /**
     * 批量删除菜品分类
     */
    public int deleteKitchenCategoryByIds(Long[] ids);

    /**
     * 统计子分类数量
     */
    public int selectChildrenCountByParentId(Long parentId);

    /**
     * 统计分类下未删除的菜品数量
     */
    public int selectDishCountByCategoryId(Long categoryId);

    /** 统计“其他”区域引用该分类的菜品数量 */
    public int selectOtherDishCountByCategoryId(Long categoryId);
}
