package com.ruoyi.kitchen.controller.wx;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenComment;
import com.ruoyi.kitchen.domain.KitchenSharePost;
import com.ruoyi.kitchen.service.IKitchenCommentService;
import com.ruoyi.kitchen.service.IKitchenSharePostService;
import com.ruoyi.kitchen.util.WxContentFilter;
import com.ruoyi.kitchen.util.WxPageUtils;
import com.ruoyi.kitchen.util.WxTokenService;
import com.ruoyi.kitchen.web.WxFeatureRequired;

/**
 * 成品评论Controller（微信小程序端）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/wx/comment")
public class WxCommentController
{
    @Autowired
    private IKitchenCommentService commentService;

    @Autowired
    private IKitchenSharePostService sharePostService;

    @Autowired
    private WxTokenService wxTokenService;

    /** 评论是否需要审核：1 需要(默认待审核) 0 不需要(直接通过) */
    @Value("${wx.shareAudit:1}")
    private String shareAudit;

    /**
     * 小程序：评论列表（仅展示审核通过，按 postId 查询，分页）
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(KitchenComment query)
    {
        query.setAuditStatus("1");
        WxPageUtils.startPage();
        List<KitchenComment> list = commentService.selectKitchenCommentList(query);
        return WxPageUtils.getDataTable(list);
    }

    /**
     * 小程序：发表评论（需登录）
     */
    @Anonymous
    @PostMapping("/add")
    @WxFeatureRequired
    public AjaxResult add(@RequestBody KitchenComment comment, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredUserId(request);
        if (comment.getPostId() == null)
        {
            return AjaxResult.error("缺少动态ID");
        }
        String content = comment.getContent() == null ? null : comment.getContent().trim();
        if (StringUtils.isBlank(content))
        {
            return AjaxResult.error("请填写评论内容");
        }
        if (content.length() > 500)
        {
            return AjaxResult.error("评论内容不能超过500字");
        }
        KitchenSharePost post = sharePostService.selectKitchenSharePostById(comment.getPostId());
        if (post == null || !"1".equals(post.getAuditStatus()))
        {
            return AjaxResult.error("动态不存在或尚未公开");
        }
        if (WxContentFilter.containsBlockedContent(content))
        {
            return AjaxResult.error("评论包含不适宜信息，请修改后再发送");
        }
        comment.setContent(content);
        comment.setWxUserId(userId);
        comment.setAuditStatus("1".equals(shareAudit) ? "0" : "1");
        commentService.addComment(comment);
        AjaxResult ajax = AjaxResult.success("1".equals(shareAudit) ? "评论成功，审核通过后展示" : "评论成功");
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("status", comment.getAuditStatus());
        data.put("commentId", comment.getId());
        data.put("comment", comment);
        ajax.put("data", data);
        return ajax;
    }
}
