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
import com.ruoyi.kitchen.domain.KitchenRider;
import com.ruoyi.kitchen.service.IKitchenRiderService;

/**
 * 配送员Controller（后台管理端）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/kitchen/rider")
public class KitchenRiderController extends BaseController
{
    @Autowired
    private IKitchenRiderService kitchenRiderService;

    @PreAuthorize("@ss.hasPermi('kitchen:rider:list')")
    @GetMapping("/list")
    public TableDataInfo list(KitchenRider kitchenRider)
    {
        startPage();
        List<KitchenRider> list = kitchenRiderService.selectKitchenRiderList(kitchenRider);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('kitchen:rider:export')")
    @Log(title = "配送员", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KitchenRider kitchenRider)
    {
        List<KitchenRider> list = kitchenRiderService.selectKitchenRiderList(kitchenRider);
        ExcelUtil<KitchenRider> util = new ExcelUtil<>(KitchenRider.class);
        util.exportExcel(response, list, "配送员数据");
    }

    @PreAuthorize("@ss.hasPermi('kitchen:rider:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kitchenRiderService.selectKitchenRiderById(id));
    }

    @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('kitchen:rider:add')")
    @Log(title = "配送员", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody KitchenRider kitchenRider)
    {
        return toAjax(kitchenRiderService.insertKitchenRider(kitchenRider));
    }

    @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('kitchen:rider:edit')")
    @Log(title = "配送员", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody KitchenRider kitchenRider)
    {
        return toAjax(kitchenRiderService.updateKitchenRider(kitchenRider));
    }

    @PreAuthorize("@ss.hasRole('admin') and @ss.hasPermi('kitchen:rider:remove')")
    @Log(title = "配送员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kitchenRiderService.deleteKitchenRiderByIds(ids));
    }
}
