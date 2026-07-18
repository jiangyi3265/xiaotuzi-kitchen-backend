package com.ruoyi.common.utils.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HttpUtilsTest
{
    @Test
    void removesSensitiveQueryParametersFromLogs()
    {
        assertEquals("https://api.weixin.qq.com/sns/jscode2session?[redacted]",
                HttpUtils.sanitizeUrlForLog(
                        "https://api.weixin.qq.com/sns/jscode2session?appid=test&secret=top-secret&js_code=login-code"));
    }

    @Test
    void keepsUrlWithoutQueryUnchanged()
    {
        assertEquals("https://example.com/health", HttpUtils.sanitizeUrlForLog("https://example.com/health"));
        assertEquals("", HttpUtils.sanitizeUrlForLog(null));
    }
}
