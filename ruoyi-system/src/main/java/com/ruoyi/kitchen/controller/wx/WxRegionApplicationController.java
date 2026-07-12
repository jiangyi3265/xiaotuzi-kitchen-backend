package com.ruoyi.kitchen.controller.wx;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenRegionApplication;
import com.ruoyi.kitchen.mapper.KitchenRegionApplicationMapper;
import com.ruoyi.kitchen.util.WxTokenService;

@RestController
@RequestMapping("/api/wx/region")
public class WxRegionApplicationController {
    @Autowired private KitchenRegionApplicationMapper mapper;
    @Autowired private WxTokenService tokenService;
    @Anonymous @GetMapping("/status") public AjaxResult status(KitchenRegionApplication q, HttpServletRequest request) {
        boolean opened=StringUtils.isNotBlank(q.getProvince())&&StringUtils.isNotBlank(q.getCity())&&StringUtils.isNotBlank(q.getDistrict())&&mapper.countEnabledRegion(q)>0;
        Long uid=tokenService.getUserId(request); KitchenRegionApplication latest=uid==null?null:mapper.selectLatestByUser(uid);
        Map<String,Object> data=new HashMap<>(); data.put("opened",opened); data.put("application",latest); return AjaxResult.success(data);
    }
    @Anonymous @PostMapping("/apply") public AjaxResult apply(@RequestBody KitchenRegionApplication a, HttpServletRequest request) {
        Long uid=tokenService.getRequiredUserId(request);
        if(StringUtils.isBlank(a.getApplicantName())||StringUtils.isBlank(a.getPhone())||StringUtils.isBlank(a.getProvince())||StringUtils.isBlank(a.getCity())||StringUtils.isBlank(a.getDistrict())) return AjaxResult.error("请完整填写申请信息");
        KitchenRegionApplication old=mapper.selectLatestByUser(uid); if(old!=null&&"0".equals(old.getAuditStatus())) return AjaxResult.error("申请正在审核中，请勿重复提交");
        a.setWxUserId(uid); mapper.insert(a); return AjaxResult.success("申请已提交，请等待后台审核",a);
    }
}
