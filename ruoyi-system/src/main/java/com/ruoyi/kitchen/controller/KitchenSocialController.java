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
 @PreAuthorize("@ss.hasPermi('kitchen:social:edit')")
 @Log(title="聚餐房间状态",businessType=BusinessType.UPDATE)
 @PutMapping("/group/status")
 public AjaxResult groupStatus(@RequestBody(required=false) Map<String,String> p){
  if(p==null||p.get("id")==null||p.get("id").trim().isEmpty())return AjaxResult.error("缺少聚餐房间ID");
  Long id;
  try{id=Long.valueOf(p.get("id").trim());}catch(NumberFormatException e){return AjaxResult.error("聚餐房间ID不正确");}
  if(id<=0)return AjaxResult.error("聚餐房间ID不正确");
  String status=p.get("status");
  if("1".equals(status))return AjaxResult.error("已结束或关闭的聚餐房间不允许重新激活");
  if(!"0".equals(status))return AjaxResult.error("聚餐房间状态参数无效，管理员仅允许关闭活动房间");
  return mapper.closeActiveGroup(id)>0?AjaxResult.success("聚餐房间已关闭"):AjaxResult.error("聚餐房间不存在或已经结束/关闭");
 }
 @PreAuthorize("@ss.hasPermi('kitchen:social:edit')")
 @Log(title="情侣空间状态",businessType=BusinessType.UPDATE)
 @PutMapping("/couple/status")
 public AjaxResult coupleStatus(@RequestBody(required=false) Map<String,String> p){
  if(p==null||p.get("id")==null||p.get("id").trim().isEmpty())return AjaxResult.error("缺少情侣空间ID");
  Long id;
  try{id=Long.valueOf(p.get("id").trim());}catch(NumberFormatException e){return AjaxResult.error("情侣空间ID不正确");}
  if(id<=0)return AjaxResult.error("情侣空间ID不正确");
  String status=p.get("status");
  if("1".equals(status))return AjaxResult.error("已解除的情侣空间不允许重新激活");
  if(!"0".equals(status))return AjaxResult.error("情侣空间状态参数无效，仅允许关闭");
  return mapper.closeActiveCouple(id)>0?AjaxResult.success("情侣空间已关闭"):AjaxResult.error("情侣空间不存在或已解除");
 }
}
