package com.ruoyi.kitchen.controller.wx;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.kitchen.domain.KitchenRider;
import com.ruoyi.kitchen.service.IKitchenRiderService;

/**
 * 配送员Controller（微信小程序端，公开只读）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/wx/rider")
public class WxRiderController
{
    @Autowired
    private IKitchenRiderService kitchenRiderService;

    /**
     * 小程序：配送员列表（仅正常状态，用于配送员选择）
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list()
    {
        KitchenRider query = new KitchenRider();
        query.setStatus("0");
        List<KitchenRider> list = kitchenRiderService.selectKitchenRiderList(query);
        return AjaxResult.success(list);
    }
}
