package com.ruoyi.kitchen.controller.wx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.kitchen.domain.KitchenRegionApplication;
import com.ruoyi.kitchen.mapper.KitchenRegionApplicationMapper;
import com.ruoyi.kitchen.mapper.KitchenWxUserMapper;
import com.ruoyi.kitchen.util.WxTokenService;

@ExtendWith(MockitoExtension.class)
class WxRegionStatusTest
{
    @Mock private KitchenRegionApplicationMapper mapper;
    @Mock private KitchenWxUserMapper wxUserMapper;
    @Mock private WxTokenService tokenService;
    @Mock private HttpServletRequest request;
    @InjectMocks private WxRegionApplicationController controller;

    @Test
    void statusRestoresApprovedRegionWhenLocalRegionWasCleared()
    {
        KitchenRegionApplication enabled = region("广西", "百色", "右江", "1", "1");
        enabled.setAddress("中山路1号");
        when(mapper.selectEnabledRegions()).thenReturn(Arrays.asList(enabled));
        when(tokenService.getUserId(request)).thenReturn(8L);
        when(mapper.selectLatestByUser(8L)).thenReturn(enabled);
        MockHttpServletResponse response = new MockHttpServletResponse();

        AjaxResult result = controller.status(new KitchenRegionApplication(), request, response);
        Map<?, ?> data = (Map<?, ?>) result.get("data");
        Map<?, ?> restored = (Map<?, ?>) data.get("region");

        assertEquals(Boolean.TRUE, data.get("opened"));
        assertEquals("百色", restored.get("city"));
        assertEquals("中山路1号", restored.get("address"));
        assertNoCache(response);
    }

    @Test
    void statusMatchesCommonAdministrativeSuffixes()
    {
        KitchenRegionApplication enabled = region("广西", "百色", "右江", "1", "1");
        when(mapper.selectEnabledRegions()).thenReturn(Arrays.asList(enabled));
        KitchenRegionApplication query = region("广西壮族自治区", "百色市", "右江区", null, null);
        MockHttpServletResponse response = new MockHttpServletResponse();

        AjaxResult result = controller.status(query, request, response);
        Map<?, ?> data = (Map<?, ?>) result.get("data");

        assertEquals(Boolean.TRUE, data.get("opened"));
    }

    @Test
    void openedListDisablesCaching()
    {
        when(mapper.selectEnabledRegions()).thenReturn(Arrays.asList());
        MockHttpServletResponse response = new MockHttpServletResponse();

        AjaxResult result = controller.opened(response);

        assertEquals(200, result.get("code"));
        assertNoCache(response);
    }

    private static KitchenRegionApplication region(String province, String city, String district, String auditStatus, String enabled)
    {
        KitchenRegionApplication region = new KitchenRegionApplication();
        region.setProvince(province);
        region.setCity(city);
        region.setDistrict(district);
        region.setAuditStatus(auditStatus);
        region.setEnabled(enabled);
        return region;
    }

    private static void assertNoCache(MockHttpServletResponse response)
    {
        assertEquals("no-store, no-cache, must-revalidate, max-age=0", response.getHeader("Cache-Control"));
        assertEquals("no-cache", response.getHeader("Pragma"));
        assertEquals(0L, response.getDateHeader("Expires"));
        assertTrue(response.getHeaderNames().contains("Cache-Control"));
    }
}
