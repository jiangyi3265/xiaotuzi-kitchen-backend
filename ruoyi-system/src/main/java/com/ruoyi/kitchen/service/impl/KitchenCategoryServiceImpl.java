package com.ruoyi.kitchen.service.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        normalizeAndValidate(kitchenCategory, null);
        Long parentId = kitchenCategory.getParentId() == null ? 0L : kitchenCategory.getParentId();
        List<KitchenCategory> parentPath = resolveParentPath(parentId, null, true);
        kitchenCategory.setParentId(parentId);
        kitchenCategory.setAncestors(buildAncestors(parentPath));
        kitchenCategory.setCatLevel(parentPath.size() + 1);
        kitchenCategory.setDisplayArea(resolveDisplayArea(kitchenCategory.getDisplayArea(), null, parentPath));
        if (kitchenCategory.getCatLevel() > 3)
        {
            throw new ServiceException("分类最多支持三级");
        }
        return kitchenCategoryMapper.insertKitchenCategory(kitchenCategory);
    }

    private void normalizeAndValidate(KitchenCategory category, KitchenCategory current)
    {
        String catName = category.getCatName();
        if (catName == null && current != null)
        {
            catName = current.getCatName();
        }
        catName = catName == null ? "" : catName.trim();
        if (catName.isEmpty())
        {
            throw new ServiceException("分类名称不能为空");
        }
        category.setCatName(catName);

        String status = category.getStatus();
        if (status == null && current != null)
        {
            status = current.getStatus();
        }
        status = status == null ? "0" : status;
        if (!"0".equals(status) && !"1".equals(status))
        {
            throw new ServiceException("分类状态只能为0或1");
        }
        category.setStatus(status);
    }

    private List<KitchenCategory> resolveParentPath(Long parentId, Long movingId, boolean requireActive)
    {
        List<KitchenCategory> path = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        Long cursor = parentId;
        while (cursor != null && cursor != 0L)
        {
            if (movingId != null && movingId.equals(cursor))
            {
                throw new ServiceException("不能将分类移到自己的子分类下");
            }
            if (!visited.add(cursor))
            {
                throw new ServiceException("父分类层级异常，请先修复分类树");
            }
            KitchenCategory parent = kitchenCategoryMapper.selectKitchenCategoryById(cursor);
            if (parent == null)
            {
                throw new ServiceException("父分类不存在");
            }
            if (requireActive && !"0".equals(parent.getStatus()))
            {
                throw new ServiceException("父分类或其上级已停用，不允许使用");
            }
            path.add(parent);
            cursor = parent.getParentId();
        }
        Collections.reverse(path);
        return path;
    }

    private String buildAncestors(List<KitchenCategory> parentPath)
    {
        StringBuilder ancestors = new StringBuilder("0");
        for (KitchenCategory parent : parentPath)
        {
            ancestors.append(',').append(parent.getId());
        }
        return ancestors.toString();
    }

    private String resolveDisplayArea(String requested, KitchenCategory current, List<KitchenCategory> parentPath)
    {
        if (!parentPath.isEmpty())
        {
            String inherited = parentPath.get(parentPath.size() - 1).getDisplayArea();
            return inherited == null || inherited.trim().isEmpty() ? "私房菜" : inherited.trim();
        }
        String displayArea = requested;
        if (displayArea == null && current != null)
        {
            displayArea = current.getDisplayArea();
        }
        displayArea = displayArea == null ? "私房菜" : displayArea.trim();
        if (displayArea.isEmpty())
        {
            throw new ServiceException("展示栏目不能为空");
        }
        if (displayArea.length() > 32)
        {
            throw new ServiceException("展示栏目不能超过32个字符");
        }
        return displayArea;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateKitchenCategory(KitchenCategory kitchenCategory)
    {
        if (kitchenCategory.getId() == null)
        {
            throw new ServiceException("缺少分类ID");
        }

        KitchenCategory current = kitchenCategoryMapper.selectKitchenCategoryById(kitchenCategory.getId());
        if (current == null)
        {
            throw new ServiceException("分类不存在");
        }
        normalizeAndValidate(kitchenCategory, current);

        Long parentId = kitchenCategory.getParentId() == null ? current.getParentId() : kitchenCategory.getParentId();
        parentId = parentId == null ? 0L : parentId;
        if (kitchenCategory.getId().equals(parentId))
        {
            throw new ServiceException("分类不能选择自己作为父分类");
        }

        List<KitchenCategory> all = kitchenCategoryMapper.selectKitchenCategoryList(new KitchenCategory());
        Map<Long, List<KitchenCategory>> childrenMap = new HashMap<>();
        for (KitchenCategory item : all)
        {
            Long itemParentId = item.getParentId() == null ? 0L : item.getParentId();
            childrenMap.computeIfAbsent(itemParentId, key -> new ArrayList<>()).add(item);
        }

        Long currentParentId = current.getParentId() == null ? 0L : current.getParentId();
        boolean parentChanged = !parentId.equals(currentParentId);
        boolean enabling = "1".equals(current.getStatus()) && "0".equals(kitchenCategory.getStatus());
        List<KitchenCategory> parentPath = resolveParentPath(parentId, kitchenCategory.getId(), parentChanged || enabling);
        String ancestors = buildAncestors(parentPath);
        int catLevel = parentPath.size() + 1;
        String displayArea = resolveDisplayArea(kitchenCategory.getDisplayArea(), current, parentPath);

        if (kitchenCategoryMapper.selectOtherDishCountByCategoryId(current.getId()) > 0
                && (parentId != 0L || "私房菜".equals(displayArea)))
        {
            throw new ServiceException("仍有菜品附加展示在该分类，不能移为子分类或私房菜栏目");
        }

        if (catLevel > 3)
        {
            throw new ServiceException("分类最多支持三级");
        }
        if (!"1".equals(current.getStatus()) && "1".equals(kitchenCategory.getStatus())
                && (kitchenCategoryMapper.selectChildrenCountByParentId(kitchenCategory.getId()) > 0
                    || kitchenCategoryMapper.selectDishCountByCategoryId(kitchenCategory.getId()) > 0
                    || kitchenCategoryMapper.selectOtherDishCountByCategoryId(kitchenCategory.getId()) > 0))
        {
            throw new ServiceException("分类下存在子分类或菜谱，不允许停用");
        }

        current.setParentId(parentId);
        current.setAncestors(ancestors);
        current.setCatLevel(catLevel);
        current.setDisplayArea(displayArea);
        Deque<KitchenCategory> queue = new ArrayDeque<>();
        Set<Long> subtree = new HashSet<>();
        List<KitchenCategory> descendants = new ArrayList<>();
        queue.add(current);
        subtree.add(current.getId());
        while (!queue.isEmpty())
        {
            KitchenCategory parent = queue.removeFirst();
            for (KitchenCategory child : childrenMap.getOrDefault(parent.getId(), Collections.emptyList()))
            {
                if (!subtree.add(child.getId()))
                {
                    throw new ServiceException("分类子树存在循环，请先修复分类树");
                }
                int childLevel = parent.getCatLevel() + 1;
                if (childLevel > 3)
                {
                    throw new ServiceException("移动后子分类将超过三级，不允许保存");
                }
                child.setAncestors(parent.getAncestors() + "," + parent.getId());
                child.setCatLevel(childLevel);
                child.setDisplayArea(parent.getDisplayArea());
                descendants.add(child);
                queue.addLast(child);
            }
        }

        kitchenCategory.setParentId(parentId);
        kitchenCategory.setAncestors(ancestors);
        kitchenCategory.setCatLevel(catLevel);
        kitchenCategory.setDisplayArea(displayArea);
        int rows = kitchenCategoryMapper.updateKitchenCategory(kitchenCategory);
        if (rows <= 0)
        {
            throw new ServiceException("分类更新失败");
        }
        for (KitchenCategory descendant : descendants)
        {
            KitchenCategory update = new KitchenCategory();
            update.setId(descendant.getId());
            update.setAncestors(descendant.getAncestors());
            update.setCatLevel(descendant.getCatLevel());
            update.setDisplayArea(descendant.getDisplayArea());
            update.setUpdateBy(kitchenCategory.getUpdateBy());
            if (kitchenCategoryMapper.updateKitchenCategory(update) <= 0)
            {
                throw new ServiceException("子分类层级更新失败");
            }
        }
        return rows;
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
            if (kitchenCategoryMapper.selectDishCountByCategoryId(id) > 0)
            {
                throw new ServiceException("分类下存在菜谱，请先迁移或删除菜谱");
            }
            if (kitchenCategoryMapper.selectOtherDishCountByCategoryId(id) > 0)
            {
                throw new ServiceException("仍有菜品使用该其他分类，请先取消关联");
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
        if (kitchenCategoryMapper.selectDishCountByCategoryId(id) > 0)
        {
            throw new ServiceException("分类下存在菜谱，请先迁移或删除菜谱");
        }
        if (kitchenCategoryMapper.selectOtherDishCountByCategoryId(id) > 0)
        {
            throw new ServiceException("仍有菜品使用该其他分类，请先取消关联");
        }
        return kitchenCategoryMapper.deleteKitchenCategoryById(id);
    }
}
