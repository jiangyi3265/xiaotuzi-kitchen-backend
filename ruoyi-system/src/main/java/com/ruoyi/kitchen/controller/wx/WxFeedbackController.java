package com.ruoyi.kitchen.controller.wx;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenFeedback;
import com.ruoyi.kitchen.mapper.KitchenFeedbackMapper;
import com.ruoyi.kitchen.util.WxTokenService;

@RestController
@RequestMapping("/api/wx/feedback")
public class WxFeedbackController {
    @Autowired private KitchenFeedbackMapper mapper;
    @Autowired private WxTokenService tokenService;

    @Anonymous @GetMapping("/my")
    public AjaxResult my(HttpServletRequest request) { return AjaxResult.success(mapper.selectMy(tokenService.getRequiredUserId(request))); }

    @Anonymous @PostMapping
    public AjaxResult submit(@RequestBody KitchenFeedback feedback,HttpServletRequest request) {
        if(StringUtils.isBlank(feedback.getContent())||feedback.getContent().trim().length()<5) return AjaxResult.error("请至少填写5个字的反馈内容");
        feedback.setContent(feedback.getContent().trim());
        if(feedback.getContent().length()>1000) return AjaxResult.error("反馈内容不能超过1000字");
        if(StringUtils.isNotBlank(feedback.getContact())&&feedback.getContact().length()>100) return AjaxResult.error("联系方式不能超过100字");
        if(StringUtils.isNotBlank(feedback.getImages())&&feedback.getImages().length()>1200) return AjaxResult.error("反馈图片过多");
        if(!"0".equals(feedback.getFeedbackType())&&!"1".equals(feedback.getFeedbackType())&&!"2".equals(feedback.getFeedbackType())&&!"3".equals(feedback.getFeedbackType())) feedback.setFeedbackType("0");
        feedback.setWxUserId(tokenService.getRequiredUserId(request));
        mapper.insert(feedback);
        return AjaxResult.success("感谢你的反馈",feedback);
    }
}
