package com.ruoyi.kitchen.controller.wx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.kitchen.domain.KitchenComment;
import com.ruoyi.kitchen.domain.KitchenSharePost;
import com.ruoyi.kitchen.mapper.KitchenPostLikeMapper;
import com.ruoyi.kitchen.service.IKitchenCommentService;
import com.ruoyi.kitchen.service.IKitchenSharePostService;
import com.ruoyi.kitchen.util.WxTokenService;

@ExtendWith(MockitoExtension.class)
class WxShareAutoPublishTest
{
    @Mock
    private IKitchenSharePostService shareService;

    @Mock
    private IKitchenCommentService commentService;

    @Mock
    private KitchenPostLikeMapper kitchenPostLikeMapper;

    @Mock
    private WxTokenService wxTokenService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private WxShareController shareController;

    @InjectMocks
    private WxCommentController commentController;

    @Test
    void shareIsPublishedImmediatelyWhenReviewIsDisabled()
    {
        ReflectionTestUtils.setField(shareController, "shareAudit", "0");
        when(wxTokenService.getRequiredUserId(request)).thenReturn(7L);
        KitchenSharePost post = new KitchenSharePost();
        post.setContent("今天的晚餐");

        AjaxResult result = shareController.publish(post, request);

        assertEquals(200, result.get("code"));
        assertEquals("1", post.getAuditStatus());
        assertSame(post, result.get("data"));
        verify(shareService).insertKitchenSharePost(post);
    }

    @Test
    void commentIsPublishedImmediatelyWhenReviewIsDisabled()
    {
        ReflectionTestUtils.setField(commentController, "shareAudit", "0");
        when(wxTokenService.getRequiredUserId(request)).thenReturn(8L);
        KitchenSharePost publishedPost = new KitchenSharePost();
        publishedPost.setAuditStatus("1");
        when(shareService.selectKitchenSharePostById(11L)).thenReturn(publishedPost);
        KitchenComment comment = new KitchenComment();
        comment.setPostId(11L);
        comment.setContent("看起来很好吃");

        AjaxResult result = commentController.add(comment, request);

        assertEquals(200, result.get("code"));
        assertEquals("1", comment.getAuditStatus());
        assertEquals("1", ((Map<?, ?>) result.get("data")).get("status"));
        verify(commentService).addComment(comment);
    }
}
