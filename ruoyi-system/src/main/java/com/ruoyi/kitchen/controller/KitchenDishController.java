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
import com.ruoyi.kitchen.domain.KitchenDish;
import com.ruoyi.kitchen.service.IKitchenDishService;

/**
 * 菜品Controller（后台管理端）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/kitchen/dish")
public class KitchenDishController extends BaseController
{
    @Autowired
    private IKitchenDishService kitchenDishService;

    @PreAuthorize("@ss.hasPermi('kitchen:dish:list')")
    @GetMapping("/list")
    public TableDataInfo list(KitchenDish kitchenDish)
    {
        startPage();
        List<KitchenDish> list = kitchenDishService.selectKitchenDishList(kitchenDish);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('kitchen:dish:export')")
    @Log(title = "菜品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KitchenDish kitchenDish)
    {
        List<KitchenDish> list = kitchenDishService.selectKitchenDishList(kitchenDish);
        ExcelUtil<KitchenDish> util = new ExcelUtil<>(KitchenDish.class);
        util.exportExcel(response, list, "菜品数据");
    }

    @PreAuthorize("@ss.hasPermi('kitchen:dish:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kitchenDishService.selectKitchenDishById(id));
    }

    @PreAuthorize("@ss.hasPermi('kitchen:dish:add')")
    @Log(title = "菜品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody KitchenDish kitchenDish)
    {
        return toAjax(kitchenDishService.insertKitchenDish(kitchenDish));
    }

    @PreAuthorize("@ss.hasPermi('kitchen:dish:edit')")
    @Log(title = "菜品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody KitchenDish kitchenDish)
    {
        return toAjax(kitchenDishService.updateKitchenDish(kitchenDish));
    }

    @PreAuthorize("@ss.hasPermi('kitchen:dish:remove')")
    @Log(title = "菜品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kitchenDishService.deleteKitchenDishByIds(ids));
    }
}
