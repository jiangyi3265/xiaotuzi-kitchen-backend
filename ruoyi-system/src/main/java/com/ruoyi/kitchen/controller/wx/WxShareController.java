package com.ruoyi.kitchen.controller.wx;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenSharePost;
import com.ruoyi.kitchen.mapper.KitchenPostLikeMapper;
import com.ruoyi.kitchen.service.IKitchenSharePostService;
import com.ruoyi.kitchen.util.WxPageUtils;
import com.ruoyi.kitchen.util.WxTokenService;

/**
 * 分享广场Controller（微信小程序端）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/wx/share")
public class WxShareController
{
    @Autowired
    private IKitchenSharePostService shareService;

    @Autowired
    private KitchenPostLikeMapper kitchenPostLikeMapper;

    @Autowired
    private WxTokenService wxTokenService;

    /** 发布是否需要审核：1 需要(默认待审核) 0 不需要(直接通过) */
    @Value("${wx.shareAudit:0}")
    private String shareAudit;

    /**
     * 小程序：分享广场列表（仅展示审核通过，分页）
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(KitchenSharePost query, HttpServletRequest request)
    {
        query.setAuditStatus("1");
        WxPageUtils.startPage();
        List<KitchenSharePost> list = shareService.selectKitchenSharePostList(query);
        Long userId = wxTokenService.getUserId(request);
        if (userId != null)
        {
            for (KitchenSharePost post : list)
            {
                post.setLiked(kitchenPostLikeMapper.countLike(post.getId(), userId) > 0);
            }
        }
        return WxPageUtils.getDataTable(list);
    }

    /**
     * 小程序：发布成品（需登录）
     */
    @Anonymous
    @PostMapping("/publish")
    public AjaxResult publish(@RequestBody KitchenSharePost post, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredUserId(request);
        if (StringUtils.isBlank(post.getContent()) && StringUtils.isBlank(post.getImages()))
        {
            return AjaxResult.error("请填写内容或上传图片");
        }
        post.setWxUserId(userId);
        post.setAuditStatus("1".equals(shareAudit) ? "0" : "1");
        shareService.insertKitchenSharePost(post);
        post.setLiked(false);
        return AjaxResult.success("1".equals(shareAudit) ? "发布成功，审核通过后展示" : "发布成功", post);
    }

    /**
     * 小程序：点赞/取消点赞（同一用户对同一帖幂等，返回操作后的点赞状态）
     */
    @Anonymous
    @PostMapping("/like/{id}")
    public AjaxResult like(@PathVariable("id") Long id, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredUserId(request);
        boolean liked = shareService.toggleLike(id, userId);
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("liked", liked);
        return AjaxResult.success(data);
    }
}
