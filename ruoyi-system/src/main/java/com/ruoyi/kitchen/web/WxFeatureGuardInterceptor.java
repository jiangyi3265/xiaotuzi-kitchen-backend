package com.ruoyi.kitchen.web;

import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.ISysConfigService;

/**
 * Server-side enforcement for {@code wx.feature.enabled}.
 *
 * The switch is fail-closed: a missing/invalid value, or a temporary config
 * read failure, must not allow a guarded write through.
 */
@Component
public class WxFeatureGuardInterceptor implements HandlerInterceptor
{
    static final String CONFIG_KEY = "wx.feature.enabled";
    static final String DISABLED_MESSAGE = "当前功能暂未开放，请稍后再试";

    private static final Logger log = LoggerFactory.getLogger(WxFeatureGuardInterceptor.class);

    private final ISysConfigService configService;

    @Autowired
    public WxFeatureGuardInterceptor(ISysConfigService configService)
    {
        this.configService = configService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if (!(handler instanceof HandlerMethod))
        {
            return true;
        }
        WxFeatureRequired requirement = findRequirement((HandlerMethod) handler);
        if (requirement == null || !appliesToRequest(requirement, request))
        {
            return true;
        }
        if (isFeatureEnabled())
        {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(JSON.toJSONString(AjaxResult.error(HttpStatus.FORBIDDEN, DISABLED_MESSAGE)));
        return false;
    }

    private WxFeatureRequired findRequirement(HandlerMethod handlerMethod)
    {
        WxFeatureRequired methodRequirement = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(),
                WxFeatureRequired.class);
        return methodRequirement != null ? methodRequirement
                : AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), WxFeatureRequired.class);
    }

    private boolean appliesToRequest(WxFeatureRequired requirement, HttpServletRequest request)
    {
        if (requirement.writeMethodsOnly() && isReadMethod(request.getMethod()))
        {
            return false;
        }
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath))
        {
            path = path.substring(contextPath.length());
        }
        for (String excludedPath : requirement.excludedPaths())
        {
            if (excludedPath.equals(path))
            {
                return false;
            }
        }
        return true;
    }

    private boolean isReadMethod(String method)
    {
        return "GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)
                || "OPTIONS".equalsIgnoreCase(method);
    }

    boolean isFeatureEnabled()
    {
        try
        {
            String value = configService.selectConfigByKey(CONFIG_KEY);
            return value != null && "true".equalsIgnoreCase(value.trim());
        }
        catch (RuntimeException e)
        {
            log.error("读取小程序功能总开关失败，已按关闭处理", e);
            return false;
        }
    }
}
