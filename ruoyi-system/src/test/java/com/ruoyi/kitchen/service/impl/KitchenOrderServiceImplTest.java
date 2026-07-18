package com.ruoyi.kitchen.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.kitchen.domain.KitchenDish;
import com.ruoyi.kitchen.domain.KitchenOrder;
import com.ruoyi.kitchen.domain.KitchenOrderItem;
import com.ruoyi.kitchen.domain.KitchenSharePost;
import com.ruoyi.kitchen.mapper.KitchenChefMapper;
import com.ruoyi.kitchen.mapper.KitchenDishMapper;
import com.ruoyi.kitchen.mapper.KitchenOrderMapper;
import com.ruoyi.kitchen.mapper.KitchenRiderMapper;
import com.ruoyi.kitchen.mapper.KitchenSharePostMapper;

@ExtendWith(MockitoExtension.class)
class KitchenOrderServiceImplTest
{
    @Mock
    private KitchenOrderMapper kitchenOrderMapper;

    @Mock
    private KitchenDishMapper kitchenDishMapper;

    @Mock
    private KitchenChefMapper kitchenChefMapper;

    @Mock
    private KitchenRiderMapper kitchenRiderMapper;

    @Mock
    private KitchenSharePostMapper kitchenSharePostMapper;

    @InjectMocks
    private KitchenOrderServiceImpl service;

    @BeforeEach
    void setUp()
    {
        ReflectionTestUtils.setField(service, "shareAudit", "1");
    }

    @Test
    void changeOrderStatusAllowsOnlyForwardTransition()
    {
        KitchenOrder order = order("0", "0");
        when(kitchenOrderMapper.selectKitchenOrderById(1L)).thenReturn(order);
        when(kitchenOrderMapper.updateOrderStatus(1L, "0", "1")).thenReturn(1);

        assertEquals(1, service.changeOrderStatus(1L, "1"));
        verify(kitchenOrderMapper).updateOrderStatus(1L, "0", "1");
    }

    @Test
    void changeOrderStatusRejectsRollbackAndRevival()
    {
        when(kitchenOrderMapper.selectKitchenOrderById(1L)).thenReturn(order("3", "1"));

        ServiceException error = assertThrows(ServiceException.class,
                () -> service.changeOrderStatus(1L, "1"));

        assertTrue(error.getMessage().contains("不能从"));
        verify(kitchenOrderMapper, never()).updateOrderStatus(any(), any(), any());
    }

    @Test
    void refundCompletionClosesOrderAndClearsPaidFlag()
    {
        when(kitchenOrderMapper.selectKitchenOrderById(8L)).thenReturn(order("5", "1"));
        when(kitchenOrderMapper.updateOrderStatus(8L, "5", "4")).thenReturn(1);
        when(kitchenOrderMapper.updatePayStatus(8L, "1", "0")).thenReturn(1);

        assertEquals(1, service.changeOrderStatus(8L, "4"));
        verify(kitchenOrderMapper).updatePayStatus(8L, "1", "0");
    }

    @Test
    void changePayStatusSupportsOfflineReceiptButNotCanceledOrderReceipt()
    {
        when(kitchenOrderMapper.selectKitchenOrderById(2L)).thenReturn(order("3", "0"));
        when(kitchenOrderMapper.updatePayStatus(2L, "0", "1")).thenReturn(1);
        assertEquals(1, service.changePayStatus(2L, "1"));

        when(kitchenOrderMapper.selectKitchenOrderById(3L)).thenReturn(order("4", "0"));
        assertThrows(ServiceException.class, () -> service.changePayStatus(3L, "1"));
        verify(kitchenOrderMapper, never()).updatePayStatus(3L, "0", "1");
    }

    @Test
    void genericEditWhitelistsEditableFieldsAndDropsProtectedValues()
    {
        when(kitchenOrderMapper.selectKitchenOrderById(5L)).thenReturn(order("1", "0"));
        when(kitchenOrderMapper.updateKitchenOrder(any(KitchenOrder.class))).thenReturn(1);
        KitchenOrder input = new KitchenOrder();
        input.setId(5L);
        input.setReceiverName(" 张三 ");
        input.setOrderStatus("3");
        input.setPayStatus("1");
        input.setTotalAmount(new BigDecimal("0.01"));

        assertEquals(1, service.updateKitchenOrder(input));

        ArgumentCaptor<KitchenOrder> captor = ArgumentCaptor.forClass(KitchenOrder.class);
        verify(kitchenOrderMapper).updateKitchenOrder(captor.capture());
        KitchenOrder update = captor.getValue();
        assertEquals("张三", update.getReceiverName());
        assertNull(update.getOrderStatus());
        assertNull(update.getPayStatus());
        assertNull(update.getTotalAmount());
    }

