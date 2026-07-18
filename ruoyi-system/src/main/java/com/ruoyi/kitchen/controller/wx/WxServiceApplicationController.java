package com.ruoyi.kitchen.controller.wx;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenServiceApplication;
import com.ruoyi.kitchen.mapper.KitchenServiceApplicationMapper;
import com.ruoyi.kitchen.mapper.KitchenWxUserMapper;
import com.ruoyi.kitchen.util.WxTokenService;
import com.ruoyi.kitchen.web.WxFeatureRequired;

@RestController
@RequestMapping("/api/wx/service-application")
public class WxServiceApplicationController {
    @Autowired private KitchenServiceApplicationMapper mapper;
    @Autowired private KitchenWxUserMapper wxUserMapper;
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
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public AjaxResult apply(@RequestBody KitchenServiceApplication application,HttpServletRequest request) {
        Long userId=tokenService.getRequiredUserId(request);
        if(!validType(application.getApplicationType())) return AjaxResult.error("申请类型无效");
        normalize(application);
        if(StringUtils.isBlank(application.getApplicantName())||StringUtils.isBlank(application.getPhone())||
           StringUtils.isBlank(application.getProvince())||StringUtils.isBlank(application.getCity())||StringUtils.isBlank(application.getDistrict())) {
            return AjaxResult.error("请完整填写姓名、手机号和服务区域");
        }
        if(!application.getPhone().matches("^1\\d{10}$")) return AjaxResult.error("请输入正确的11位手机号");
        if("0".equals(application.getApplicationType())&&StringUtils.isBlank(application.getVehicleType())) return AjaxResult.error("请填写配送交通工具");
        if("1".equals(application.getApplicationType())&&StringUtils.isBlank(application.getSkillTag())) return AjaxResult.error("请填写擅长菜系");
        if(tooLong(application.getApplicantName(),32)||tooLong(application.getPhone(),20)||
           tooLong(application.getProvince(),50)||tooLong(application.getCity(),50)||tooLong(application.getDistrict(),50)||
           tooLong(application.getAddress(),255)||tooLong(application.getExperience(),255)||
           tooLong(application.getSkillTag(),32)||tooLong(application.getVehicleType(),32)) {
            return AjaxResult.error("申请信息过长，请精简后再提交（姓名/标签32字，经验255字）");
        }
        if(wxUserMapper.lockActiveUser(userId)==null) return AjaxResult.error("用户不存在或已停用");
        KitchenServiceApplication latest=mapper.selectLatestByUserAndType(userId,application.getApplicationType());
        if(latest!=null&&("0".equals(latest.getAuditStatus())||"1".equals(latest.getAuditStatus()))) {
            return AjaxResult.error("该身份已申请，不能重复提交");
        }
        application.setWxUserId(userId);
        mapper.insert(application);
        return AjaxResult.success("申请已提交，请等待后台审核",application);
    }

    private boolean validType(String type) { return "0".equals(type)||"1".equals(type); }

    private void normalize(KitchenServiceApplication application) {
        application.setApplicantName(trim(application.getApplicantName()));
        application.setPhone(trim(application.getPhone()));
        application.setProvince(trim(application.getProvince()));
        application.setCity(trim(application.getCity()));
        application.setDistrict(trim(application.getDistrict()));
        application.setAddress(trim(application.getAddress()));
        application.setExperience(trim(application.getExperience()));
        application.setSkillTag(trim(application.getSkillTag()));
        application.setVehicleType(trim(application.getVehicleType()));
    }

    private String trim(String value) { return value==null?null:value.trim(); }
    private boolean tooLong(String value,int max) { return value!=null&&value.length()>max; }
}
