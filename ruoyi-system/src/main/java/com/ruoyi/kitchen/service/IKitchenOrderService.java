package com.ruoyi.kitchen.service;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenOrder;

/**
 * 订单Service接口
 *
 * @author ruoyi
 */
public interface IKitchenOrderService
{
    /**
     * 查询订单（含明细）
     */
    public KitchenOrder selectKitchenOrderById(Long id);

    /**
     * 查询订单列表
     */
    public List<KitchenOrder> selectKitchenOrderList(KitchenOrder kitchenOrder);

    /**
     * 小程序：提交订单（生成订单号，保存明细，计算金额，累加销量）
     *
     * @return 生成的订单
     */
    public KitchenOrder submitOrder(KitchenOrder kitchenOrder);

    /**
     * 后台：修改订单（状态/支付/收货信息）
     */
    public int updateKitchenOrder(KitchenOrder kitchenOrder);

    /**
     * 修改订单状态
     */
    public int changeOrderStatus(Long id, String orderStatus);

    /**
     * 批量删除订单（级联删除明细）
     */
    public int deleteKitchenOrderByIds(Long[] ids);

    public int deleteKitchenOrderById(Long id);
}
