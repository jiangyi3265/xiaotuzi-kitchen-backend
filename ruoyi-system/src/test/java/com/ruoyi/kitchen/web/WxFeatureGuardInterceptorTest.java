package com.ruoyi.kitchen.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.ruoyi.system.service.ISysConfigService;

@ExtendWith(MockitoExtension.class)
class WxFeatureGuardInterceptorTest
{
    @Mock
    private ISysConfigService configService;

    private WxFeatureGuardInterceptor interceptor;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp()
    {
        interceptor = new WxFeatureGuardInterceptor(configService);
        mockMvc = MockMvcBuilders.standaloneSetup(new MethodGuardController(), new ClassGuardController())
                .addInterceptors(interceptor)
                .build();
    }

    @Test
    void disabledSwitchReturnsHttp403AndBusinessMessage() throws Exception
    {
        when(configService.selectConfigByKey(WxFeatureGuardInterceptor.CONFIG_KEY)).thenReturn("false");

        mockMvc.perform(post("/method/guarded"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.msg").value(WxFeatureGuardInterceptor.DISABLED_MESSAGE));
    }

    @Test
    void enabledSwitchAllowsGuardedHandler() throws Exception
    {
        when(configService.selectConfigByKey(WxFeatureGuardInterceptor.CONFIG_KEY)).thenReturn(" TRUE ");

        mockMvc.perform(post("/method/guarded"))
                .andExpect(status().isOk())
                .andExpect(content().string("guarded"));
    }

    @Test
    void unguardedHandlerDoesNotReadSwitch() throws Exception
    {
        mockMvc.perform(post("/method/unguarded"))
                .andExpect(status().isOk())
                .andExpect(content().string("unguarded"));

        verifyNoInteractions(configService);
    }

    @Test
    void classGuardBlocksWritesButKeepsQueriesAndExplicitExitAvailable() throws Exception
    {
        when(configService.selectConfigByKey(WxFeatureGuardInterceptor.CONFIG_KEY)).thenReturn("");

        mockMvc.perform(get("/class/query"))
                .andExpect(status().isOk())
                .andExpect(content().string("query"));
        mockMvc.perform(post("/class/unbind"))
                .andExpect(status().isOk())
                .andExpect(content().string("unbind"));
        mockMvc.perform(post("/class/create"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void missingInvalidOrUnreadableConfigFailsClosed()
    {
        when(configService.selectConfigByKey(WxFeatureGuardInterceptor.CONFIG_KEY))
                .thenReturn(null, "1", "false")
                .thenThrow(new IllegalStateException("cache unavailable"));

        assertFalse(interceptor.isFeatureEnabled());
        assertFalse(interceptor.isFeatureEnabled());
        assertFalse(interceptor.isFeatureEnabled());
        assertFalse(interceptor.isFeatureEnabled());

        reset(configService);
        when(configService.selectConfigByKey(WxFeatureGuardInterceptor.CONFIG_KEY)).thenReturn("true");
        assertTrue(interceptor.isFeatureEnabled());
    }

    @RequestMapping("/method")
    private static class MethodGuardController
    {
        @WxFeatureRequired
        @PostMapping("/guarded")
        @ResponseBody
        String guarded()
        {
            return "guarded";
        }

        @PostMapping("/unguarded")
        @ResponseBody
        String unguarded()
        {
            return "unguarded";
        }
    }

    @WxFeatureRequired(writeMethodsOnly = true, excludedPaths = "/class/unbind")
    @RequestMapping("/class")
    private static class ClassGuardController
    {
        @GetMapping("/query")
        @ResponseBody
        String query()
        {
            return "query";
        }

        @PostMapping("/create")
        @ResponseBody
        String create()
        {
            return "create";
        }

        @PostMapping("/unbind")
        @ResponseBody
        String unbind()
        {
            return "unbind";
        }
    }
}
