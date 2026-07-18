package com.ruoyi.kitchen.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.kitchen.util.WxMediaUrlUtils;

class WxMediaUrlResponseAdviceTest
{
    private static final String PUBLIC_BASE_URL = "https://cfht.hcaidachu.cn";

    @Test
    void buildPublicBaseUrlUsesForwardedHttpsAndOriginalHost()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("xiaotuzi-backend");
        request.setServerPort(8080);
        request.addHeader("X-Forwarded-Proto", "https,http");
        request.addHeader("Host", "cfht.hcaidachu.cn");

        assertEquals(PUBLIC_BASE_URL, WxMediaUrlUtils.buildPublicBaseUrl(request));
    }

    @Test
    void normalizeOnlyRewritesProfileMedia()
    {
        assertEquals(PUBLIC_BASE_URL + "/profile/upload/a.png",
                WxMediaUrlUtils.normalize("/profile/upload/a.png", PUBLIC_BASE_URL));
        assertEquals(PUBLIC_BASE_URL + "/profile/a.png,/static/b.png,https://img.example/c.png",
                WxMediaUrlUtils.normalize("/profile/a.png,/static/b.png,https://img.example/c.png", PUBLIC_BASE_URL));
        assertEquals("/static/a.png", WxMediaUrlUtils.normalize("/static/a.png", PUBLIC_BASE_URL));
        assertEquals("http://img.example/a.png",
                WxMediaUrlUtils.normalize("http://img.example/a.png", PUBLIC_BASE_URL));
        assertEquals("https://img.example/a.png",
                WxMediaUrlUtils.normalize("https://img.example/a.png", PUBLIC_BASE_URL));
        assertEquals("data:image/png;base64,AAAA",
                WxMediaUrlUtils.normalize("data:image/png;base64,AAAA", PUBLIC_BASE_URL));
        assertEquals("wxfile://tmp/a.png", WxMediaUrlUtils.normalize("wxfile://tmp/a.png", PUBLIC_BASE_URL));
    }

    @Test
    void wxJsonResponseRewritesAllKnownMediaFieldsRecursively()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        WxMediaUrlResponseAdvice advice = new WxMediaUrlResponseAdvice(objectMapper);
        Map<String, Object> media = new LinkedHashMap<>();
        List<String> fields = Arrays.asList("cover", "dishCover", "image", "avatar", "userAvatar", "banner",
                "officialAccountQr", "inviteCover", "wechatQr", "alipayQr", "stockGroupQr", "url");
        for (String field : fields)
        {
            media.put(field, "/profile/upload/" + field + ".png");
        }
        media.put("images", "/profile/upload/one.png,/static/two.png,https://img.example/three.png");

        List<Map<String, Object>> preserved = new ArrayList<>();
        preserved.add(singleton("cover", "/static/local.png"));
        preserved.add(singleton("cover", "http://img.example/plain.png"));
        preserved.add(singleton("cover", "https://img.example/secure.png"));
        preserved.add(singleton("cover", "data:image/png;base64,AAAA"));
        preserved.add(singleton("cover", "wxfile://tmp/local.png"));

        AjaxResult body = AjaxResult.success(media);
        body.put("rows", preserved);

        JsonNode result = (JsonNode) advice.beforeBodyWrite(body, null, MediaType.APPLICATION_JSON,
                MappingJackson2HttpMessageConverter.class, wxRequest("/api/wx/dish/list"), response());

        assertEquals(200, result.path("code").asInt());
        assertEquals("操作成功", result.path("msg").asText());
        assertTrue(result.path("data").isObject());
        assertTrue(result.path("rows").isArray());
        for (String field : fields)
        {
            assertEquals(PUBLIC_BASE_URL + "/profile/upload/" + field + ".png",
                    result.path("data").path(field).asText(), field);
        }
        assertEquals(PUBLIC_BASE_URL + "/profile/upload/one.png,/static/two.png,https://img.example/three.png",
                result.path("data").path("images").asText());
        assertEquals("/static/local.png", result.path("rows").path(0).path("cover").asText());
        assertEquals("http://img.example/plain.png", result.path("rows").path(1).path("cover").asText());
        assertEquals("https://img.example/secure.png", result.path("rows").path(2).path("cover").asText());
        assertEquals("data:image/png;base64,AAAA", result.path("rows").path(3).path("cover").asText());
        assertEquals("wxfile://tmp/local.png", result.path("rows").path(4).path("cover").asText());
        assertTrue(advice.supports(null, MappingJackson2HttpMessageConverter.class));
    }

    @Test
    void tableDataInfoKeepsPagingShapeWhileNormalizingRows()
    {
        WxMediaUrlResponseAdvice advice = new WxMediaUrlResponseAdvice(new ObjectMapper());
        TableDataInfo table = new TableDataInfo(Arrays.asList(singleton("cover", "/profile/upload/dish.png")), 62);
        table.setCode(200);
        table.setMsg("查询成功");

        JsonNode result = (JsonNode) advice.beforeBodyWrite(table, null, MediaType.APPLICATION_JSON,
                MappingJackson2HttpMessageConverter.class, wxRequest("/api/wx/dish/list"), response());

        assertEquals(200, result.path("code").asInt());
        assertEquals("查询成功", result.path("msg").asText());
        assertEquals(62, result.path("total").asLong());
        assertTrue(result.path("rows").isArray());
        assertEquals(PUBLIC_BASE_URL + "/profile/upload/dish.png",
                result.path("rows").path(0).path("cover").asText());
    }

    @Test
    void nonWxResponseIsNotChanged()
    {
        WxMediaUrlResponseAdvice advice = new WxMediaUrlResponseAdvice(new ObjectMapper());
        Map<String, Object> body = singleton("avatar", "/profile/upload/admin.png");

        Object result = advice.beforeBodyWrite(body, null, MediaType.APPLICATION_JSON,
                MappingJackson2HttpMessageConverter.class, wxRequest("/system/kitchen/shop"), response());

        assertSame(body, result);
    }

    private static Map<String, Object> singleton(String key, Object value)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(key, value);
        return result;
    }

    private static ServletServerHttpRequest wxRequest(String uri)
    {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        request.setRequestURI(uri);
        request.setScheme("http");
        request.setServerName("xiaotuzi-backend");
        request.setServerPort(8080);
        request.addHeader("X-Forwarded-Proto", "https");
        request.addHeader("Host", "cfht.hcaidachu.cn");
        return new ServletServerHttpRequest(request);
    }

    private static ServletServerHttpResponse response()
    {
        return new ServletServerHttpResponse(new MockHttpServletResponse());
    }
}
