package com.ruoyi.kitchen.service;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenCategory;

/**
 * 菜品分类Service接口
 *
 * @author ruoyi
 */
public interface IKitchenCategoryService
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
     * 构建分类树（供后台树选择/小程序使用）
     */
    public List<KitchenCategory> buildCategoryTree(KitchenCategory kitchenCategory);

    /**
     * 新增菜品分类
     */
    public int insertKitchenCategory(KitchenCategory kitchenCategory);

    /**
     * 修改菜品分类
     */
    public int updateKitchenCategory(KitchenCategory kitchenCategory);

    /**
     * 批量删除菜品分类
     */
    public int deleteKitchenCategoryByIds(Long[] ids);

    /**
     * 删除菜品分类
     */
    public int deleteKitchenCategoryById(Long id);
}
