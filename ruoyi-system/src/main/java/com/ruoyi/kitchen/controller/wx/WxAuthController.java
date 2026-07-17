package com.ruoyi.kitchen.controller.wx;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.kitchen.domain.KitchenWxUser;
import com.ruoyi.kitchen.service.IKitchenWxUserService;
import com.ruoyi.kitchen.util.WxTokenService;

/**
 * 小程序登录鉴权Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/wx/auth")
public class WxAuthController
{
    private static final Logger log = LoggerFactory.getLogger(WxAuthController.class);

    @Value("${wx.appid:}")
    private String appid;

    @Value("${wx.secret:}")
    private String secret;

    /** 开发模式：仅本地联调用。生产必须为 false，否则可用 code 冒充任意用户 */
    @Value("${wx.devMode:true}")
    private boolean devMode;

    @Autowired
    private IKitchenWxUserService wxUserService;

    @Autowired
    private WxTokenService wxTokenService;

    /**
     * 小程序登录：用 wx.login 的 code 换取 openid，注册/登录后返回 token
     * 请求体: { code, nickname?, avatar? }
     */
    @Anonymous
    @PostMapping("/login")
    public AjaxResult login(@RequestBody JSONObject body)
    {
        String code = body.getString("code");
        if (StringUtils.isBlank(code))
        {
            return AjaxResult.error("缺少登录凭证 code");
        }

        String openid;
        String unionId = "";
        if (StringUtils.isNotBlank(appid) && StringUtils.isNotBlank(secret))
        {
            // 正式：调用微信 jscode2session
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid
                    + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
            String resp = HttpUtils.sendGet(url);
            JSONObject json = JSON.parseObject(resp);
            if (json == null || StringUtils.isBlank(json.getString("openid")))
            {
                // 不打印完整响应，避免泄露 session_key 等敏感信息
                log.error("微信登录失败: errcode={}, errmsg={}",
                        json != null ? json.getString("errcode") : "null",
                        json != null ? json.getString("errmsg") : "无响应");
                return AjaxResult.error("微信登录失败，请稍后重试");
            }
            openid = json.getString("openid");
            unionId = json.getString("unionid");
        }
        else if (devMode)
        {
            // 开发模式：未配置 appid/secret 时，将 code 视作 openid，便于本地联调
            log.warn("wx.devMode=true 且未配置 appid/secret，使用开发模式登录(code 即 openid)。生产环境严禁开启！");
            openid = "dev_" + code;
        }
        else
        {
            // 生产模式但未配置微信凭证：拒绝登录，绝不回退到可被伪造的开发模式
            log.error("wx.appid/wx.secret 未配置且 devMode=false，无法完成小程序登录");
            return AjaxResult.error("小程序登录暂不可用，请联系管理员");
        }

        KitchenWxUser user = wxUserService.registerOrGet(openid, unionId);
        if ("1".equals(user.getStatus()))
        {
            return AjaxResult.error("账号已被停用");
        }

        // 同步昵称/头像（如前端传入）
        String nickname = body.getString("nickname");
        String avatar = body.getString("avatar");
        if (StringUtils.isNotBlank(nickname) || StringUtils.isNotBlank(avatar))
        {
            KitchenWxUser update = new KitchenWxUser();
            update.setId(user.getId());
            update.setNickname(nickname);
            update.setAvatar(avatar);
            wxUserService.updateKitchenWxUser(update);
        }

        String token = wxTokenService.createToken(user.getId());
        AjaxResult ajax = AjaxResult.success();
        ajax.put("token", token);
        ajax.put("user", user);
        return ajax;
    }

    /**
     * 获取当前登录用户信息
     */
    @Anonymous
    @GetMapping("/info")
    public AjaxResult info(HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredUserId(request);
        return AjaxResult.success(wxUserService.selectKitchenWxUserById(userId));
    }

    /**
     * 修改当前登录用户资料。只允许更新本人可编辑字段，防止越权修改身份和状态。
     */
    @Anonymous
    @PutMapping("/profile")
    public AjaxResult updateProfile(@RequestBody JSONObject body, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredUserId(request);
        String nickname = body == null ? null : body.getString("nickname");
        if (StringUtils.isBlank(nickname))
        {
            return AjaxResult.error("昵称不能为空");
        }
        nickname = nickname.trim();
        if (nickname.length() > 30)
        {
            return AjaxResult.error("昵称最多30个字");
        }

        KitchenWxUser update = new KitchenWxUser();
        update.setId(userId);
        update.setNickname(nickname);
        update.setUpdateBy("wx:" + userId);
        if (wxUserService.updateKitchenWxUser(update) <= 0)
        {
            return AjaxResult.error("昵称更新失败");
        }
        return AjaxResult.success(wxUserService.selectKitchenWxUserById(userId));
    }

    /**
     * 退出登录
     */
    @Anonymous
    @PostMapping("/logout")
    public AjaxResult logout(HttpServletRequest request)
    {
        wxTokenService.removeToken(request);
        return AjaxResult.success();
    }
}