    @Test
    void refundRejectsCompletedOrderAndOversizedReason()
    {
        KitchenOrder completed = order("3", "1");
        completed.setWxUserId(9L);
        when(kitchenOrderMapper.selectKitchenOrderById(7L)).thenReturn(completed);
        assertThrows(ServiceException.class, () -> service.applyRefund(7L, 9L, "原因"));

        KitchenOrder active = order("2", "1");
        active.setWxUserId(9L);
        when(kitchenOrderMapper.selectKitchenOrderById(10L)).thenReturn(active);
        assertThrows(ServiceException.class, () -> service.applyRefund(10L, 9L, repeat('a', 201)));
    }

    @Test
    void submitOrderCreatesAuditedShareFromDishSnapshot()
    {
        KitchenDish dish = new KitchenDish();
        dish.setId(11L);
        dish.setDishName("红烧肉");
        dish.setCover("/profile/cover.jpg");
        dish.setVirtualPrice(new BigDecimal("18.00"));
        dish.setStatus("1");
        when(kitchenDishMapper.selectKitchenDishById(11L)).thenReturn(dish);
        when(kitchenOrderMapper.insertKitchenOrder(any(KitchenOrder.class))).thenAnswer(invocation -> {
            KitchenOrder saved = invocation.getArgument(0);
            saved.setId(99L);
            return 1;
        });
        when(kitchenOrderMapper.batchInsertItem(anyList())).thenReturn(1);
        when(kitchenDishMapper.addSales(11L, 2)).thenReturn(1);
        when(kitchenSharePostMapper.insertKitchenSharePost(any(KitchenSharePost.class))).thenReturn(1);

        KitchenOrder input = new KitchenOrder();
        input.setWxUserId(6L);
        input.setServiceType("2");
        input.setShareFlag("1");
        KitchenOrderItem item = new KitchenOrderItem();
        item.setDishId(11L);
        item.setQuantity(2);
        input.setItems(Collections.singletonList(item));

        KitchenOrder saved = service.submitOrder(input);

        assertEquals(99L, saved.getId());
        ArgumentCaptor<KitchenSharePost> captor = ArgumentCaptor.forClass(KitchenSharePost.class);
        verify(kitchenSharePostMapper).insertKitchenSharePost(captor.capture());
        KitchenSharePost share = captor.getValue();
        assertEquals(6L, share.getWxUserId());
        assertTrue(share.getTitle().contains("红烧肉"));
        assertTrue(share.getContent().contains("红烧肉 × 2"));
        assertEquals("/profile/cover.jpg", share.getImages());
        assertEquals("0", share.getAuditStatus());
        assertEquals("orderId=99", share.getRemark());
    }

    @Test
    void shareAuditDisabledPublishesOrderShareDirectly()
    {
        ReflectionTestUtils.setField(service, "shareAudit", "0");
        prepareSharedOrderMocks();
        KitchenOrder input = sharedOrder();

        service.submitOrder(input);

        ArgumentCaptor<KitchenSharePost> captor = ArgumentCaptor.forClass(KitchenSharePost.class);
        verify(kitchenSharePostMapper).insertKitchenSharePost(captor.capture());
        assertEquals("1", captor.getValue().getAuditStatus());
    }

    private void prepareSharedOrderMocks()
    {
        KitchenDish dish = new KitchenDish();
        dish.setId(11L);
        dish.setDishName("红烧肉");
        dish.setVirtualPrice(BigDecimal.TEN);
        dish.setStatus("1");
        when(kitchenDishMapper.selectKitchenDishById(11L)).thenReturn(dish);
        when(kitchenOrderMapper.insertKitchenOrder(any(KitchenOrder.class))).thenAnswer(invocation -> {
            invocation.<KitchenOrder>getArgument(0).setId(100L);
            return 1;
        });
        when(kitchenOrderMapper.batchInsertItem(anyList())).thenReturn(1);
        when(kitchenDishMapper.addSales(11L, 1)).thenReturn(1);
        when(kitchenSharePostMapper.insertKitchenSharePost(any(KitchenSharePost.class))).thenReturn(1);
    }

    private KitchenOrder sharedOrder()
    {
        KitchenOrder input = new KitchenOrder();
        input.setWxUserId(6L);
        input.setServiceType("2");
        input.setShareFlag("1");
        KitchenOrderItem item = new KitchenOrderItem();
        item.setDishId(11L);
        item.setQuantity(1);
        input.setItems(Collections.singletonList(item));
        return input;
    }

    private KitchenOrder order(String orderStatus, String payStatus)
    {
        KitchenOrder order = new KitchenOrder();
        order.setId(1L);
        order.setServiceType("2");
        order.setOrderStatus(orderStatus);
        order.setPayStatus(payStatus);
        return order;
    }

    private String repeat(char value, int count)
    {
        StringBuilder text = new StringBuilder(count);
        for (int i = 0; i < count; i++)
        {
            text.append(value);
        }
        return text.toString();
    }
}
