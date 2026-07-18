package com.ruoyi.kitchen.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.kitchen.domain.KitchenSharePost;
import com.ruoyi.kitchen.mapper.KitchenPostLikeMapper;
import com.ruoyi.kitchen.mapper.KitchenSharePostMapper;

@ExtendWith(MockitoExtension.class)
class KitchenSharePostServiceImplTest
{
    @Mock
    private KitchenSharePostMapper postMapper;

    @Mock
    private KitchenPostLikeMapper likeMapper;

    @InjectMocks
    private KitchenSharePostServiceImpl service;

    @Test
    void auditRejectsInvalidStatusBeforeWriting()
    {
        assertThrows(ServiceException.class, () -> service.audit(5L, "9"));
        verifyNoInteractions(postMapper, likeMapper);
    }

    @Test
    void auditRejectsMissingOrDeletedPost()
    {
        when(postMapper.selectKitchenSharePostById(5L)).thenReturn(null);

        assertThrows(ServiceException.class, () -> service.audit(5L, "1"));

        verify(postMapper, never()).updateKitchenSharePost(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void auditWritesValidatedStatus()
    {
        KitchenSharePost existing = new KitchenSharePost();
        existing.setId(5L);
        when(postMapper.selectKitchenSharePostById(5L)).thenReturn(existing);
        when(postMapper.updateKitchenSharePost(org.mockito.ArgumentMatchers.any())).thenReturn(1);

        assertTrue(service.audit(5L, "1") == 1);

        verify(postMapper).updateKitchenSharePost(org.mockito.ArgumentMatchers.argThat(
                post -> Long.valueOf(5L).equals(post.getId()) && "1".equals(post.getAuditStatus())));
    }

    @Test
    void rejectsIncompleteParametersBeforeTouchingTheDatabase()
    {
        assertThrows(ServiceException.class, () -> service.toggleLike(null, 9L));
        verifyNoInteractions(postMapper, likeMapper);
    }

    @Test
    void rejectsMissingDeletedOrUnpublishedPostBeforeCreatingLike()
    {
        when(postMapper.lockPublishedPost(5L)).thenReturn(null);

        assertThrows(ServiceException.class, () -> service.toggleLike(5L, 9L));

        verify(postMapper).lockPublishedPost(5L);
        verifyNoInteractions(likeMapper);
    }

    @Test
    void createsLikeOnlyAfterPublishedPostWasLocked()
    {
        when(postMapper.lockPublishedPost(5L)).thenReturn(5L);
        when(likeMapper.countLike(5L, 9L)).thenReturn(0);
        when(likeMapper.insertLike(5L, 9L)).thenReturn(1);
        when(postMapper.addLike(5L)).thenReturn(1);

        assertTrue(service.toggleLike(5L, 9L));

        InOrder order = inOrder(postMapper, likeMapper);
        order.verify(postMapper).lockPublishedPost(5L);
        order.verify(likeMapper).countLike(5L, 9L);
        order.verify(likeMapper).insertLike(5L, 9L);
        order.verify(postMapper).addLike(5L);
    }

    @Test
    void removesExistingLikeAndDecrementsCount()
    {
        when(postMapper.lockPublishedPost(5L)).thenReturn(5L);
        when(likeMapper.countLike(5L, 9L)).thenReturn(1);
        when(likeMapper.deleteLike(5L, 9L)).thenReturn(1);
        when(postMapper.decLike(5L)).thenReturn(1);

        assertFalse(service.toggleLike(5L, 9L));

        InOrder order = inOrder(postMapper, likeMapper);
        order.verify(postMapper).lockPublishedPost(5L);
        order.verify(likeMapper).countLike(5L, 9L);
        order.verify(likeMapper).deleteLike(5L, 9L);
        order.verify(postMapper).decLike(5L);
    }

    @Test
    void failedRelationWriteDoesNotChangeCachedCounter()
    {
        when(postMapper.lockPublishedPost(5L)).thenReturn(5L);
        when(likeMapper.countLike(5L, 9L)).thenReturn(0);
        when(likeMapper.insertLike(5L, 9L)).thenReturn(0);

        assertThrows(ServiceException.class, () -> service.toggleLike(5L, 9L));

        verify(postMapper, never()).addLike(5L);
    }
}
