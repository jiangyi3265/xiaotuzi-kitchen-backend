package com.ruoyi.kitchen.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.kitchen.domain.KitchenOrder;
import com.ruoyi.kitchen.domain.KitchenOrderItem;

/**
 * 订单Mapper接口
 *
 * @author ruoyi
 */
public interface KitchenOrderMapper
{
    public KitchenOrder selectKitchenOrderById(Long id);

    public List<KitchenOrder> selectKitchenOrderList(KitchenOrder kitchenOrder);

    public int countOrderViewer(@Param("orderId") Long orderId, @Param("userId") Long userId);

    public int insertKitchenOrder(KitchenOrder kitchenOrder);

    public int updateKitchenOrder(KitchenOrder kitchenOrder);

    public int deleteKitchenOrderById(Long id);

    public int deleteKitchenOrderByIds(Long[] ids);

    // ===== 子表：订单明细 =====
    public List<KitchenOrderItem> selectItemsByOrderId(Long orderId);

    public int batchInsertItem(List<KitchenOrderItem> list);

    public int deleteItemByOrderIds(Long[] orderIds);
}
