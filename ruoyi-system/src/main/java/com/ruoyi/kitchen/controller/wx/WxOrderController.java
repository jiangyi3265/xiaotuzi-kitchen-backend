package com.ruoyi.kitchen.controller.wx;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.kitchen.domain.KitchenOrder;
import com.ruoyi.kitchen.service.IKitchenOrderService;
import com.ruoyi.kitchen.util.WxPageUtils;
import com.ruoyi.kitchen.util.WxTokenService;

/**
 * 订单Controller（微信小程序端，需登录）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/wx/order")
public class WxOrderController
{
    @Autowired
    private IKitchenOrderService kitchenOrderService;

    @Autowired
    private WxTokenService wxTokenService;

    /**
     * 小程序：提交订单
     */
    @Anonymous
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody KitchenOrder kitchenOrder, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredUserId(request);
        kitchenOrder.setWxUserId(userId);
        KitchenOrder result = kitchenOrderService.submitOrder(kitchenOrder);
        AjaxResult ajax = AjaxResult.success("下单成功");
        ajax.put("orderId", result.getId());
        ajax.put("orderNo", result.getOrderNo());
        ajax.put("totalAmount", result.getTotalAmount());
        return ajax;
    }

    /**
     * 小程序：我的订单列表（分页）
     */
    @Anonymous
    @GetMapping("/my")
    public TableDataInfo my(KitchenOrder kitchenOrder, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredUserId(request);
        kitchenOrder.setWxUserId(userId);
        WxPageUtils.startPage();
        List<KitchenOrder> list = kitchenOrderService.selectKitchenOrderList(kitchenOrder);
        return WxPageUtils.getDataTable(list);
    }

    /**
     * 小程序：订单详情（仅本人可看）
     */
    @Anonymous
    @GetMapping("/detail/{id}")
    public AjaxResult detail(@PathVariable("id") Long id, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredUserId(request);
        KitchenOrder order = kitchenOrderService.selectKitchenOrderById(id);
        if (order == null || !userId.equals(order.getWxUserId()))
        {
            return AjaxResult.error("订单不存在");
        }
        return AjaxResult.success(order);
    }

    /**
     * 小程序：申请退款（仅本人订单）
     */
    @Anonymous
    @PostMapping("/refund/{id}")
    public AjaxResult refund(@PathVariable("id") Long id, @RequestBody(required = false) Map<String, String> body,
            HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredUserId(request);
        String reason = body == null ? "" : body.get("reason");
        KitchenOrder order = kitchenOrderService.applyRefund(id, userId, reason);
        return AjaxResult.success("退款申请已提交", order);
    }
}
