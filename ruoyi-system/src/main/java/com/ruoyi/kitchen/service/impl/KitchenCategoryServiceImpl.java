package com.ruoyi.kitchen.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.kitchen.domain.KitchenCategory;
import com.ruoyi.kitchen.mapper.KitchenCategoryMapper;
import com.ruoyi.kitchen.service.IKitchenCategoryService;

/**
 * 菜品分类Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class KitchenCategoryServiceImpl implements IKitchenCategoryService
{
    @Autowired
    private KitchenCategoryMapper kitchenCategoryMapper;

    @Override
    public KitchenCategory selectKitchenCategoryById(Long id)
    {
        return kitchenCategoryMapper.selectKitchenCategoryById(id);
    }

    @Override
    public List<KitchenCategory> selectKitchenCategoryList(KitchenCategory kitchenCategory)
    {
        return kitchenCategoryMapper.selectKitchenCategoryList(kitchenCategory);
    }

    /**
     * 构建分类树（parent_id = 0 为根，递归挂载 children）
     */
    @Override
    public List<KitchenCategory> buildCategoryTree(KitchenCategory kitchenCategory)
    {
        List<KitchenCategory> all = kitchenCategoryMapper.selectKitchenCategoryList(kitchenCategory);
        List<KitchenCategory> roots = all.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0L)
                .collect(Collectors.toList());
        for (KitchenCategory root : roots)
        {
            recursionFn(all, root);
        }
        return roots;
    }

    private void recursionFn(List<KitchenCategory> all, KitchenCategory parent)
    {
        List<KitchenCategory> children = all.stream()
                .filter(c -> parent.getId().equals(c.getParentId()))
                .collect(Collectors.toList());
        parent.setChildren(children);
        for (KitchenCategory child : children)
        {
            recursionFn(all, child);
        }
    }

    @Override
    public int insertKitchenCategory(KitchenCategory kitchenCategory)
    {
        // 计算祖级列表与层级
        Long parentId = kitchenCategory.getParentId() == null ? 0L : kitchenCategory.getParentId();
        if (parentId == 0L)
        {
            kitchenCategory.setAncestors("0");
            kitchenCategory.setCatLevel(1);
        }
        else
        {
            KitchenCategory parent = kitchenCategoryMapper.selectKitchenCategoryById(parentId);
            if (parent == null)
            {
                throw new ServiceException("父分类不存在");
            }
            if ("1".equals(parent.getStatus()))
            {
                throw new ServiceException("父分类已停用，不允许新增");
            }
            kitchenCategory.setAncestors(parent.getAncestors() + "," + parentId);
            kitchenCategory.setCatLevel(parent.getCatLevel() + 1);
            if (kitchenCategory.getCatLevel() > 3)
            {
                throw new ServiceException("分类最多支持三级");
            }
        }
        return kitchenCategoryMapper.insertKitchenCategory(kitchenCategory);
    }

    @Override
    public int updateKitchenCategory(KitchenCategory kitchenCategory)
    {
        return kitchenCategoryMapper.updateKitchenCategory(kitchenCategory);
    }

    @Override
    public int deleteKitchenCategoryByIds(Long[] ids)
    {
        for (Long id : ids)
        {
            if (kitchenCategoryMapper.selectChildrenCountByParentId(id) > 0)
            {
                throw new ServiceException("存在子分类，不允许删除");
            }
        }
        return kitchenCategoryMapper.deleteKitchenCategoryByIds(ids);
    }

    @Override
    public int deleteKitchenCategoryById(Long id)
    {
        if (kitchenCategoryMapper.selectChildrenCountByParentId(id) > 0)
        {
            throw new ServiceException("存在子分类，不允许删除");
        }
        return kitchenCategoryMapper.deleteKitchenCategoryById(id);
    }
}
