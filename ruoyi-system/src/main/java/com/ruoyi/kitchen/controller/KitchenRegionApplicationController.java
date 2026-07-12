package com.ruoyi.kitchen.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.kitchen.domain.KitchenRegionApplication;
import com.ruoyi.kitchen.mapper.KitchenRegionApplicationMapper;

@RestController
@RequestMapping("/system/kitchen/regionApplication")
public class KitchenRegionApplicationController extends BaseController {
    @Autowired private KitchenRegionApplicationMapper mapper;
    @PreAuthorize("@ss.hasPermi('kitchen:regionApplication:list')")
    @GetMapping("/list") public TableDataInfo list(KitchenRegionApplication q) { startPage(); List<KitchenRegionApplication> rows=mapper.selectList(q); return getDataTable(rows); }
    @PreAuthorize("@ss.hasPermi('kitchen:regionApplication:audit')")
    @Log(title="区域代理审核", businessType=BusinessType.UPDATE)
    @PutMapping("/audit") public AjaxResult audit(@RequestBody KitchenRegionApplication a) { if(!"1".equals(a.getAuditStatus())&&!"2".equals(a.getAuditStatus())) return AjaxResult.error("审核状态无效"); return toAjax(mapper.audit(a)); }
    @PreAuthorize("@ss.hasPermi('kitchen:regionApplication:edit')")
    @PutMapping("/enabled") public AjaxResult enabled(@RequestBody KitchenRegionApplication a) { return toAjax(mapper.updateEnabled(a)); }
}
