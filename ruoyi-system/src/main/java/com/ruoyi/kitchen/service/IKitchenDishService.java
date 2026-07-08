package com.ruoyi.kitchen.service;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenDish;

/**
 * 菜品Service接口
 *
 * @author ruoyi
 */
public interface IKitchenDishService
{
    /**
     * 查询菜品（含规格、步骤）
     */
    public KitchenDish selectKitchenDishById(Long id);

    /**
     * 查询菜品列表
     */
    public List<KitchenDish> selectKitchenDishList(KitchenDish kitchenDish);

    /**
     * 新增菜品（级联规格、步骤）
     */
    public int insertKitchenDish(KitchenDish kitchenDish);

    /**
     * 修改菜品（级联规格、步骤）
     */
    public int updateKitchenDish(KitchenDish kitchenDish);

    /**
     * 批量删除菜品（级联删除规格、步骤）
     */
    public int deleteKitchenDishByIds(Long[] ids);

    /**
     * 删除菜品
     */
    public int deleteKitchenDishById(Long id);

    /**
     * 增加销量
     */
    public int addSales(Long id, Integer count);

    /**
     * 仅更新上下架状态（不触碰规格/步骤子表）
     */
    public int updateDishStatus(Long id, String status);
}
