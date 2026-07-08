package com.ruoyi.kitchen.controller.wx;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.kitchen.util.WxTokenService;

/**
 * 小程序通用上传Controller
 * 独立于后台 /common/upload，使用 @Anonymous 放行；URL 基于请求动态拼装。
 * 安全加固：需登录才可上传，且仅允许图片类型，防止匿名刷盘 / 上传 HTML 托管钓鱼页。
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/wx/common")
public class WxCommonController
{
    @Autowired
    private WxTokenService wxTokenService;

    /**
     * 小程序图片上传（单个，仅登录用户，仅图片）
     */
    @Anonymous
    @PostMapping("/upload")
    public AjaxResult upload(MultipartFile file, HttpServletRequest request)
    {
        // 需登录，避免匿名滥用
        wxTokenService.getRequiredUserId(request);
        try
        {
            String filePath = RuoYiConfig.getUploadPath();
            // 仅允许图片扩展名，杜绝上传 html/脚本等可被直出的文件
            String fileName = FileUploadUtils.upload(filePath, file, MimeTypeUtils.IMAGE_EXTENSION);
            String url = buildBaseUrl(request) + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 基于请求构造访问域名（替代 framework 的 ServerConfig，避免跨模块依赖）
     */
    private String buildBaseUrl(HttpServletRequest request)
    {
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        String contextPath = request.getContextPath();
        StringBuilder sb = new StringBuilder();
        sb.append(scheme).append("://").append(host);
        if (!("http".equals(scheme) && port == 80) && !("https".equals(scheme) && port == 443))
        {
            sb.append(":").append(port);
        }
        sb.append(contextPath);
        return sb.toString();
    }
}
