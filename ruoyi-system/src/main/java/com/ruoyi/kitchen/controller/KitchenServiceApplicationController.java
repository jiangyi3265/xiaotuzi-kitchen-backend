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
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult audit(@RequestBody KitchenServiceApplication request) {
        if(request.getId()==null) return AjaxResult.error("申请ID不能为空");
        if(!"1".equals(request.getAuditStatus())&&!"2".equals(request.getAuditStatus())) return AjaxResult.error("审核状态无效");
        if(request.getRemark()!=null&&request.getRemark().length()>500) return AjaxResult.error("审核备注最多500个字");
        KitchenServiceApplication application=mapper.selectByIdForUpdate(request.getId());
        if(application==null) return AjaxResult.error("申请不存在");
        if(!"0".equals(application.getAuditStatus())) return AjaxResult.error("该申请已审核");
        if(!"0".equals(application.getApplicationType())&&!"1".equals(application.getApplicationType())) return AjaxResult.error("申请类型无效");
        Long providerId=null;
        if("1".equals(request.getAuditStatus())) {
            Map<String,Object> provider=new HashMap<>();
            provider.put("name",limit(application.getApplicantName(),32));
            provider.put("tag",limit("0".equals(application.getApplicationType())?StringUtils.defaultIfEmpty(application.getVehicleType(),"同城配送"):StringUtils.defaultIfEmpty(application.getSkillTag(),"家常菜"),32));
            provider.put("intro",limit(StringUtils.defaultIfEmpty(application.getExperience(),application.getProvince()+application.getCity()+application.getDistrict()+"服务人员"),255));
            int inserted="0".equals(application.getApplicationType())?mapper.insertRiderProvider(provider):mapper.insertChefProvider(provider);
            if(inserted!=1||provider.get("id")==null) throw new IllegalStateException("服务人员资料创建失败");
            providerId=((Number)provider.get("id")).longValue();
        }
        if(mapper.audit(application.getId(),request.getAuditStatus(),request.getRemark(),providerId)!=1) {
            throw new IllegalStateException("申请审核状态更新失败");
        }
        return AjaxResult.success("审核完成");
    }

    private String limit(String value,int max) {
        String normalized=StringUtils.defaultString(value).trim();
        if(normalized.length()<=max)return normalized;
        int end=normalized.offsetByCodePoints(0,Math.min(max,normalized.codePointCount(0,normalized.length())));
        return normalized.substring(0,end);
    }
}
