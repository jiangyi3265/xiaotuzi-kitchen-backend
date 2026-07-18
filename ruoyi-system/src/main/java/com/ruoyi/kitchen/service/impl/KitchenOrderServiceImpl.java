package com.ruoyi.kitchen.service.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenChef;
import com.ruoyi.kitchen.domain.KitchenDish;
import com.ruoyi.kitchen.domain.KitchenOrder;
import com.ruoyi.kitchen.domain.KitchenOrderItem;
import com.ruoyi.kitchen.domain.KitchenRider;
import com.ruoyi.kitchen.mapper.KitchenChefMapper;
import com.ruoyi.kitchen.mapper.KitchenDishMapper;
import com.ruoyi.kitchen.mapper.KitchenOrderMapper;
import com.ruoyi.kitchen.mapper.KitchenRiderMapper;
import com.ruoyi.kitchen.service.IKitchenOrderService;

/**
 * 订单Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class KitchenOrderServiceImpl implements IKitchenOrderService
{
    @Autowired
    private KitchenOrderMapper kitchenOrderMapper;

    @Autowired
    private KitchenDishMapper kitchenDishMapper;

    @Autowired
    private KitchenChefMapper kitchenChefMapper;

    @Autowired
    private KitchenRiderMapper kitchenRiderMapper;

    @Override
    public KitchenOrder selectKitchenOrderById(Long id)
    {
        KitchenOrder order = kitchenOrderMapper.selectKitchenOrderById(id);
        if (order != null)
        {
            order.setItems(kitchenOrderMapper.selectItemsByOrderId(id));
        }
        return order;
    }

    @Override
    public List<KitchenOrder> selectKitchenOrderList(KitchenOrder kitchenOrder)
    {
        List<KitchenOrder> list = kitchenOrderMapper.selectKitchenOrderList(kitchenOrder);
        for (KitchenOrder order : list)
        {
            if (order.getId() != null)
            {
                order.setItems(kitchenOrderMapper.selectItemsByOrderId(order.getId()));
            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KitchenOrder submitOrder(KitchenOrder kitchenOrder)
    {
        List<KitchenOrderItem> items = kitchenOrder.getItems();
        if (StringUtils.isEmpty(items))
        {
            throw new ServiceException("请选择菜品后再下单");
        }

        // 配送/代炒需要收货信息
        String serviceType = kitchenOrder.getServiceType();
        if (!("0".equals(serviceType) || "1".equals(serviceType) || "2".equals(serviceType)))
        {
            throw new ServiceException("请选择正确的用餐类型");
        }
        if (!(StringUtils.isBlank(kitchenOrder.getShareFlag())
                || "0".equals(kitchenOrder.getShareFlag()) || "1".equals(kitchenOrder.getShareFlag())))
        {
            throw new ServiceException("订单分享参数不正确");
        }
        if (!(StringUtils.isBlank(kitchenOrder.getRemoteFeed())
                || "0".equals(kitchenOrder.getRemoteFeed()) || "1".equals(kitchenOrder.getRemoteFeed()))
                || !(StringUtils.isBlank(kitchenOrder.getCoupleOrder())
                || "0".equals(kitchenOrder.getCoupleOrder()) || "1".equals(kitchenOrder.getCoupleOrder())))
        {
            throw new ServiceException("订单来源参数不正确");
        }
        if (kitchenOrder.getRemark() != null && kitchenOrder.getRemark().length() > 500)
        {
            throw new ServiceException("备注最多填写500个字");
        }
        if ("0".equals(serviceType) || "1".equals(serviceType))
        {
            if (StringUtils.isBlank(kitchenOrder.getReceiverName())
                    || StringUtils.isBlank(kitchenOrder.getReceiverPhone())
                    || StringUtils.isBlank(kitchenOrder.getReceiverAddress()))
            {
                throw new ServiceException("请完善收货人/电话/地址");
            }
            kitchenOrder.setReceiverName(kitchenOrder.getReceiverName().trim());
            kitchenOrder.setReceiverPhone(kitchenOrder.getReceiverPhone().trim());
            kitchenOrder.setReceiverAddress(kitchenOrder.getReceiverAddress().trim());
            if (kitchenOrder.getReceiverName().length() > 32)
            {
                throw new ServiceException("收货人姓名最多32个字");
            }
            if (!kitchenOrder.getReceiverPhone().matches("^1\\d{10}$"))
            {
                throw new ServiceException("请填写正确的手机号");
            }
            if (kitchenOrder.getReceiverAddress().length() > 255)
            {
                throw new ServiceException("收货地址最多255个字");
            }
        }

        int totalCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 以服务端数据为准，重新核对菜品快照与价格，防止前端篡改
        Set<Long> dishIds = new HashSet<>();
        for (KitchenOrderItem item : items)
        {
            if (item == null || item.getDishId() == null)
            {
                throw new ServiceException("订单中存在无效菜品");
            }
            if (!dishIds.add(item.getDishId()))
            {
                throw new ServiceException("订单中存在重复菜品，请刷新后重试");
            }
            KitchenDish dish = kitchenDishMapper.selectKitchenDishById(item.getDishId());
            if (dish == null || !"1".equals(dish.getStatus()))
            {
                throw new ServiceException("菜品不存在或已下架: " + item.getDishId());
            }
            if (item.getQuantity() == null || item.getQuantity() < 1 || item.getQuantity() > 999)
            {
                throw new ServiceException("单个菜品数量必须在1到999之间");
            }
            int qty = item.getQuantity();
            BigDecimal price = dish.getVirtualPrice() == null ? BigDecimal.ZERO : dish.getVirtualPrice();
            item.setDishName(dish.getDishName());
            item.setDishCover(dish.getCover());
            item.setQuantity(qty);
            item.setPrice(price);
            totalCount += qty;
            totalAmount = totalAmount.add(price.multiply(new BigDecimal(qty)));
        }

        // 厨师代炒加价
        if ("1".equals(serviceType) && kitchenOrder.getChefId() != null)
        {
            KitchenChef chef = kitchenChefMapper.selectKitchenChefById(kitchenOrder.getChefId());
            if (chef == null || "1".equals(chef.getStatus()))
            {
                throw new ServiceException("所选厨师不可用");
            }
            if (chef.getExtraPrice() != null)
            {
                totalAmount = totalAmount.add(chef.getExtraPrice());
            }
        }
        else if ("1".equals(serviceType))
        {
            throw new ServiceException("请选择代炒厨师");
        }
        else
        {
            kitchenOrder.setChefId(null);
        }

        // 同城配送：校验配送员并叠加配送费
        if ("0".equals(serviceType) && kitchenOrder.getRiderId() != null)
        {
            KitchenRider rider = kitchenRiderMapper.selectKitchenRiderById(kitchenOrder.getRiderId());
            if (rider == null || "1".equals(rider.getStatus()))
            {
                throw new ServiceException("所选配送员不可用");
            }
            if (rider.getDeliveryFee() != null)
            {
                totalAmount = totalAmount.add(rider.getDeliveryFee());
            }
        }
        else if (!"0".equals(serviceType))
        {
            kitchenOrder.setRiderId(null);
        }

        kitchenOrder.setOrderNo(generateOrderNo());
        kitchenOrder.setTotalCount(totalCount);
        kitchenOrder.setTotalAmount(totalAmount);
        kitchenOrder.setOrderStatus("0"); // 待处理
        kitchenOrder.setPayStatus("0");    // 未支付（小程序仅展示收款码，线下支付）
        kitchenOrderMapper.insertKitchenOrder(kitchenOrder);

        // 保存明细 + 累加销量
        for (KitchenOrderItem item : items)
        {
            item.setOrderId(kitchenOrder.getId());
        }
        kitchenOrderMapper.batchInsertItem(items);
        for (KitchenOrderItem item : items)
        {
            kitchenDishMapper.addSales(item.getDishId(), item.getQuantity());
        }
        return kitchenOrder;
    }

    /** 订单号自增序列（进程内，配合时间戳+随机降低撞号概率） */
    private static final java.util.concurrent.atomic.AtomicInteger ORDER_SEQ = new java.util.concurrent.atomic.AtomicInteger(0);

    /**
     * 生成订单号：yyyyMMddHHmmss + 4位自增序列 + 3位随机（撞号需同秒同序列同随机，概率可忽略；order_no 有唯一索引兜底）
     */
    private String generateOrderNo()
    {
        String date = DateUtils.parseDateToStr("yyyyMMddHHmmss", DateUtils.getNowDate());
        int seq = ORDER_SEQ.updateAndGet(v -> (v + 1) % 10000);
        int rand = java.util.concurrent.ThreadLocalRandom.current().nextInt(100, 1000);
        return date + String.format("%04d%03d", seq, rand);
    }

    @Override
    public int updateKitchenOrder(KitchenOrder kitchenOrder)
    {
        return kitchenOrderMapper.updateKitchenOrder(kitchenOrder);
    }

    @Override
    public int changeOrderStatus(Long id, String orderStatus)
    {
        KitchenOrder order = new KitchenOrder();
        order.setId(id);
        order.setOrderStatus(orderStatus);
        return kitchenOrderMapper.updateKitchenOrder(order);
    }

    @Override
    public int completeOrder(Long id, Long wxUserId)
    {
        return kitchenOrderMapper.completeOrder(id, wxUserId);
    }

    @Override
    public KitchenOrder applyRefund(Long id, Long wxUserId, String reason)
    {
        KitchenOrder order = selectKitchenOrderById(id);
        if (order == null || !wxUserId.equals(order.getWxUserId()))
        {
            throw new ServiceException("订单不存在");
        }
        String status = order.getOrderStatus();
        if ("4".equals(status))
        {
            throw new ServiceException("已取消订单不能申请退款");
        }
        if ("5".equals(status))
        {
            return order;
        }
        KitchenOrder update = new KitchenOrder();
        update.setId(id);
        update.setOrderStatus("5");
        String refundReason = StringUtils.isBlank(reason) ? "用户申请退款" : "用户申请退款：" + reason.trim();
        String oldRemark = StringUtils.isBlank(order.getRemark()) ? "" : order.getRemark().trim() + "\n";
        update.setRemark(oldRemark + refundReason);
        kitchenOrderMapper.updateKitchenOrder(update);
        return selectKitchenOrderById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteKitchenOrderByIds(Long[] ids)
    {
        kitchenOrderMapper.deleteItemByOrderIds(ids);
        return kitchenOrderMapper.deleteKitchenOrderByIds(ids);
    }

    @Override
    public int deleteKitchenOrderById(Long id)
    {
        return deleteKitchenOrderByIds(new Long[] { id });
    }
}
