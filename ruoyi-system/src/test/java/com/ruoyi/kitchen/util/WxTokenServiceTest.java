package com.ruoyi.kitchen.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.kitchen.domain.KitchenWxUser;
import com.ruoyi.kitchen.mapper.KitchenWxUserMapper;

@ExtendWith(MockitoExtension.class)
class WxTokenServiceTest
{
    private static final String TOKEN = "token-7";

    @Mock
    private RedisCache redisCache;

    @Mock
    private KitchenWxUserMapper wxUserMapper;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private WxTokenService service;

    @Test
    void rejectsLegacyTokenWithoutAbsoluteIssueTime()
    {
        when(request.getHeader(WxTokenService.HEADER)).thenReturn(TOKEN);
        doReturn(7L).when(redisCache).getCacheObject("wx_login_token:" + TOKEN);
        doReturn(null).when(redisCache).getCacheObject("wx_login_token_iat:" + TOKEN);

        assertNull(service.getUserId(request));

        verify(redisCache).deleteObject("wx_login_token:" + TOKEN);
        verify(redisCache).deleteObject("wx_login_token_iat:" + TOKEN);
        verify(wxUserMapper, never()).selectKitchenWxUserById(7L);
    }

    @Test
    void acceptsActiveUserOnlyInsideAbsoluteLifetime()
    {
        KitchenWxUser user = new KitchenWxUser();
        user.setId(7L);
        user.setStatus("0");
        when(request.getHeader(WxTokenService.HEADER)).thenReturn(TOKEN);
        doReturn(7L).when(redisCache).getCacheObject("wx_login_token:" + TOKEN);
        doReturn(System.currentTimeMillis()).when(redisCache).getCacheObject("wx_login_token_iat:" + TOKEN);
        when(wxUserMapper.selectKitchenWxUserById(7L)).thenReturn(user);

        assertEquals(7L, service.getUserId(request));

        verify(redisCache).expire("wx_login_token:" + TOKEN, 7, TimeUnit.DAYS);
    }

    @Test
    void rejectsMalformedTokenState()
    {
        when(request.getHeader(WxTokenService.HEADER)).thenReturn(TOKEN);
        doReturn("not-a-user-id").when(redisCache).getCacheObject("wx_login_token:" + TOKEN);
        doReturn(System.currentTimeMillis()).when(redisCache).getCacheObject("wx_login_token_iat:" + TOKEN);

        assertNull(service.getUserId(request));

        verify(redisCache).deleteObject("wx_login_token:" + TOKEN);
        verify(redisCache).deleteObject("wx_login_token_iat:" + TOKEN);
    }
}
