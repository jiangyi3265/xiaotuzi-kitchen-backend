package com.ruoyi.kitchen.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.kitchen.domain.KitchenCategory;
import com.ruoyi.kitchen.service.IKitchenCategoryService;

/**
 * 菜品分类Controller（后台管理端）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/kitchen/category")
public class KitchenCategoryController extends BaseController
{
    @Autowired
    private IKitchenCategoryService kitchenCategoryService;

    /**
     * 查询菜品分类列表（分页）
     */
    @PreAuthorize("@ss.hasPermi('kitchen:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(KitchenCategory kitchenCategory)
    {
        startPage();
        List<KitchenCategory> list = kitchenCategoryService.selectKitchenCategoryList(kitchenCategory);
        return getDataTable(list);
    }

    /**
     * 查询分类树（不分页，用于父级下拉）
     */
    @PreAuthorize("@ss.hasPermi('kitchen:category:list')")
    @GetMapping("/tree")
    public AjaxResult tree(KitchenCategory kitchenCategory)
    {
        return success(kitchenCategoryService.buildCategoryTree(kitchenCategory));
    }

    /**
     * 导出菜品分类列表
     */
    @PreAuthorize("@ss.hasPermi('kitchen:category:export')")
    @Log(title = "菜品分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KitchenCategory kitchenCategory)
    {
        List<KitchenCategory> list = kitchenCategoryService.selectKitchenCategoryList(kitchenCategory);
        ExcelUtil<KitchenCategory> util = new ExcelUtil<>(KitchenCategory.class);
        util.exportExcel(response, list, "菜品分类数据");
    }

    /**
     * 获取菜品分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('kitchen:category:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kitchenCategoryService.selectKitchenCategoryById(id));
    }

    /**
     * 新增菜品分类
     */
    @PreAuthorize("@ss.hasPermi('kitchen:category:add')")
    @Log(title = "菜品分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody KitchenCategory kitchenCategory)
    {
        return toAjax(kitchenCategoryService.insertKitchenCategory(kitchenCategory));
    }

    /**
     * 修改菜品分类
     */
    @PreAuthorize("@ss.hasPermi('kitchen:category:edit')")
    @Log(title = "菜品分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody KitchenCategory kitchenCategory)
    {
        return toAjax(kitchenCategoryService.updateKitchenCategory(kitchenCategory));
    }

    /**
     * 删除菜品分类
     */
    @PreAuthorize("@ss.hasPermi('kitchen:category:remove')")
    @Log(title = "菜品分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kitchenCategoryService.deleteKitchenCategoryByIds(ids));
    }
}
