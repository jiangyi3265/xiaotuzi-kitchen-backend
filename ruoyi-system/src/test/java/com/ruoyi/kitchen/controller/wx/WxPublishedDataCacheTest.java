package com.ruoyi.kitchen.controller.wx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.github.pagehelper.PageHelper;
import com.ruoyi.kitchen.domain.KitchenCategory;
import com.ruoyi.kitchen.domain.KitchenDish;
import com.ruoyi.kitchen.service.IKitchenCategoryService;
import com.ruoyi.kitchen.service.IKitchenDishService;
import com.ruoyi.kitchen.util.WxTokenService;

@ExtendWith(MockitoExtension.class)
class WxPublishedDataCacheTest
{
    @Mock private IKitchenDishService dishService;
    @Mock private IKitchenCategoryService categoryService;
    @Mock private WxTokenService tokenService;
    @InjectMocks private WxDishController dishController;
    @InjectMocks private WxCategoryController categoryController;

    @AfterEach
    void clearRequestContext()
    {
        PageHelper.clearPage();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void publishedDishListDisablesCaching()
    {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        when(dishService.selectKitchenDishList(any(KitchenDish.class))).thenReturn(Collections.emptyList());
        MockHttpServletResponse response = new MockHttpServletResponse();

        dishController.list(new KitchenDish(), response);

        assertNoCache(response);
    }

    @Test
    void categoryTreeDisablesCaching()
    {
        when(categoryService.buildCategoryTree(any(KitchenCategory.class))).thenReturn(Collections.emptyList());
        MockHttpServletResponse response = new MockHttpServletResponse();

        categoryController.tree(response);

        assertNoCache(response);
    }

    private static void assertNoCache(MockHttpServletResponse response)
    {
        assertEquals("no-store, no-cache, must-revalidate, max-age=0", response.getHeader("Cache-Control"));
        assertEquals("no-cache", response.getHeader("Pragma"));
        assertEquals(0L, response.getDateHeader("Expires"));
    }
}
