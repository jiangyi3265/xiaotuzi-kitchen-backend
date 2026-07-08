package com.ruoyi.kitchen.controller.wx;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.kitchen.domain.KitchenChef;
import com.ruoyi.kitchen.service.IKitchenChefService;

/**
 * 厨师Controller（微信小程序端，公开只读）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/wx/chef")
public class WxChefController
{
    @Autowired
    private IKitchenChefService kitchenChefService;

    /**
     * 小程序：厨师列表（仅正常状态，用于厨师代炒选择）
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list()
    {
        KitchenChef query = new KitchenChef();
        query.setStatus("0");
        List<KitchenChef> list = kitchenChefService.selectKitchenChefList(query);
        return AjaxResult.success(list);
    }
}
