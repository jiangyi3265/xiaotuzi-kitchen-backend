package com.ruoyi.kitchen.controller.wx;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.kitchen.mapper.KitchenSocialMapper;
import com.ruoyi.kitchen.util.WxTokenService;

@ExtendWith(MockitoExtension.class)
class WxSocialControllerTest
{
    @Mock
    private KitchenSocialMapper mapper;

    @Mock
    private WxTokenService tokenService;

    private WxSocialController controller;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp()
    {
        controller = new WxSocialController();
        ReflectionTestUtils.setField(controller, "mapper", mapper);
        ReflectionTestUtils.setField(controller, "token", tokenService);
        request = new MockHttpServletRequest();
    }

    @Test
    void leaderboardRejectsAnonymousRequestBeforeReadingNicknames()
    {
        when(tokenService.getRequiredUserId(request))
                .thenThrow(new ServiceException("unauthorized", HttpStatus.UNAUTHORIZED));

        assertThrows(ServiceException.class, () -> controller.leaderboard(request));

        verify(mapper, never()).selectCoupleLeaderboard();
    }

    @Test
    void leaderboardReturnsRowsForAuthenticatedUser()
    {
        List<Map<String, Object>> rows = Collections.singletonList(
                Collections.<String, Object>singletonMap("nickname", "user"));
        when(tokenService.getRequiredUserId(request)).thenReturn(7L);
        when(mapper.selectCoupleLeaderboard()).thenReturn(rows);

        AjaxResult result = controller.leaderboard(request);

        assertEquals(HttpStatus.SUCCESS, result.get(AjaxResult.CODE_TAG));
        assertSame(rows, result.get(AjaxResult.DATA_TAG));
        verify(tokenService).getRequiredUserId(request);
    }
}
