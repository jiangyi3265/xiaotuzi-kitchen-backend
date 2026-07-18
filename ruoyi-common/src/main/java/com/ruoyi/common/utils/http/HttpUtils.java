package com.ruoyi.common.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.http.MediaType;

/**
 * 通用http发送方法
 * 
 * @author ruoyi
 */
public class HttpUtils
{
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    /** 外部接口连接与读取超时，避免第三方网络异常耗尽业务线程。 */
    private static final int CONNECT_TIMEOUT_MS = 5000;

    private static final int READ_TIMEOUT_MS = 10000;

    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url 发送请求的 URL
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url)
    {
        return sendGet(url, StringUtils.EMPTY);
    }

    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param)
    {
        return sendGet(url, param, Constants.UTF8);
    }

    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param contentType 编码类型
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param, String contentType)
    {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try
        {
            String urlNameString = StringUtils.isNotBlank(param) ? url + "?" + param : url;
            log.debug("sendGet - {}", sanitizeUrlForLog(urlNameString));
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), contentType));
            String line;
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
            log.debug("sendGet completed - {}, responseLength={}", sanitizeUrlForLog(urlNameString), result.length());
        }
        catch (ConnectException e)
        {
            logFailure("HttpUtils.sendGet", url, e);
        }
        catch (SocketTimeoutException e)
        {
            logFailure("HttpUtils.sendGet", url, e);
        }
        catch (IOException e)
        {
            logFailure("HttpUtils.sendGet", url, e);
        }
        catch (Exception e)
        {
            logFailure("HttpUtils.sendGet", url, e);
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (Exception ex)
            {
                logFailure("HttpUtils.sendGet.close", url, ex);
            }
        }
        return result.toString();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param)
    {
        return sendPost(url, param, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url 发送请求的 URL
     * @param param 请求参数
     * @param contentType 内容类型
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param, String contentType)
    {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try
        {
            log.debug("sendPost - {}", sanitizeUrlForLog(url));
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-Type", contentType);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
            log.debug("sendPost completed - {}, responseLength={}", sanitizeUrlForLog(url), result.length());
        }
        catch (ConnectException e)
        {
            logFailure("HttpUtils.sendPost", url, e);
        }
        catch (SocketTimeoutException e)
        {
            logFailure("HttpUtils.sendPost", url, e);
        }
        catch (IOException e)
        {
            logFailure("HttpUtils.sendPost", url, e);
        }
        catch (Exception e)
        {
            logFailure("HttpUtils.sendPost", url, e);
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ex)
            {
                logFailure("HttpUtils.sendPost.close", url, ex);
            }
        }
        return result.toString();
    }

    public static String sendSSLPost(String url, String param)
    {
        return sendSSLPost(url, param, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    public static String sendSSLPost(String url, String param, String contentType)
    {
        StringBuilder result = new StringBuilder();
        String urlNameString = url + "?" + param;
        try
        {
            log.debug("sendSSLPost - {}", sanitizeUrlForLog(urlNameString));
            URL console = new URL(urlNameString);
            HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-Type", contentType);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String ret = "";
            while ((ret = br.readLine()) != null)
            {
                if (ret != null && !"".equals(ret.trim()))
                {
                    result.append(new String(ret.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                }
            }
            log.debug("sendSSLPost completed - {}, responseLength={}", sanitizeUrlForLog(urlNameString), result.length());
            conn.disconnect();
            br.close();
        }
        catch (ConnectException e)
        {
            logFailure("HttpUtils.sendSSLPost", url, e);
        }
        catch (SocketTimeoutException e)
        {
            logFailure("HttpUtils.sendSSLPost", url, e);
        }
        catch (IOException e)
        {
            logFailure("HttpUtils.sendSSLPost", url, e);
        }
        catch (Exception e)
        {
            logFailure("HttpUtils.sendSSLPost", url, e);
        }
        return result.toString();
    }

    /**
     * 日志中只保留目标地址，不记录 query，避免 AppSecret、登录 code、密码等敏感参数落盘。
     */
    static String sanitizeUrlForLog(String url)
    {
        if (url == null)
        {
            return "";
        }
        int queryIndex = url.indexOf('?');
        return queryIndex < 0 ? url : url.substring(0, queryIndex) + "?[redacted]";
    }

    /** 异常消息本身也可能携带完整 URL，因此只记录类型，不把异常对象写入日志。 */
    private static void logFailure(String operation, String url, Exception exception)
    {
        log.error("{} failed, url={}, errorType={}", operation, sanitizeUrlForLog(url),
                exception.getClass().getSimpleName());
    }
}
