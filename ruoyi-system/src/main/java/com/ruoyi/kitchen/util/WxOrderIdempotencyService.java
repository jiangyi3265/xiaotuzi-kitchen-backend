package com.ruoyi.kitchen.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.kitchen.domain.KitchenOrder;
import com.ruoyi.kitchen.domain.KitchenOrderItem;

/**
 * 小程序普通订单幂等保护。相同用户、相同业务参数的网络重试会复用第一次成功结果，
 * 不依赖小程序额外生成请求号。
 */
@Component
public class WxOrderIdempotencyService
{
    private static final Logger log = LoggerFactory.getLogger(WxOrderIdempotencyService.class);

    private static final String PREFIX = "wx_order_submit:";

    private static final String PROCESSING_PREFIX = "PROCESSING:";

    private static final String SUCCESS_PREFIX = "SUCCESS:";

    private static final long PROCESSING_SECONDS = 30;

    private static final long SUCCESS_SECONDS = 15;

    private final RedisCache redisCache;

    private final ObjectMapper objectMapper;

    public WxOrderIdempotencyService(RedisCache redisCache, ObjectMapper objectMapper)
    {
        this.redisCache = redisCache;
        this.objectMapper = objectMapper;
    }

    public Submission begin(Long userId, KitchenOrder order)
    {
        if (userId == null || order == null)
        {
            throw new ServiceException("订单参数不完整");
        }
        String key = PREFIX + userId + ":" + fingerprint(order);
        String owner = PROCESSING_PREFIX + IdUtils.fastSimpleUUID();
        for (int attempt = 0; attempt < 2; attempt++)
        {
            if (redisCache.setCacheObjectIfAbsent(key, owner, PROCESSING_SECONDS, TimeUnit.SECONDS))
            {
                Submission submission = Submission.acquired(key, owner);
                registerTransactionCallback(submission);
                return submission;
            }
            Object existing = redisCache.getCacheObject(key);
            if (existing == null)
            {
                continue;
            }
            String value = existing.toString();
            if (value.startsWith(SUCCESS_PREFIX))
            {
                try
                {
                    Map<String, Object> cached = objectMapper.readValue(
                            value.substring(SUCCESS_PREFIX.length()), new TypeReference<Map<String, Object>>() { });
                    AjaxResult result = new AjaxResult();
                    result.putAll(cached);
                    return Submission.cached(result);
                }
                catch (JsonProcessingException e)
                {
                    redisCache.deleteObjectIfEquals(key, existing);
                    log.warn("订单幂等缓存损坏，已清理: {}", key);
                    continue;
                }
            }
            return Submission.busy();
        }
        return Submission.busy();
    }

    public void markSuccessful(Submission submission, AjaxResult result)
    {
        if (submission == null || !submission.acquired)
        {
            return;
        }
        try
        {
            submission.successValue = SUCCESS_PREFIX + objectMapper.writeValueAsString(result);
        }
        catch (JsonProcessingException e)
        {
            throw new ServiceException("订单结果保存失败，请重试");
        }
    }

    private void registerTransactionCallback(Submission submission)
    {
        if (!TransactionSynchronizationManager.isSynchronizationActive())
        {
            redisCache.deleteObjectIfEquals(submission.key, submission.owner);
            throw new ServiceException("订单事务未就绪，请稍后重试");
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
        {
            @Override
            public void afterCommit()
            {
                if (submission.successValue == null)
                {
                    return;
                }
                try
                {
                    if (!redisCache.replaceObjectIfEquals(submission.key, submission.owner,
                            submission.successValue, SUCCESS_SECONDS, TimeUnit.SECONDS))
                    {
                        log.warn("订单已提交，但幂等结果缓存未能写入: {}", submission.key);
                    }
                }
                catch (Exception e)
                {
                    log.error("订单已提交，但幂等结果缓存写入异常: {}", submission.key, e);
                }
            }

            @Override
            public void afterCompletion(int status)
            {
                if (status != TransactionSynchronization.STATUS_COMMITTED || submission.successValue == null)
                {
                    try
                    {
                        redisCache.deleteObjectIfEquals(submission.key, submission.owner);
                    }
                    catch (Exception e)
                    {
                        log.warn("订单幂等处理中标记清理失败: {}", submission.key, e);
                    }
                }
            }
        });
    }

    private String fingerprint(KitchenOrder order)
    {
        StringBuilder value = new StringBuilder();
        append(value, order.getServiceType());
        append(value, order.getChefId());
        append(value, order.getRiderId());
        append(value, clean(order.getReceiverName()));
        append(value, clean(order.getReceiverPhone()));
        append(value, clean(order.getReceiverAddress()));
        append(value, clean(order.getRemark()));
        append(value, order.getShareFlag());
        append(value, order.getRemoteFeed());
        append(value, order.getCoupleOrder());
        append(value, order.getGroupRoomId());

        List<KitchenOrderItem> items = new ArrayList<>();
        if (order.getItems() != null)
        {
            for (KitchenOrderItem item : order.getItems())
            {
                if (item != null)
                {
                    items.add(item);
                }
            }
        }
        items.sort(Comparator.comparing((KitchenOrderItem item) -> String.valueOf(item.getDishId()))
                .thenComparing(item -> StringUtils.defaultString(item.getSpecJson()))
                .thenComparing(item -> String.valueOf(item.getQuantity())));
        for (KitchenOrderItem item : items)
        {
            append(value, item.getDishId());
            append(value, item.getSpecJson());
            append(value, item.getQuantity());
        }
        try
        {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte part : digest)
            {
                hex.append(String.format("%02x", part & 0xff));
            }
            return hex.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private static String clean(String value)
    {
        return value == null ? "" : value.trim();
    }

    private static void append(StringBuilder target, Object value)
    {
        String text = value == null ? "" : value.toString();
        target.append(text.length()).append(':').append(text).append('|');
    }

    public static class Submission
    {
        private final String key;
        private final String owner;
        private final boolean acquired;
        private final AjaxResult cachedResult;
        private String successValue;

        private Submission(String key, String owner, boolean acquired, AjaxResult cachedResult)
        {
            this.key = key;
            this.owner = owner;
            this.acquired = acquired;
            this.cachedResult = cachedResult;
        }

        static Submission acquired(String key, String owner)
        {
            return new Submission(key, owner, true, null);
        }

        static Submission cached(AjaxResult result)
        {
            return new Submission(null, null, false, result);
        }

        static Submission busy()
        {
            return new Submission(null, null, false, null);
        }

        public boolean isAcquired()
        {
            return acquired;
        }

        public AjaxResult getCachedResult()
        {
            return cachedResult;
        }
    }
}
