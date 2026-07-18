package com.ruoyi.kitchen.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.kitchen.mapper.KitchenSocialMapper;

@ExtendWith(MockitoExtension.class)
class KitchenSocialControllerTest
{
    @Mock
    private KitchenSocialMapper mapper;

    @InjectMocks
    private KitchenSocialController controller;

    @Test
    void activeGroupCanOnlyBeClosedByAdminEndpoint()
    {
        when(mapper.closeActiveGroup(10L)).thenReturn(1);

        AjaxResult result = controller.groupStatus(request("10", "0"));

        assertTrue(result.isSuccess());
        assertEquals("聚餐房间已关闭", result.get(AjaxResult.MSG_TAG));
        verify(mapper).closeActiveGroup(10L);
    }

    @Test
    void groupEndpointRejectsRevivalUserFinishAndInvalidIds()
    {
        assertTrue(controller.groupStatus(null).isError());
        assertTrue(controller.groupStatus(request("", "0")).isError());
        assertTrue(controller.groupStatus(request("not-a-number", "0")).isError());
        assertTrue(controller.groupStatus(request("-1", "0")).isError());
        assertTrue(controller.groupStatus(request("10", "1")).isError());
        assertTrue(controller.groupStatus(request("10", "2")).isError());

        verify(mapper, never()).closeActiveGroup(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void terminalOrMissingGroupCannotBeClosedAgain()
    {
        when(mapper.closeActiveGroup(11L)).thenReturn(0);

        AjaxResult result = controller.groupStatus(request("11", "0"));

        assertTrue(result.isError());
        assertEquals("聚餐房间不存在或已经结束/关闭", result.get(AjaxResult.MSG_TAG));
    }

    @Test
    void activeCoupleCanOnlyBeClosed()
    {
        when(mapper.closeActiveCouple(20L)).thenReturn(1);

        AjaxResult result = controller.coupleStatus(request("20", "0"));

        assertTrue(result.isSuccess());
        assertEquals("情侣空间已关闭", result.get(AjaxResult.MSG_TAG));
        verify(mapper).closeActiveCouple(20L);
    }

    @Test
    void inactiveCoupleCannotBeReactivated()
    {
        AjaxResult result = controller.coupleStatus(request("20", "1"));

        assertTrue(result.isError());
        assertEquals("已解除的情侣空间不允许重新激活", result.get(AjaxResult.MSG_TAG));
        verify(mapper, never()).closeActiveCouple(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void missingInvalidIdAndUnsupportedStatusAreRejectedWithoutWriting()
    {
        assertTrue(controller.coupleStatus(null).isError());
        assertTrue(controller.coupleStatus(request("not-a-number", "0")).isError());
        assertTrue(controller.coupleStatus(request("-1", "0")).isError());
        assertTrue(controller.coupleStatus(request("20", "2")).isError());

        verify(mapper, never()).closeActiveCouple(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void nonexistentOrAlreadyClosedCoupleReturnsExplicitError()
    {
        when(mapper.closeActiveCouple(21L)).thenReturn(0);

        AjaxResult result = controller.coupleStatus(request("21", "0"));

        assertTrue(result.isError());
        assertEquals("情侣空间不存在或已解除", result.get(AjaxResult.MSG_TAG));
    }

    private static Map<String, String> request(String id, String status)
    {
        Map<String, String> body = new HashMap<>();
        body.put("id", id);
        body.put("status", status);
        return body;
    }
}
