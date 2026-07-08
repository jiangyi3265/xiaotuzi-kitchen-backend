package com.ruoyi.kitchen.util;

import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.kitchen.domain.KitchenWxUser;
import com.ruoyi.kitchen.mapper.KitchenWxUserMapper;

/**
 * 小程序登录 Token 服务（基于 Redis，独立于后台管理员 JWT，互不影响）
 * 请求头使用 wx-token 传递。
 *
 * @author ruoyi
 */
@Component
public class WxTokenService
{
    /** 请求头名称 */
    public static final String HEADER = "wx-token";

    /** Redis key 前缀 */
    private static final String PREFIX = "wx_login_token:";

    /** 签发时间 key 前缀（用于绝对有效期控制） */
    private static final String IAT_PREFIX = "wx_login_token_iat:";

    /** 滑动有效期（天）：每次访问续期 */
    private static final long EXPIRE_DAYS = 7;

    /** 绝对有效期（天）：无论如何续期，超过此时长强制重新登录 */
    private static final long ABSOLUTE_DAYS = 30;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private KitchenWxUserMapper wxUserMapper;

    /**
     * 校验并返回"店主"用户ID：非登录抛 401，非店主抛 403。
     * 用于小程序端的菜品/分类/厨房设置等管理写接口，防止普通顾客越权。
     */
    public Long getRequiredOwnerId(HttpServletRequest request)
    {
        Long userId = getRequiredUserId(request);
        KitchenWxUser user = wxUserMapper.selectKitchenWxUserById(userId);
        if (user == null || !"1".equals(user.getIsOwner()))
        {
            throw new ServiceException("无权操作，仅店主可管理", 403);
        }
        return userId;
    }

    /**
     * 创建 token 并写入 Redis
     */
    public String createToken(Long wxUserId)
    {
        String token = IdUtils.fastSimpleUUID();
        redisCache.setCacheObject(PREFIX + token, wxUserId, (int) EXPIRE_DAYS, TimeUnit.DAYS);
        // 记录签发时间，用于绝对有效期控制（该 key 不随访问续期）
        redisCache.setCacheObject(IAT_PREFIX + token, System.currentTimeMillis(), (int) ABSOLUTE_DAYS, TimeUnit.DAYS);
        return token;
    }

    /**
     * 从请求中解析当前小程序用户ID，未登录抛异常
     */
    public Long getRequiredUserId(HttpServletRequest request)
    {
        Long userId = getUserId(request);
        if (userId == null)
        {
            throw new ServiceException("未登录或登录已过期，请重新登录", 401);
        }
        return userId;
    }

    /**
     * 从请求中解析当前小程序用户ID，未登录返回 null
     */
    public Long getUserId(HttpServletRequest request)
    {
        String token = request.getHeader(HEADER);
        if (StringUtils.isEmpty(token))
        {
            return null;
        }
        Object val = redisCache.getCacheObject(PREFIX + token);
        if (val == null)
        {
            return null;
        }
        // 绝对有效期校验：超过 ABSOLUTE_DAYS 强制失效，防止被盗 token 无限续期
        Object iat = redisCache.getCacheObject(IAT_PREFIX + token);
        if (iat != null)
        {
            long issuedAt = Long.parseLong(iat.toString());
            if (System.currentTimeMillis() - issuedAt > ABSOLUTE_DAYS * 24L * 60 * 60 * 1000)
            {
                redisCache.deleteObject(PREFIX + token);
                redisCache.deleteObject(IAT_PREFIX + token);
                return null;
            }
        }
        // 滑动续期（在绝对有效期窗口内）
        redisCache.expire(PREFIX + token, EXPIRE_DAYS, TimeUnit.DAYS);
        return Long.valueOf(val.toString());
    }

    /**
     * 退出登录
     */
    public void removeToken(HttpServletRequest request)
    {
        String token = request.getHeader(HEADER);
        if (StringUtils.isNotEmpty(token))
        {
            redisCache.deleteObject(PREFIX + token);
            redisCache.deleteObject(IAT_PREFIX + token);
        }
    }
}
