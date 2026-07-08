package com.ruoyi.kitchen.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.kitchen.domain.KitchenDish;
import com.ruoyi.kitchen.domain.KitchenDishSpec;
import com.ruoyi.kitchen.domain.KitchenDishSpecValue;
import com.ruoyi.kitchen.domain.KitchenDishStep;

/**
 * 菜品Mapper接口
 *
 * @author ruoyi
 */
public interface KitchenDishMapper
{
    public KitchenDish selectKitchenDishById(Long id);

    public List<KitchenDish> selectKitchenDishList(KitchenDish kitchenDish);

    public int insertKitchenDish(KitchenDish kitchenDish);

    public int updateKitchenDish(KitchenDish kitchenDish);

    public int deleteKitchenDishById(Long id);

    public int deleteKitchenDishByIds(Long[] ids);

    /** 增加销量 */
    public int addSales(@Param("id") Long id, @Param("count") Integer count);

    /** 仅更新上下架状态（不触碰其它列，避免误清价格/子表） */
    public int updateDishStatus(@Param("id") Long id, @Param("status") String status);

    // ===== 子表：做法步骤 =====
    public List<KitchenDishStep> selectStepsByDishId(Long dishId);

    public int batchInsertStep(List<KitchenDishStep> list);

    public int deleteStepByDishIds(Long[] dishIds);

    // ===== 子表：规格组 =====
    public List<KitchenDishSpec> selectSpecsByDishId(Long dishId);

    public int insertSpec(KitchenDishSpec spec);

    public int deleteSpecByDishIds(Long[] dishIds);

    // ===== 子表：规格值 =====
    public List<KitchenDishSpecValue> selectSpecValuesBySpecId(Long specId);

    public int batchInsertSpecValue(List<KitchenDishSpecValue> list);

    public int deleteSpecValueByDishIds(Long[] dishIds);
}
