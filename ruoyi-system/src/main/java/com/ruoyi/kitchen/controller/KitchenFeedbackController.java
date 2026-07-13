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
import com.ruoyi.kitchen.domain.KitchenFeedback;
import com.ruoyi.kitchen.mapper.KitchenFeedbackMapper;

@RestController
@RequestMapping("/system/kitchen/feedback")
public class KitchenFeedbackController extends BaseController {
    @Autowired private KitchenFeedbackMapper mapper;
    @PreAuthorize("@ss.hasPermi('kitchen:feedback:list')")
    @GetMapping("/list") public TableDataInfo list(KitchenFeedback query) { startPage(); List<KitchenFeedback> rows=mapper.selectList(query); return getDataTable(rows); }
    @PreAuthorize("@ss.hasPermi('kitchen:feedback:handle')")
    @Log(title="反馈与建议处理",businessType=BusinessType.UPDATE)
    @PutMapping("/handle") public AjaxResult handle(@RequestBody KitchenFeedback feedback) { if(feedback.getId()==null)return AjaxResult.error("反馈不存在"); if(feedback.getHandleStatus()==null)feedback.setHandleStatus("2"); return toAjax(mapper.handle(feedback)); }
}
