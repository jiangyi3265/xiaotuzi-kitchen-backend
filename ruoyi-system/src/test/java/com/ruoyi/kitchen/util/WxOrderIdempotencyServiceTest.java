package com.ruoyi.kitchen.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.kitchen.domain.KitchenOrder;
import com.ruoyi.kitchen.domain.KitchenOrderItem;
import com.ruoyi.kitchen.util.WxOrderIdempotencyService.Submission;

@ExtendWith(MockitoExtension.class)
class WxOrderIdempotencyServiceTest
{
    @Mock
    private RedisCache redisCache;

    private WxOrderIdempotencyService service;

    @BeforeEach
    void setUp()
    {
        service = new WxOrderIdempotencyService(redisCache, new ObjectMapper());
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void tearDown()
    {
        if (TransactionSynchronizationManager.isSynchronizationActive())
        {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void committedSubmissionCachesTheSuccessfulOrderResult()
    {
        when(redisCache.setCacheObjectIfAbsent(anyString(), anyString(), eq(30L), eq(TimeUnit.SECONDS)))
                .thenReturn(true);
        when(redisCache.replaceObjectIfEquals(anyString(), anyString(), anyString(), eq(15L), eq(TimeUnit.SECONDS)))
                .thenReturn(true);
        Submission submission = service.begin(7L, order());
        AjaxResult result = AjaxResult.success("下单成功").put("orderId", 99L).put("orderNo", "NO99");

        service.markSuccessful(submission, result);
        List<TransactionSynchronization> callbacks = TransactionSynchronizationManager.getSynchronizations();
        callbacks.forEach(TransactionSynchronization::afterCommit);
        callbacks.forEach(callback -> callback.afterCompletion(TransactionSynchronization.STATUS_COMMITTED));

        assertTrue(submission.isAcquired());
        verify(redisCache).replaceObjectIfEquals(anyString(), anyString(),
                org.mockito.ArgumentMatchers.startsWith("SUCCESS:"), eq(15L), eq(TimeUnit.SECONDS));
    }

    @Test
    void retryReturnsThePreviouslyCommittedOrder()
            throws Exception
    {
        AjaxResult previous = AjaxResult.success("下单成功").put("orderId", 99L).put("orderNo", "NO99");
        when(redisCache.setCacheObjectIfAbsent(anyString(), anyString(), eq(30L), eq(TimeUnit.SECONDS)))
                .thenReturn(false);
        when(redisCache.getCacheObject(anyString()))
                .thenReturn("SUCCESS:" + new ObjectMapper().writeValueAsString(previous));

        Submission submission = service.begin(7L, order());

        assertFalse(submission.isAcquired());
        assertNotNull(submission.getCachedResult());
        assertEquals(99, ((Number) submission.getCachedResult().get("orderId")).intValue());
        assertEquals("NO99", submission.getCachedResult().get("orderNo"));
    }

    @Test
    void concurrentSubmissionIsRejectedWhileFirstRequestIsProcessing()
    {
        when(redisCache.setCacheObjectIfAbsent(anyString(), anyString(), eq(30L), eq(TimeUnit.SECONDS)))
                .thenReturn(false);
        when(redisCache.getCacheObject(anyString())).thenReturn("PROCESSING:another-request");

        Submission submission = service.begin(7L, order());

        assertFalse(submission.isAcquired());
        assertNull(submission.getCachedResult());
    }

    @Test
    void rolledBackSubmissionReleasesOnlyItsOwnProcessingLock()
    {
        when(redisCache.setCacheObjectIfAbsent(anyString(), anyString(), eq(30L), eq(TimeUnit.SECONDS)))
                .thenReturn(true);
        when(redisCache.deleteObjectIfEquals(anyString(), anyString())).thenReturn(true);

        Submission submission = service.begin(7L, order());
        List<TransactionSynchronization> callbacks = TransactionSynchronizationManager.getSynchronizations();
        callbacks.forEach(callback -> callback.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));

        assertTrue(submission.isAcquired());
        verify(redisCache).deleteObjectIfEquals(anyString(),
                org.mockito.ArgumentMatchers.startsWith("PROCESSING:"));
    }

    @Test
    void committedValidationFailureAlsoReleasesTheProcessingLock()
    {
        when(redisCache.setCacheObjectIfAbsent(anyString(), anyString(), eq(30L), eq(TimeUnit.SECONDS)))
                .thenReturn(true);
        when(redisCache.deleteObjectIfEquals(anyString(), anyString())).thenReturn(true);

        service.begin(7L, order());
        List<TransactionSynchronization> callbacks = TransactionSynchronizationManager.getSynchronizations();
        callbacks.forEach(TransactionSynchronization::afterCommit);
        callbacks.forEach(callback -> callback.afterCompletion(TransactionSynchronization.STATUS_COMMITTED));

        verify(redisCache).deleteObjectIfEquals(anyString(),
                org.mockito.ArgumentMatchers.startsWith("PROCESSING:"));
    }

    private static KitchenOrder order()
    {
        KitchenOrder order = new KitchenOrder();
        order.setServiceType("2");
        order.setShareFlag("0");
        KitchenOrderItem item = new KitchenOrderItem();
        item.setDishId(5L);
        item.setQuantity(2);
        order.setItems(java.util.Collections.singletonList(item));
        return order;
    }
}
