package com.ruoyi.kitchen.service.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.ruoyi.kitchen.domain.KitchenSharePost;
import com.ruoyi.kitchen.mapper.KitchenChefMapper;
import com.ruoyi.kitchen.mapper.KitchenDishMapper;
import com.ruoyi.kitchen.mapper.KitchenOrderMapper;
import com.ruoyi.kitchen.mapper.KitchenRiderMapper;
import com.ruoyi.kitchen.mapper.KitchenSharePostMapper;
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

    @Autowired
    private KitchenSharePostMapper kitchenSharePostMapper;

    /** 发布是否需要审核：1待审核，0直接通过。 */
    @Value("${wx.shareAudit:0}")
    private String shareAudit;

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
        kitchenOrder.setShareFlag("1".equals(kitchenOrder.getShareFlag()) ? "1" : "0");
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
        if ("1".equals(kitchenOrder.getShareFlag()))
        {
            createOrderShare(kitchenOrder, items);
        }
        return kitchenOrder;
    }

    private void createOrderShare(KitchenOrder order, List<KitchenOrderItem> items)
    {
        if (order.getWxUserId() == null)
        {
            throw new ServiceException("登录后才能公开美食搭配");
        }
        StringBuilder combination = new StringBuilder();
        String firstCover = "";
        for (KitchenOrderItem item : items)
        {
            if (combination.length() > 0)
            {
                combination.append("、");
            }
            combination.append(item.getDishName()).append(" × ").append(item.getQuantity());
            if (StringUtils.isBlank(firstCover) && StringUtils.isNotBlank(item.getDishCover()))
            {
                firstCover = item.getDishCover();
            }
        }
        String combinationText = combination.toString();
        KitchenSharePost post = new KitchenSharePost();
        post.setWxUserId(order.getWxUserId());
        post.setTitle(limitText("我的美食搭配：" + combinationText, 64));
        post.setContent(limitText("本次下单搭配：" + combinationText, 500));
        post.setImages(firstCover);
        post.setTags("订单分享,美食搭配");
        post.setAuditStatus("1".equals(shareAudit) ? "0" : "1");
        post.setRemark("orderId=" + order.getId());
        if (kitchenSharePostMapper.insertKitchenSharePost(post) <= 0)
        {
            throw new ServiceException("公开搭配创建失败，请重试");
        }
    }

    private String limitText(String value, int maxLength)
    {
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
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
        validateOrderId(kitchenOrder == null ? null : kitchenOrder.getId());
        KitchenOrder existing = kitchenOrderMapper.selectKitchenOrderById(kitchenOrder.getId());
        if (existing == null)
        {
            throw new ServiceException("订单不存在或已删除");
        }

        KitchenOrder editable = new KitchenOrder();
        editable.setId(kitchenOrder.getId());
        boolean hasEditableField = false;
        if (kitchenOrder.getReceiverName() != null)
        {
            String receiverName = kitchenOrder.getReceiverName().trim();
            if (receiverName.isEmpty() || receiverName.length() > 32)
            {
                throw new ServiceException("收货人不能为空且最多32个字");
            }
            editable.setReceiverName(receiverName);
            hasEditableField = true;
        }
        if (kitchenOrder.getReceiverPhone() != null)
        {
            String receiverPhone = kitchenOrder.getReceiverPhone().trim();
            if (!receiverPhone.matches("^1\\d{10}$"))
            {
                throw new ServiceException("请填写正确的收货手机号");
            }
            editable.setReceiverPhone(receiverPhone);
            hasEditableField = true;
        }
        if (kitchenOrder.getReceiverAddress() != null)
        {
            String receiverAddress = kitchenOrder.getReceiverAddress().trim();
            if (receiverAddress.isEmpty() || receiverAddress.length() > 255)
            {
                throw new ServiceException("收货地址不能为空且最多255个字");
            }
            editable.setReceiverAddress(receiverAddress);
            hasEditableField = true;
        }
        if (kitchenOrder.getChefId() != null)
        {
            if (!"1".equals(existing.getServiceType()))
            {
                throw new ServiceException("只有代炒订单可以分配厨师");
            }
            KitchenChef chef = kitchenChefMapper.selectKitchenChefById(kitchenOrder.getChefId());
            if (chef == null || "1".equals(chef.getStatus()))
            {
                throw new ServiceException("所选厨师不可用");
            }
            editable.setChefId(kitchenOrder.getChefId());
            hasEditableField = true;
        }
        if (kitchenOrder.getRiderId() != null)
        {
            if (!"0".equals(existing.getServiceType()))
            {
                throw new ServiceException("只有配送订单可以分配配送员");
            }
            KitchenRider rider = kitchenRiderMapper.selectKitchenRiderById(kitchenOrder.getRiderId());
            if (rider == null || "1".equals(rider.getStatus()))
            {
                throw new ServiceException("所选配送员不可用");
            }
            editable.setRiderId(kitchenOrder.getRiderId());
            hasEditableField = true;
        }
        if (kitchenOrder.getRemark() != null)
        {
            String remark = kitchenOrder.getRemark().trim();
            if (remark.length() > 500)
            {
                throw new ServiceException("备注最多填写500个字");
            }
            editable.setRemark(remark);
            hasEditableField = true;
        }
        if (!hasEditableField)
        {
            throw new ServiceException("没有可修改的订单信息，状态和收款请使用对应操作");
        }
        editable.setUpdateBy(kitchenOrder.getUpdateBy());
        int rows = kitchenOrderMapper.updateKitchenOrder(editable);
        if (rows <= 0)
        {
            throw new ServiceException("订单已被删除或发生变化，请刷新后重试");
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeOrderStatus(Long id, String orderStatus)
    {
        validateOrderId(id);
        if (!isValidOrderStatus(orderStatus))
        {
            throw new ServiceException("订单状态参数不正确");
        }
        KitchenOrder order = kitchenOrderMapper.selectKitchenOrderById(id);
        if (order == null)
        {
            throw new ServiceException("订单不存在或已删除");
        }
        String currentStatus = order.getOrderStatus();
        if (orderStatus.equals(currentStatus))
        {
            throw new ServiceException("订单已处于“" + orderStatusLabel(orderStatus) + "”状态");
        }
        if (!isAllowedAdminTransition(currentStatus, orderStatus))
        {
            throw new ServiceException("订单状态不能从“" + orderStatusLabel(currentStatus)
                    + "”变更为“" + orderStatusLabel(orderStatus) + "”");
        }
        int rows = kitchenOrderMapper.updateOrderStatus(id, currentStatus, orderStatus);
        if (rows <= 0)
        {
            throw new ServiceException("订单状态已发生变化，请刷新后重试");
        }
        if ("5".equals(currentStatus) && "4".equals(orderStatus) && "1".equals(order.getPayStatus())
                && kitchenOrderMapper.updatePayStatus(id, "1", "0") <= 0)
        {
            throw new ServiceException("退款收款状态已发生变化，请刷新后重试");
        }
        return rows;
    }

    @Override
    public int changePayStatus(Long id, String payStatus)
    {
        validateOrderId(id);
        if (!("0".equals(payStatus) || "1".equals(payStatus)))
        {
            throw new ServiceException("收款状态参数不正确");
        }
        KitchenOrder order = kitchenOrderMapper.selectKitchenOrderById(id);
        if (order == null)
        {
            throw new ServiceException("订单不存在或已删除");
        }
        String currentStatus = order.getPayStatus();
        if (payStatus.equals(currentStatus))
        {
            throw new ServiceException("订单已经标记为" + ("1".equals(payStatus) ? "已收款" : "未收款"));
        }
        if ("1".equals(payStatus) && "4".equals(order.getOrderStatus()))
        {
            throw new ServiceException("已取消订单不能标记为已收款");
        }
        int rows = kitchenOrderMapper.updatePayStatus(id, currentStatus, payStatus);
        if (rows <= 0)
        {
            throw new ServiceException("订单收款状态已发生变化，请刷新后重试");
        }
        return rows;
    }

    @Override
    public int completeOrder(Long id, Long wxUserId)
    {
        return kitchenOrderMapper.completeOrder(id, wxUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KitchenOrder applyRefund(Long id, Long wxUserId, String reason)
    {
        KitchenOrder order = selectKitchenOrderById(id);
        if (order == null || !wxUserId.equals(order.getWxUserId()))
        {
            throw new ServiceException("订单不存在");
        }
        String status = order.getOrderStatus();
        if ("5".equals(status))
        {
            return order;
        }
        if (!("0".equals(status) || "1".equals(status) || "2".equals(status)))
        {
            throw new ServiceException("当前订单状态不能申请退款");
        }
        String cleanReason = StringUtils.isBlank(reason) ? "" : reason.trim();
        if (cleanReason.length() > 200)
        {
            throw new ServiceException("退款原因最多填写200个字");
        }
        String refundReason = cleanReason.isEmpty() ? "用户申请退款" : "用户申请退款：" + cleanReason;
        String oldRemark = StringUtils.isBlank(order.getRemark()) ? "" : order.getRemark().trim() + "\n";
        String newRemark = oldRemark + refundReason;
        if (newRemark.length() > 500)
        {
            throw new ServiceException("订单备注与退款原因合计不能超过500个字");
        }
        if (kitchenOrderMapper.updateOrderStatus(id, status, "5") <= 0)
        {
            throw new ServiceException("订单状态已发生变化，请刷新后重试");
        }
        KitchenOrder update = new KitchenOrder();
        update.setId(id);
        update.setRemark(newRemark);
        if (kitchenOrderMapper.updateKitchenOrder(update) <= 0)
        {
            throw new ServiceException("退款申请保存失败，请重试");
        }
        return selectKitchenOrderById(id);
    }

    private void validateOrderId(Long id)
    {
        if (id == null || id <= 0)
        {
            throw new ServiceException("订单ID不能为空");
        }
    }

    private boolean isValidOrderStatus(String status)
    {
        return "0".equals(status) || "1".equals(status) || "2".equals(status)
                || "3".equals(status) || "4".equals(status) || "5".equals(status);
    }

    private boolean isAllowedAdminTransition(String currentStatus, String nextStatus)
    {
        if ("0".equals(currentStatus))
        {
            return "1".equals(nextStatus) || "4".equals(nextStatus);
        }
        if ("1".equals(currentStatus))
        {
            return "2".equals(nextStatus) || "4".equals(nextStatus);
        }
        if ("2".equals(currentStatus))
        {
            return "3".equals(nextStatus) || "4".equals(nextStatus);
        }
        if ("5".equals(currentStatus))
        {
            return "4".equals(nextStatus);
        }
        return false;
    }

    private String orderStatusLabel(String status)
    {
        if ("0".equals(status)) return "待处理";
        if ("1".equals(status)) return "已接单";
        if ("2".equals(status)) return "制作中";
        if ("3".equals(status)) return "已完成";
        if ("4".equals(status)) return "已取消";
        if ("5".equals(status)) return "申请退款";
        return "未知状态";
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
