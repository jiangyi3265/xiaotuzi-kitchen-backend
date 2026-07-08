package com.ruoyi.kitchen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.kitchen.domain.KitchenShop;
import com.ruoyi.kitchen.service.IKitchenShopService;

/**
 * 厨房/店铺设置Controller（后台管理端，单条配置）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/kitchen/shop")
public class KitchenShopController extends BaseController
{
    @Autowired
    private IKitchenShopService kitchenShopService;

    /**
     * 获取厨房配置
     */
    @PreAuthorize("@ss.hasPermi('kitchen:shop:query')")
    @GetMapping
    public AjaxResult getInfo()
    {
        return success(kitchenShopService.getShop());
    }

    /**
     * 保存厨房配置
     */
    @PreAuthorize("@ss.hasPermi('kitchen:shop:edit')")
    @Log(title = "厨房设置", businessType = BusinessType.UPDATE)
    @PostMapping
    public AjaxResult save(@RequestBody KitchenShop kitchenShop)
    {
        return toAjax(kitchenShopService.saveShop(kitchenShop));
    }
}
