package com.ruoyi.kitchen.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;

@ExtendWith(MockitoExtension.class)
class KitchenAppletSwitchControllerTest
{
    private static final ObjectMapper JSON = new ObjectMapper();

    @Mock
    private ISysConfigService configService;

    private KitchenAppletSwitchController controller;

    @BeforeEach
    void setUp()
    {
        controller = spy(new KitchenAppletSwitchController(configService));
    }

    @Test
    void endpointsUseDedicatedLeastPrivilegePermissions() throws Exception
    {
        PreAuthorize read = KitchenAppletSwitchController.class.getMethod("getSwitch")
                .getAnnotation(PreAuthorize.class);
        PreAuthorize edit = KitchenAppletSwitchController.class.getMethod("updateSwitch", JsonNode.class)
                .getAnnotation(PreAuthorize.class);

        assertEquals("@ss.hasPermi('applet:switch:list')", read.value());
        assertEquals("@ss.hasPermi('applet:switch:edit')", edit.value());
    }

    @Test
    void getSwitchReturnsOnlyFixedFeatureValue()
    {
        SysConfig config = config(17L, KitchenAppletSwitchController.CONFIG_KEY, "true");
        when(configService.selectConfigList(any(SysConfig.class)))
                .thenReturn(Collections.singletonList(config));

        AjaxResult result = controller.getSwitch();

        assertTrue(result.isSuccess());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.get(AjaxResult.DATA_TAG);
        assertEquals(Boolean.TRUE, data.get("configured"));
        assertEquals(Boolean.TRUE, data.get("enabled"));

        ArgumentCaptor<SysConfig> query = ArgumentCaptor.forClass(SysConfig.class);
        verify(configService).selectConfigList(query.capture());
        assertEquals(KitchenAppletSwitchController.CONFIG_KEY, query.getValue().getConfigKey());
    }

    @Test
    void updateSwitchUsesServerSelectedIdAndFixedKey() throws Exception
    {
        doReturn("operator").when(controller).getUsername();
        SysConfig config = config(17L, KitchenAppletSwitchController.CONFIG_KEY, "false");
        when(configService.selectConfigList(any(SysConfig.class)))
                .thenReturn(Collections.singletonList(config));
        when(configService.updateConfig(any(SysConfig.class))).thenReturn(1);

        AjaxResult result = controller.updateSwitch(JSON.readTree("{\"enabled\":true}"));

        assertTrue(result.isSuccess());
        ArgumentCaptor<SysConfig> update = ArgumentCaptor.forClass(SysConfig.class);
        verify(configService).updateConfig(update.capture());
        assertEquals(17L, update.getValue().getConfigId());
        assertEquals(KitchenAppletSwitchController.CONFIG_KEY, update.getValue().getConfigKey());
        assertEquals("true", update.getValue().getConfigValue());
        assertEquals("operator", update.getValue().getUpdateBy());
    }

    @Test
    void nonBooleanOrClientControlledConfigFieldsAreRejected() throws Exception
    {
        assertTrue(controller.updateSwitch(JSON.readTree("{\"enabled\":\"true\"}")).isError());
        assertTrue(controller.updateSwitch(JSON.readTree(
                "{\"enabled\":true,\"configKey\":\"sys.account.captchaEnabled\"}")).isError());
        assertTrue(controller.updateSwitch(JSON.readTree("{\"enabled\":true,\"configId\":1}")).isError());
        assertTrue(controller.updateSwitch(null).isError());

        verify(configService, never()).updateConfig(any(SysConfig.class));
    }

    @Test
    void missingConfigIsReportedWithoutCreatingOrUpdatingAnything() throws Exception
    {
        when(configService.selectConfigList(any(SysConfig.class))).thenReturn(Collections.emptyList());

        AjaxResult read = controller.getSwitch();
        AjaxResult write = controller.updateSwitch(JSON.readTree("{\"enabled\":false}"));

        assertTrue(read.isSuccess());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) read.get(AjaxResult.DATA_TAG);
        assertFalse((Boolean) data.get("configured"));
        assertTrue(write.isError());
        verify(configService, never()).updateConfig(any(SysConfig.class));
    }

    private static SysConfig config(Long id, String key, String value)
    {
        SysConfig config = new SysConfig();
        config.setConfigId(id);
        config.setConfigKey(key);
        config.setConfigValue(value);
        return config;
    }
}
