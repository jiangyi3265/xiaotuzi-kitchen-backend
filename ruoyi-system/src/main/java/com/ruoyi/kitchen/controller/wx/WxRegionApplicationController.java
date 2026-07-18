package com.ruoyi.kitchen.controller.wx;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenRegionApplication;
import com.ruoyi.kitchen.mapper.KitchenRegionApplicationMapper;
import com.ruoyi.kitchen.mapper.KitchenWxUserMapper;
import com.ruoyi.kitchen.util.WxTokenService;
import com.ruoyi.kitchen.web.WxFeatureRequired;

@RestController
@RequestMapping("/api/wx/region")
public class WxRegionApplicationController {
    @Autowired private KitchenRegionApplicationMapper mapper;
    @Autowired private KitchenWxUserMapper wxUserMapper;
    @Autowired private WxTokenService tokenService;
    @Anonymous @GetMapping("/opened") public AjaxResult opened() { return AjaxResult.success(mapper.selectEnabledRegions()); }
    @Anonymous @GetMapping("/status") public AjaxResult status(KitchenRegionApplication q, HttpServletRequest request) {
        boolean opened=StringUtils.isNotBlank(q.getProvince())&&StringUtils.isNotBlank(q.getCity())&&StringUtils.isNotBlank(q.getDistrict())&&mapper.countEnabledRegion(q)>0;
        Long uid=tokenService.getUserId(request); KitchenRegionApplication latest=uid==null?null:mapper.selectLatestByUser(uid);
        Map<String,Object> data=new HashMap<>(); data.put("opened",opened); data.put("application",latest); return AjaxResult.success(data);
    }
    @Anonymous @PostMapping("/apply") @WxFeatureRequired @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class) public AjaxResult apply(@RequestBody KitchenRegionApplication a, HttpServletRequest request) {
        Long uid=tokenService.getRequiredUserId(request);
        normalize(a);
        if(StringUtils.isBlank(a.getApplicantName())||StringUtils.isBlank(a.getPhone())||StringUtils.isBlank(a.getProvince())||StringUtils.isBlank(a.getCity())||StringUtils.isBlank(a.getDistrict())) return AjaxResult.error("请完整填写申请信息");
        if(!a.getPhone().matches("^1\\d{10}$")) return AjaxResult.error("请输入正确的11位手机号");
        if(tooLong(a.getApplicantName(),50)||tooLong(a.getPhone(),20)||tooLong(a.getProvince(),50)||tooLong(a.getCity(),50)||tooLong(a.getDistrict(),50)||tooLong(a.getAddress(),255)||tooLong(a.getExperience(),500)) return AjaxResult.error("申请信息过长，请精简后再提交");
        if(wxUserMapper.lockActiveUser(uid)==null) return AjaxResult.error("用户不存在或已停用");
        KitchenRegionApplication old=mapper.selectLatestByUser(uid); if(old!=null&&"0".equals(old.getAuditStatus())) return AjaxResult.error("申请正在审核中，请勿重复提交");
        a.setWxUserId(uid); mapper.insert(a); return AjaxResult.success("申请已提交，请等待后台审核",a);
    }

    private void normalize(KitchenRegionApplication a) {
        a.setApplicantName(trim(a.getApplicantName())); a.setPhone(trim(a.getPhone()));
        a.setProvince(trim(a.getProvince())); a.setCity(trim(a.getCity())); a.setDistrict(trim(a.getDistrict()));
        a.setAddress(trim(a.getAddress())); a.setExperience(trim(a.getExperience()));
    }
    private String trim(String value) { return value==null?null:value.trim(); }
    private boolean tooLong(String value,int max) { return value!=null&&value.length()>max; }
}
