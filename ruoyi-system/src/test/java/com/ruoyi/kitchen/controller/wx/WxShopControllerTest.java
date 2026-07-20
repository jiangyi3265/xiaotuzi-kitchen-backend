package com.ruoyi.kitchen.controller.wx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.kitchen.domain.KitchenShop;
import com.ruoyi.kitchen.service.IKitchenShopService;

@ExtendWith(MockitoExtension.class)
class WxShopControllerTest
{
    @Mock
    private IKitchenShopService kitchenShopService;

    @InjectMocks
    private WxShopController controller;

    @Test
    void shopInfoDisablesCachingSoUpdatedImagesAreReturnedImmediately()
    {
        KitchenShop shop = new KitchenShop();
        shop.setBanner("/profile/new-banner.png");
        when(kitchenShopService.getShop()).thenReturn(shop);
        MockHttpServletResponse response = new MockHttpServletResponse();

        AjaxResult result = controller.info(response);

        assertEquals(200, result.get("code"));
        assertEquals(shop, result.get("data"));
        assertEquals("no-store, no-cache, must-revalidate, max-age=0", response.getHeader("Cache-Control"));
        assertEquals("no-cache", response.getHeader("Pragma"));
        assertEquals(0L, response.getDateHeader("Expires"));
    }
}
