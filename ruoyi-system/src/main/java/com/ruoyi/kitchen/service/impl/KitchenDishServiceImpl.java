package com.ruoyi.kitchen.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenCategory;
import com.ruoyi.kitchen.domain.KitchenDish;
import com.ruoyi.kitchen.domain.KitchenDishSpec;
import com.ruoyi.kitchen.domain.KitchenDishSpecValue;
import com.ruoyi.kitchen.domain.KitchenDishStep;
import com.ruoyi.kitchen.mapper.KitchenCategoryMapper;
import com.ruoyi.kitchen.mapper.KitchenDishMapper;
import com.ruoyi.kitchen.service.IKitchenDishService;

/**
 * 菜品Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class KitchenDishServiceImpl implements IKitchenDishService
{
    @Autowired
    private KitchenDishMapper kitchenDishMapper;

    @Autowired
    private KitchenCategoryMapper kitchenCategoryMapper;

    @Override
    public KitchenDish selectKitchenDishById(Long id)
    {
        KitchenDish dish = kitchenDishMapper.selectKitchenDishById(id);
        if (dish != null)
        {
            // 装配步骤
            dish.setSteps(kitchenDishMapper.selectStepsByDishId(id));
            // 装配规格及规格值
            List<KitchenDishSpec> specs = kitchenDishMapper.selectSpecsByDishId(id);
            for (KitchenDishSpec spec : specs)
            {
                spec.setValues(kitchenDishMapper.selectSpecValuesBySpecId(spec.getId()));
            }
            dish.setSpecs(specs);
        }
        return dish;
    }

    @Override
    public List<KitchenDish> selectKitchenDishList(KitchenDish kitchenDish)
    {
        return kitchenDishMapper.selectKitchenDishList(kitchenDish);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertKitchenDish(KitchenDish kitchenDish)
    {
        validateActiveCategory(kitchenDish.getCategoryId());
        int rows = kitchenDishMapper.insertKitchenDish(kitchenDish);
        saveChildren(kitchenDish);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateKitchenDish(KitchenDish kitchenDish)
    {
        validateActiveCategory(kitchenDish.getCategoryId());
        // 先清空子表再重建，保证编辑后一致
        Long[] ids = new Long[] { kitchenDish.getId() };
        kitchenDishMapper.deleteSpecValueByDishIds(ids);
        kitchenDishMapper.deleteSpecByDishIds(ids);
        kitchenDishMapper.deleteStepByDishIds(ids);
        saveChildren(kitchenDish);
        return kitchenDishMapper.updateKitchenDish(kitchenDish);
    }

    /**
     * 菜品必须挂在一个存在且整条父级链均启用的分类下，避免后台保存成功但小程序分类树无法展示。
     */
    private void validateActiveCategory(Long categoryId)
    {
        if (categoryId == null)
        {
            throw new ServiceException("请选择所属分类");
        }

        Set<Long> visited = new HashSet<>();
        Long currentId = categoryId;
        while (currentId != null && currentId != 0L)
        {
            if (!visited.add(currentId))
            {
                throw new ServiceException("所属分类层级异常，请联系管理员处理");
            }
            KitchenCategory category = kitchenCategoryMapper.selectKitchenCategoryById(currentId);
            if (category == null)
            {
                throw new ServiceException("所属分类不存在，请重新选择");
            }
            if (!"0".equals(category.getStatus()))
            {
                throw new ServiceException("所属分类已停用，请重新选择");
            }
            currentId = category.getParentId();
        }
    }

    /**
     * 保存子表：步骤、规格、规格值
     */
    private void saveChildren(KitchenDish dish)
    {
        Long dishId = dish.getId();

        // 步骤
        List<KitchenDishStep> steps = dish.getSteps();
        if (StringUtils.isNotEmpty(steps))
        {
            int no = 1;
            for (KitchenDishStep step : steps)
            {
                step.setDishId(dishId);
                if (step.getStepNo() == null)
                {
                    step.setStepNo(no);
                }
                no++;
            }
            kitchenDishMapper.batchInsertStep(steps);
        }

        // 规格 + 规格值
        List<KitchenDishSpec> specs = dish.getSpecs();
        if (StringUtils.isNotEmpty(specs))
        {
            int specOrder = 0;
            for (KitchenDishSpec spec : specs)
            {
                spec.setDishId(dishId);
                spec.setOrderNum(specOrder++);
                kitchenDishMapper.insertSpec(spec); // 回填 spec.id
                List<KitchenDishSpecValue> values = spec.getValues();
                if (StringUtils.isNotEmpty(values))
                {
                    int valOrder = 0;
                    List<KitchenDishSpecValue> toInsert = new ArrayList<>();
                    for (KitchenDishSpecValue v : values)
                    {
                        v.setSpecId(spec.getId());
                        v.setOrderNum(valOrder++);
                        toInsert.add(v);
                    }
                    kitchenDishMapper.batchInsertSpecValue(toInsert);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteKitchenDishByIds(Long[] ids)
    {
        kitchenDishMapper.deleteSpecValueByDishIds(ids);
        kitchenDishMapper.deleteSpecByDishIds(ids);
        kitchenDishMapper.deleteStepByDishIds(ids);
        return kitchenDishMapper.deleteKitchenDishByIds(ids);
    }

    @Override
    public int deleteKitchenDishById(Long id)
    {
        return deleteKitchenDishByIds(new Long[] { id });
    }

    @Override
    public int addSales(Long id, Integer count)
    {
        return kitchenDishMapper.addSales(id, count);
    }

    @Override
    public int updateDishStatus(Long id, String status)
    {
        // 走专用 SQL 只改 status 列，避免 updateKitchenDish 重建子表 / 无条件更新 virtual_price 导致误清价格
        return kitchenDishMapper.updateDishStatus(id, status);
    }
}
