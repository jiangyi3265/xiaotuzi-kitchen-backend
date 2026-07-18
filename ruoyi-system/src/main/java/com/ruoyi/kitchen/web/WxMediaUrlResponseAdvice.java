package com.ruoyi.kitchen.web;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.kitchen.util.WxMediaUrlUtils;

/**
 * 仅对微信小程序 JSON 响应中的媒体字段补全公网地址。
 *
 * 采用响应层统一处理，既覆盖实体对象，也覆盖分页结果与 MyBatis Map；后台管理
 * 接口不在本 Advice 的 controller 包与 URL 范围内，不会改变后台读写的相对路径。
 */
@RestControllerAdvice(basePackages = "com.ruoyi.kitchen.controller.wx")
public class WxMediaUrlResponseAdvice implements ResponseBodyAdvice<Object>
{
    private static final Set<String> MEDIA_FIELDS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "avatar", "useravatar", "avatarurl", "banner", "cover", "dishcover", "invitecover",
            "image", "images", "imageurl", "officialaccountqr", "wechatqr", "alipayqr",
            "stockgroupqr", "qrcode", "url")));

    private final ObjectMapper objectMapper;

    public WxMediaUrlResponseAdvice(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType)
    {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response)
    {
        if (body == null || !(request instanceof ServletServerHttpRequest))
        {
            return body;
        }
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        if (!isWxRequest(servletRequest))
        {
            return body;
        }

        String publicBaseUrl = WxMediaUrlUtils.buildPublicBaseUrl(servletRequest);
        JsonNode tree = objectMapper.valueToTree(body);
        normalizeNode(tree, publicBaseUrl);
        return tree;
    }

    private boolean isWxRequest(HttpServletRequest request)
    {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && contextPath.length() > 0 && uri.startsWith(contextPath))
        {
            uri = uri.substring(contextPath.length());
        }
        return "/api/wx".equals(uri) || uri.startsWith("/api/wx/");
    }

    private void normalizeNode(JsonNode node, String publicBaseUrl)
    {
        if (node == null)
        {
            return;
        }
        if (node.isObject())
        {
            ObjectNode object = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = object.fields();
            while (fields.hasNext())
            {
                Map.Entry<String, JsonNode> field = fields.next();
                JsonNode value = field.getValue();
                if (value != null && value.isTextual() && isMediaField(field.getKey()))
                {
                    object.put(field.getKey(), WxMediaUrlUtils.normalize(value.textValue(), publicBaseUrl));
                }
                else
                {
                    normalizeNode(value, publicBaseUrl);
                }
            }
        }
        else if (node.isArray())
        {
            for (JsonNode item : node)
            {
                normalizeNode(item, publicBaseUrl);
            }
        }
    }

    private boolean isMediaField(String fieldName)
    {
        return fieldName != null && MEDIA_FIELDS.contains(fieldName.toLowerCase(java.util.Locale.ROOT));
    }
}
