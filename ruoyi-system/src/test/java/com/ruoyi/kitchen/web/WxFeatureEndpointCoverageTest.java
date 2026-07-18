package com.ruoyi.kitchen.web;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import com.ruoyi.kitchen.controller.wx.WxCommentController;
import com.ruoyi.kitchen.controller.wx.WxOrderController;
import com.ruoyi.kitchen.controller.wx.WxRegionApplicationController;
import com.ruoyi.kitchen.controller.wx.WxServiceApplicationController;
import com.ruoyi.kitchen.controller.wx.WxShareController;
import com.ruoyi.kitchen.controller.wx.WxSocialController;
import com.ruoyi.kitchen.domain.KitchenComment;
import com.ruoyi.kitchen.domain.KitchenOrder;
import com.ruoyi.kitchen.domain.KitchenRegionApplication;
import com.ruoyi.kitchen.domain.KitchenServiceApplication;
import com.ruoyi.kitchen.domain.KitchenSharePost;

class WxFeatureEndpointCoverageTest
{
    @Test
    void allRequiredStandaloneWritesAreGuarded() throws Exception
    {
        assertGuarded(WxOrderController.class, "submit", KitchenOrder.class, HttpServletRequest.class);
        assertGuarded(WxShareController.class, "publish", KitchenSharePost.class, HttpServletRequest.class);
        assertGuarded(WxShareController.class, "like", Long.class, HttpServletRequest.class);
        assertGuarded(WxCommentController.class, "add", KitchenComment.class, HttpServletRequest.class);
        assertGuarded(WxRegionApplicationController.class, "apply", KitchenRegionApplication.class,
                HttpServletRequest.class);
        assertGuarded(WxServiceApplicationController.class, "apply", KitchenServiceApplication.class,
                HttpServletRequest.class);
    }

    @Test
    void socialControllerGuardsAllWritesExceptSafeExitAndReadAcknowledgement()
    {
        WxFeatureRequired requirement = WxSocialController.class.getAnnotation(WxFeatureRequired.class);
        assertNotNull(requirement);
        assertTrue(requirement.writeMethodsOnly());
        assertArrayEquals(new String[] { "/api/wx/social/couple/unbind", "/api/wx/social/notifications/read" },
                requirement.excludedPaths());
    }

    @Test
    void existingOrderLifecycleWritesRemainAvailable() throws Exception
    {
        assertFalse(WxOrderController.class
                .getDeclaredMethod("complete", Long.class, HttpServletRequest.class)
                .isAnnotationPresent(WxFeatureRequired.class));
        assertFalse(WxOrderController.class
                .getDeclaredMethod("refund", Long.class, Map.class, HttpServletRequest.class)
                .isAnnotationPresent(WxFeatureRequired.class));
    }

    private static void assertGuarded(Class<?> controller, String method, Class<?>... parameterTypes)
            throws NoSuchMethodException
    {
        assertTrue(controller.getDeclaredMethod(method, parameterTypes)
                .isAnnotationPresent(WxFeatureRequired.class), controller.getSimpleName() + "." + method);
    }
}
