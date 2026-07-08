package com.ruoyi.kitchen.controller.wx;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.kitchen.domain.KitchenShop;
import com.ruoyi.kitchen.service.IKitchenShopService;
import com.ruoyi.kitchen.util.WxTokenService;

/**
 * 厨房/店铺Controller（微信小程序端）
 * 读接口公开；保存接口（店主在小程序里设置厨房）需登录。
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/wx/shop")
public class WxShopController
{
    @Autowired
    private IKitchenShopService kitchenShopService;

    @Autowired
    private WxTokenService wxTokenService;

    /**
     * 小程序：厨房信息（首页、自提地址、收款码等）
     */
    @Anonymous
    @GetMapping("/info")
    public AjaxResult info()
    {
        return AjaxResult.success(kitchenShopService.getShop());
    }

    /**
     * 小程序：保存厨房设置（店主，无则新增有则更新）
     */
    @Anonymous
    @PostMapping("/save")
    public AjaxResult save(@RequestBody KitchenShop shop, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredOwnerId(request);
        shop.setUpdateBy("wx:" + userId);
        return kitchenShopService.saveShop(shop) > 0 ? AjaxResult.success("保存成功") : AjaxResult.error();
    }
}
