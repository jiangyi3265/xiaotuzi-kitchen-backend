package com.ruoyi.kitchen.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ruoyi.kitchen.domain.KitchenWxUser;
import com.ruoyi.kitchen.service.IKitchenWxUserService;

/**
 * 小程序用户Controller（后台管理端）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/kitchen/wxUser")
public class KitchenWxUserController extends BaseController
{
    @Autowired
    private IKitchenWxUserService kitchenWxUserService;

    @PreAuthorize("@ss.hasPermi('kitchen:wxUser:list')")
    @GetMapping("/list")
    public TableDataInfo list(KitchenWxUser kitchenWxUser)
    {
        startPage();
        List<KitchenWxUser> list = kitchenWxUserService.selectKitchenWxUserList(kitchenWxUser);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('kitchen:wxUser:export')")
    @Log(title = "小程序用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KitchenWxUser kitchenWxUser)
    {
        List<KitchenWxUser> list = kitchenWxUserService.selectKitchenWxUserList(kitchenWxUser);
        ExcelUtil<KitchenWxUser> util = new ExcelUtil<>(KitchenWxUser.class);
        util.exportExcel(response, list, "小程序用户数据");
    }

    @PreAuthorize("@ss.hasAnyPermi('kitchen:wxUser:query,kitchen:wxUser:edit')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kitchenWxUserService.selectKitchenWxUserById(id));
    }

    @PreAuthorize("@ss.hasPermi('kitchen:wxUser:edit')")
    @Log(title = "小程序用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KitchenWxUser kitchenWxUser)
    {
        return toAjax(kitchenWxUserService.updateKitchenWxUser(kitchenWxUser));
    }

    @PreAuthorize("@ss.hasPermi('kitchen:wxUser:remove')")
    @Log(title = "小程序用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kitchenWxUserService.deleteKitchenWxUserByIds(ids));
    }
}
