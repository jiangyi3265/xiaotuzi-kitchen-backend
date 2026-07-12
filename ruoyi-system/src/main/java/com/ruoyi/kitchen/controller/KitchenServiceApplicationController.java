package com.ruoyi.kitchen.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenServiceApplication;
import com.ruoyi.kitchen.mapper.KitchenServiceApplicationMapper;

@RestController
@RequestMapping("/system/kitchen/serviceApplication")
public class KitchenServiceApplicationController extends BaseController {
    @Autowired private KitchenServiceApplicationMapper mapper;

    @PreAuthorize("@ss.hasPermi('kitchen:serviceApplication:list')")
    @GetMapping("/list") public TableDataInfo list(KitchenServiceApplication query) { startPage(); List<KitchenServiceApplication> rows=mapper.selectList(query); return getDataTable(rows); }

    @PreAuthorize("@ss.hasPermi('kitchen:serviceApplication:audit')")
    @Log(title="服务人员申请审核",businessType=BusinessType.UPDATE)
    @PutMapping("/audit")
    @Transactional
    public AjaxResult audit(@RequestBody KitchenServiceApplication request) {
        if(!"1".equals(request.getAuditStatus())&&!"2".equals(request.getAuditStatus())) return AjaxResult.error("审核状态无效");
        KitchenServiceApplication application=mapper.selectById(request.getId());
        if(application==null) return AjaxResult.error("申请不存在");
        if(!"0".equals(application.getAuditStatus())) return AjaxResult.error("该申请已审核");
        Long providerId=null;
        if("1".equals(request.getAuditStatus())) {
            Map<String,Object> provider=new HashMap<>();
            provider.put("name",application.getApplicantName());
            provider.put("tag","0".equals(application.getApplicationType())?StringUtils.defaultIfEmpty(application.getVehicleType(),"同城配送"):StringUtils.defaultIfEmpty(application.getSkillTag(),"家常菜"));
            provider.put("intro",StringUtils.defaultIfEmpty(application.getExperience(),application.getProvince()+application.getCity()+application.getDistrict()+"服务人员"));
            if("0".equals(application.getApplicationType())) mapper.insertRiderProvider(provider); else mapper.insertChefProvider(provider);
            providerId=((Number)provider.get("id")).longValue();
        }
        mapper.audit(application.getId(),request.getAuditStatus(),request.getRemark(),providerId);
        return AjaxResult.success("审核完成");
    }
}
