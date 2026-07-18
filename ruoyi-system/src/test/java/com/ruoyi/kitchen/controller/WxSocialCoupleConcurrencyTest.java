package com.ruoyi.kitchen.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.kitchen.controller.wx.WxSocialController;
import com.ruoyi.kitchen.mapper.KitchenSocialMapper;
import com.ruoyi.kitchen.util.WxTokenService;

@ExtendWith(MockitoExtension.class)
class WxSocialCoupleConcurrencyTest
{
    @Mock
    private KitchenSocialMapper mapper;

    @Mock
    private WxTokenService token;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private WxSocialController controller;

    @Test
    void createLocksStableUserRowBeforeRecheckingAndInserting()
    {
        Map<String, Object> body = new HashMap<>();
        body.put("startDate", "2020-01-01");
        when(token.getRequiredUserId(request)).thenReturn(7L);
        when(mapper.lockWxUser(7L)).thenReturn(7L);
        when(mapper.selectCoupleByUser(7L)).thenReturn(null);

        AjaxResult result = controller.createCouple(body, request);

        assertTrue(result.isSuccess());
        InOrder order = inOrder(mapper);
        order.verify(mapper).lockWxUser(7L);
        order.verify(mapper).selectCoupleByUser(7L);
        order.verify(mapper).insertCouple(body);
    }

    @Test
    void createRejectsExistingActiveRelationshipAfterTakingUserLock()
    {
        when(token.getRequiredUserId(request)).thenReturn(8L);
        when(mapper.lockWxUser(8L)).thenReturn(8L);
        when(mapper.selectCoupleByUser(8L)).thenReturn(row("id", 10L));

        AjaxResult result = controller.createCouple(new HashMap<>(), request);

        assertTrue(result.isError());
        verify(mapper, never()).insertCouple(org.mockito.ArgumentMatchers.anyMap());
    }

    @Test
    void joinLocksUserThenRechecksThenLocksTargetSpaceBeforeBinding()
    {
        Map<String, Object> body = row("inviteCode", "ABC123");
        Map<String, Object> couple = row("id", 99L);
        when(token.getRequiredUserId(request)).thenReturn(9L);
        when(mapper.lockWxUser(9L)).thenReturn(9L);
        when(mapper.selectCoupleByUser(9L)).thenReturn(null);
        when(mapper.selectCoupleByCodeForUpdate("ABC123")).thenReturn(couple);
        when(mapper.bindCouplePartner(99L, 9L)).thenReturn(1);

        AjaxResult result = controller.joinCouple(body, request);

        assertTrue(result.isSuccess());
        InOrder order = inOrder(mapper);
        order.verify(mapper).lockWxUser(9L);
        order.verify(mapper).selectCoupleByUser(9L);
        order.verify(mapper).selectCoupleByCodeForUpdate("ABC123");
        order.verify(mapper).bindCouplePartner(99L, 9L);
        verify(mapper, never()).selectCoupleByCode("ABC123");
    }

    @Test
    void joinDoesNotLockOrMutateTargetWhenUserAlreadyHasActiveRelationship()
    {
        Map<String, Object> body = row("inviteCode", "ABC123");
        when(token.getRequiredUserId(request)).thenReturn(11L);
        when(mapper.lockWxUser(11L)).thenReturn(11L);
        when(mapper.selectCoupleByUser(11L)).thenReturn(row("id", 12L));

        AjaxResult result = controller.joinCouple(body, request);

        assertTrue(result.isError());
        verify(mapper, never()).selectCoupleByCodeForUpdate("ABC123");
        verify(mapper, never()).bindCouplePartner(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void createAndJoinUseReadCommittedTransactions() throws Exception
    {
        Transactional create = WxSocialController.class
                .getDeclaredMethod("createCouple", Map.class, HttpServletRequest.class)
                .getAnnotation(Transactional.class);
        Transactional join = WxSocialController.class
                .getDeclaredMethod("joinCouple", Map.class, HttpServletRequest.class)
                .getAnnotation(Transactional.class);

        assertNotNull(create);
        assertNotNull(join);
        assertEquals(Isolation.READ_COMMITTED, create.isolation());
        assertEquals(Isolation.READ_COMMITTED, join.isolation());
    }

    private static Map<String, Object> row(String key, Object value)
    {
        Map<String, Object> row = new HashMap<>();
        row.put(key, value);
        return row;
    }
}
