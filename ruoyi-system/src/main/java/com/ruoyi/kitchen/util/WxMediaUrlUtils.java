package com.ruoyi.kitchen.util;

import java.util.Locale;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

/**
 * 微信端媒体地址工具。
 *
 * 数据库继续保存可迁移的 /profile/... 相对路径，仅在小程序响应中转换为
 * 当前公网域名下的绝对地址。/static/... 与客户端临时文件、data URI、第三方
 * 绝对地址均保持原值。
 */
public final class WxMediaUrlUtils
{
    private static final Pattern VALID_HOST = Pattern.compile("^[A-Za-z0-9.-]+(?::[0-9]{1,5})?$");

    private WxMediaUrlUtils()
    {
    }

    /**
     * 根据反向代理协议头与原始 Host 构造公网根地址。
     */
    public static String buildPublicBaseUrl(HttpServletRequest request)
    {
        String scheme = firstHeaderValue(request.getHeader("X-Forwarded-Proto"));
        if (!isHttpScheme(scheme))
        {
            scheme = request.getScheme();
        }
        if (!isHttpScheme(scheme))
        {
            scheme = "https";
        }
        scheme = scheme.toLowerCase(Locale.ROOT);

        String authority = firstHeaderValue(request.getHeader("Host"));
        if (!isValidHost(authority))
        {
            authority = request.getServerName();
            int port = request.getServerPort();
            if (port > 0 && !isDefaultPort(scheme, port))
            {
                authority += ":" + port;
            }
        }
        return scheme + "://" + authority;
    }

    /**
     * 规范单个媒体字段。images 等逗号分隔字段也可直接传入。
     */
    public static String normalize(String value, String publicBaseUrl)
    {
        if (value == null || value.length() == 0)
        {
            return value;
        }

        String trimmed = value.trim();
        // data URI 本身包含逗号，不参与多图拆分。
        if (startsWithIgnoreCase(trimmed, "data:") || startsWithIgnoreCase(trimmed, "wxfile:"))
        {
            return value;
        }
        if (trimmed.indexOf(',') >= 0)
        {
            String[] items = trimmed.split(",", -1);
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < items.length; i++)
            {
                if (i > 0)
                {
                    result.append(',');
                }
                result.append(normalizeSingle(items[i].trim(), publicBaseUrl));
            }
            return result.toString();
        }
        return normalizeSingle(trimmed, publicBaseUrl);
    }

    private static String normalizeSingle(String value, String publicBaseUrl)
    {
        if (value == null || value.length() == 0)
        {
            return value;
        }
        if (startsWithIgnoreCase(value, "http://") || startsWithIgnoreCase(value, "https://")
                || startsWithIgnoreCase(value, "data:") || startsWithIgnoreCase(value, "wxfile:")
                || value.startsWith("/static/") || "/static".equals(value))
        {
            return value;
        }
        if (value.startsWith("/profile/") || "/profile".equals(value))
        {
            return stripTrailingSlash(publicBaseUrl) + value;
        }
        return value;
    }

    private static boolean startsWithIgnoreCase(String value, String prefix)
    {
        return value.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    private static String firstHeaderValue(String value)
    {
        if (value == null)
        {
            return null;
        }
        int comma = value.indexOf(',');
        return (comma >= 0 ? value.substring(0, comma) : value).trim();
    }

    private static boolean isHttpScheme(String value)
    {
        return "http".equalsIgnoreCase(value) || "https".equalsIgnoreCase(value);
    }

    private static boolean isValidHost(String value)
    {
        return value != null && VALID_HOST.matcher(value).matches();
    }

    private static boolean isDefaultPort(String scheme, int port)
    {
        return ("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443);
    }

    private static String stripTrailingSlash(String value)
    {
        if (value == null || value.length() == 0)
        {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
