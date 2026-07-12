package com.ruoyi.kitchen.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.kitchen.mapper.KitchenSocialMapper;

@RestController @RequestMapping("/system/kitchen/social")
public class KitchenSocialController extends BaseController {
 @Autowired private KitchenSocialMapper mapper;
 @PreAuthorize("@ss.hasPermi('kitchen:social:list')") @GetMapping("/group/list") public TableDataInfo groups(@RequestParam Map<String,Object> p){startPage();List<Map<String,Object>> rows=mapper.selectAdminGroupRooms(p);return getDataTable(rows);}
 @PreAuthorize("@ss.hasPermi('kitchen:social:list')") @GetMapping("/couple/list") public TableDataInfo couples(@RequestParam Map<String,Object> p){startPage();List<Map<String,Object>> rows=mapper.selectAdminCoupleSpaces(p);return getDataTable(rows);}
 @PreAuthorize("@ss.hasPermi('kitchen:social:edit')") @Log(title="聚餐房间状态",businessType=BusinessType.UPDATE) @PutMapping("/group/status") public AjaxResult groupStatus(@RequestBody Map<String,String> p){return toAjax(mapper.updateGroupStatus(Long.valueOf(p.get("id")),p.get("status")));}
 @PreAuthorize("@ss.hasPermi('kitchen:social:edit')") @Log(title="情侣空间状态",businessType=BusinessType.UPDATE) @PutMapping("/couple/status") public AjaxResult coupleStatus(@RequestBody Map<String,String> p){return toAjax(mapper.updateCoupleStatus(Long.valueOf(p.get("id")),p.get("status")));}
}
