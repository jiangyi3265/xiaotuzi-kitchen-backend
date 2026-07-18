package com.ruoyi.kitchen.service.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import com.ruoyi.kitchen.domain.KitchenWxUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.kitchen.mapper.KitchenWxUserMapper;

@ExtendWith(MockitoExtension.class)
class KitchenWxUserServiceImplTest
{
    @Mock
    private KitchenWxUserMapper mapper;

    @InjectMocks
    private KitchenWxUserServiceImpl service;

    @Test
    void updateRejectsOwnerValueOutsideZeroAndOne()
    {
        KitchenWxUser user = user(7L, "openid-7");
        user.setIsOwner("Y");

        assertThrows(ServiceException.class, () -> service.updateKitchenWxUser(user));

        verify(mapper, never()).updateKitchenWxUser(any());
    }

    @Test
    void existingActiveUserIsReturnedWithoutWriting()
    {
        KitchenWxUser existing = user(7L, "openid-7");
        when(mapper.selectKitchenWxUserByOpenid("openid-7")).thenReturn(existing);

        assertSame(existing, service.registerOrGet("openid-7", "union-7"));
        verify(mapper, never()).restoreKitchenWxUserByOpenid(any());
        verify(mapper, never()).insertKitchenWxUser(any());
    }

    @Test
    void deletedUserIsRestoredWithTheSameIdentity()
    {
        KitchenWxUser restored = user(8L, "openid-8");
        when(mapper.selectKitchenWxUserByOpenid("openid-8")).thenReturn(null, restored);
        when(mapper.restoreKitchenWxUserByOpenid("openid-8")).thenReturn(1);

        assertSame(restored, service.registerOrGet("openid-8", ""));
        verify(mapper, never()).insertKitchenWxUser(any());
    }

    @Test
    void newUserIsReadBackAfterInsert()
    {
        KitchenWxUser persisted = user(9L, "openid-9");
        persisted.setIsOwner("0");
        when(mapper.selectKitchenWxUserByOpenid("openid-9")).thenReturn(null);
        when(mapper.restoreKitchenWxUserByOpenid("openid-9")).thenReturn(0);
        when(mapper.insertKitchenWxUser(any())).thenAnswer(invocation -> {
            KitchenWxUser inserted = invocation.getArgument(0);
            inserted.setId(9L);
            return 1;
        });
        when(mapper.selectKitchenWxUserById(9L)).thenReturn(persisted);

        assertSame(persisted, service.registerOrGet("openid-9", "union-9"));
    }

    @Test
    void duplicateInsertFromConcurrentLoginConvergesOnActiveUser()
    {
        KitchenWxUser concurrent = user(10L, "openid-10");
        when(mapper.selectKitchenWxUserByOpenid("openid-10")).thenReturn(null, concurrent);
        when(mapper.restoreKitchenWxUserByOpenid("openid-10")).thenReturn(0);
        when(mapper.insertKitchenWxUser(any())).thenThrow(new DuplicateKeyException("uk_openid"));

        assertSame(concurrent, service.registerOrGet("openid-10", ""));
    }

    @Test
    void duplicateInsertCanConvergeOnAConcurrentRestore()
    {
        KitchenWxUser restored = user(11L, "openid-11");
        when(mapper.selectKitchenWxUserByOpenid("openid-11")).thenReturn(null, null, restored);
        when(mapper.restoreKitchenWxUserByOpenid("openid-11")).thenReturn(0, 1);
        when(mapper.insertKitchenWxUser(any())).thenThrow(new DuplicateKeyException("uk_openid"));

        assertSame(restored, service.registerOrGet("openid-11", ""));
    }

    private static KitchenWxUser user(Long id, String openid)
    {
        KitchenWxUser user = new KitchenWxUser();
        user.setId(id);
        user.setOpenid(openid);
        user.setStatus("0");
        return user;
    }
}
