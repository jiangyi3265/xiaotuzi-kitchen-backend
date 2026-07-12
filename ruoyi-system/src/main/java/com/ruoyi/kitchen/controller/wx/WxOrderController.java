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
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.kitchen.domain.KitchenOrder;
import com.ruoyi.kitchen.service.IKitchenOrderService;
import com.ruoyi.kitchen.mapper.KitchenSocialMapper;
import com.ruoyi.kitchen.util.WxPageUtils;
import com.ruoyi.kitchen.util.WxTokenService;

/**
 * 微信小程序订单接口，需登录。
 */
@RestController
@RequestMapping("/api/wx/order")
public class WxOrderController
{
    @Autowired
    private IKitchenOrderService kitchenOrderService;

    @Autowired
    private WxTokenService wxTokenService;

    @Autowired
    private KitchenSocialMapper socialMapper;

    /**
     * 小程序：提交订单。
     */
    @Anonymous
    @PostMapping("/submit")
    @Transactional
    public AjaxResult submit(@RequestBody KitchenOrder kitchenOrder, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredUserId(request);
        kitchenOrder.setWxUserId(userId);
        Map<String, Object> couple = null;
        if ("1".equals(kitchenOrder.getRemoteFeed()))
        {
            couple = socialMapper.selectCoupleByUser(userId);
            if (couple == null)
            {
                return AjaxResult.error("请先绑定情侣空间");
            }
            Long userA = mapLong(couple, "userA", "usera", "user_a");
            Long userB = mapLong(couple, "userB", "userb", "user_b");
            Long recipient = userId.equals(userA) ? userB : userA;
            if (recipient == null)
            {
                return AjaxResult.error("对方尚未完成情侣绑定");
            }
            kitchenOrder.setCoupleSpaceId(mapLong(couple, "id"));
            kitchenOrder.setRecipientWxUserId(recipient);
        }
        KitchenOrder result = kitchenOrderService.submitOrder(kitchenOrder);
        if (couple != null)
        {
            socialMapper.addFeedCount(result.getCoupleSpaceId());
            Map<String, Object> notice = new java.util.HashMap<>();
            notice.put("userId", result.getRecipientWxUserId());
            notice.put("type", "couple_feed");
            notice.put("title", "TA 给你投喂了一份美食");
            notice.put("content", "订单号 " + result.getOrderNo() + "，请在订单中查看");
            notice.put("bizId", result.getId());
            socialMapper.insertNotification(notice);
        }
        AjaxResult ajax = AjaxResult.success("下单成功");
        ajax.put("orderId", result.getId());
        ajax.put("orderNo", result.getOrderNo());
        ajax.put("totalAmount", result.getTotalAmount());
        return ajax;
    }

    /**
     * 小程序：我的订单列表。
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
     * 小程序：订单详情，仅本人可看。
     */
    @Anonymous
    @GetMapping("/detail/{id}")
    public AjaxResult detail(@PathVariable("id") Long id, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredUserId(request);
        KitchenOrder order = kitchenOrderService.selectKitchenOrderById(id);
        if (order == null || (!userId.equals(order.getWxUserId()) && !userId.equals(order.getRecipientWxUserId())))
        {
            return AjaxResult.error("订单不存在");
        }
        return AjaxResult.success(order);
    }

    /**
     * 小程序：用户确认完成订单，仅本人订单。
     */
    @Anonymous
    @PostMapping("/complete/{id}")
    public AjaxResult complete(@PathVariable("id") Long id, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredUserId(request);
        KitchenOrder order = kitchenOrderService.selectKitchenOrderById(id);
        if (order == null || !userId.equals(order.getWxUserId()))
        {
            return AjaxResult.error("订单不存在");
        }
        String status = order.getOrderStatus();
        if ("4".equals(status))
        {
            return AjaxResult.error("已取消订单不能完成");
        }
        if ("5".equals(status))
        {
            return AjaxResult.error("退款中订单不能完成");
        }
        if (!"3".equals(status))
        {
            kitchenOrderService.changeOrderStatus(id, "3");
        }
        return AjaxResult.success(kitchenOrderService.selectKitchenOrderById(id));
    }

    /**
     * 小程序：申请退款，仅本人订单。
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

    private Long mapLong(Map<String, Object> map, String... keys)
    {
        for (String key : keys)
        {
            Object value = map.get(key);
            if (value != null)
            {
                return Long.valueOf(String.valueOf(value));
            }
            for (Map.Entry<String, Object> entry : map.entrySet())
            {
                if (entry.getKey().equalsIgnoreCase(key) && entry.getValue() != null)
                {
                    return Long.valueOf(String.valueOf(entry.getValue()));
                }
            }
        }
        return null;
    }
}
