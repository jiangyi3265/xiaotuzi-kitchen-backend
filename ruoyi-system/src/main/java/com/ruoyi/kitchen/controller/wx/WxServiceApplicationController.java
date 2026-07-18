package com.ruoyi.kitchen.controller.wx;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenServiceApplication;
import com.ruoyi.kitchen.mapper.KitchenServiceApplicationMapper;
import com.ruoyi.kitchen.util.WxTokenService;
import com.ruoyi.kitchen.web.WxFeatureRequired;

@RestController
@RequestMapping("/api/wx/service-application")
public class WxServiceApplicationController {
    @Autowired private KitchenServiceApplicationMapper mapper;
    @Autowired private WxTokenService tokenService;

    @Anonymous
    @GetMapping("/my")
    public AjaxResult my(@RequestParam String applicationType, HttpServletRequest request) {
        Long userId=tokenService.getRequiredUserId(request);
        if(!validType(applicationType)) return AjaxResult.error("申请类型无效");
        return AjaxResult.success(mapper.selectLatestByUserAndType(userId,applicationType));
    }

    @Anonymous
    @PostMapping("/apply")
    @WxFeatureRequired
    public AjaxResult apply(@RequestBody KitchenServiceApplication application,HttpServletRequest request) {
        Long userId=tokenService.getRequiredUserId(request);
        if(!validType(application.getApplicationType())) return AjaxResult.error("申请类型无效");
        if(StringUtils.isBlank(application.getApplicantName())||StringUtils.isBlank(application.getPhone())||
           StringUtils.isBlank(application.getProvince())||StringUtils.isBlank(application.getCity())||StringUtils.isBlank(application.getDistrict())) {
            return AjaxResult.error("请完整填写姓名、手机号和服务区域");
        }
        KitchenServiceApplication latest=mapper.selectLatestByUserAndType(userId,application.getApplicationType());
        if(latest!=null&&("0".equals(latest.getAuditStatus())||"1".equals(latest.getAuditStatus()))) {
            return AjaxResult.error("该身份已申请，不能重复提交");
        }
        application.setWxUserId(userId);
        mapper.insert(application);
        return AjaxResult.success("申请已提交，请等待后台审核",application);
    }

    private boolean validType(String type) { return "0".equals(type)||"1".equals(type); }
}
