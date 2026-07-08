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
import com.ruoyi.kitchen.domain.KitchenChef;
import com.ruoyi.kitchen.service.IKitchenChefService;

/**
 * 厨师Controller（后台管理端）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/kitchen/chef")
public class KitchenChefController extends BaseController
{
    @Autowired
    private IKitchenChefService kitchenChefService;

    @PreAuthorize("@ss.hasPermi('kitchen:chef:list')")
    @GetMapping("/list")
    public TableDataInfo list(KitchenChef kitchenChef)
    {
        startPage();
        List<KitchenChef> list = kitchenChefService.selectKitchenChefList(kitchenChef);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('kitchen:chef:export')")
    @Log(title = "厨师", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KitchenChef kitchenChef)
    {
        List<KitchenChef> list = kitchenChefService.selectKitchenChefList(kitchenChef);
        ExcelUtil<KitchenChef> util = new ExcelUtil<>(KitchenChef.class);
        util.exportExcel(response, list, "厨师数据");
    }

    @PreAuthorize("@ss.hasPermi('kitchen:chef:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kitchenChefService.selectKitchenChefById(id));
    }

    @PreAuthorize("@ss.hasPermi('kitchen:chef:add')")
    @Log(title = "厨师", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody KitchenChef kitchenChef)
    {
        return toAjax(kitchenChefService.insertKitchenChef(kitchenChef));
    }

    @PreAuthorize("@ss.hasPermi('kitchen:chef:edit')")
    @Log(title = "厨师", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody KitchenChef kitchenChef)
    {
        return toAjax(kitchenChefService.updateKitchenChef(kitchenChef));
    }

    @PreAuthorize("@ss.hasPermi('kitchen:chef:remove')")
    @Log(title = "厨师", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kitchenChefService.deleteKitchenChefByIds(ids));
    }
}
