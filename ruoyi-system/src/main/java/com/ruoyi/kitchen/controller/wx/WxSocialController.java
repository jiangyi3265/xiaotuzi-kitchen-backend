package com.ruoyi.kitchen.controller.wx;

import java.time.LocalDate;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.mapper.KitchenSocialMapper;
import com.ruoyi.kitchen.util.WxTokenService;

@RestController @RequestMapping("/api/wx/social")
public class WxSocialController {
 @Autowired private KitchenSocialMapper mapper; @Autowired private WxTokenService token;
 private String code(){return UUID.randomUUID().toString().replace("-","").substring(0,6).toUpperCase();}
 @Anonymous @PostMapping("/group/create") @Transactional public AjaxResult createGroup(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);p.put("ownerUserId",uid);p.put("roomCode",code());if(StringUtils.isBlank((String)p.get("title")))p.put("title","今天一起吃饭");mapper.insertGroupRoom(p);Map<String,Object> m=new HashMap<>();m.put("roomId",p.get("id"));m.put("userId",uid);mapper.insertGroupMember(m);return AjaxResult.success(p);}
 @Anonymous @PostMapping("/group/join") @Transactional public AjaxResult joinGroup(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> room=mapper.selectGroupRoomByCode(String.valueOf(p.get("roomCode")).toUpperCase());if(room==null)return AjaxResult.error("房间不存在或已结束");p.put("roomId",room.get("id"));p.put("userId",uid);mapper.insertGroupMember(p);return AjaxResult.success(room);}
 @Anonymous @GetMapping("/group/{id}") public AjaxResult group(@PathVariable Long id,HttpServletRequest r){Long uid=token.getRequiredUserId(r);if(mapper.countGroupMember(id,uid)==0)return AjaxResult.error("请先加入房间");Map<String,Object> room=mapper.selectGroupRoomById(id);if(room==null)return AjaxResult.error("房间不存在");List<Map<String,Object>> members=mapper.selectGroupMembers(id),items=mapper.selectGroupItems(id);double total=items.stream().mapToDouble(x->x.get("subtotal")==null?0:Double.parseDouble(String.valueOf(x.get("subtotal")))).sum();room.put("members",members);room.put("items",items);room.put("total",Math.round(total*100)/100.0);room.put("aa",members.isEmpty()?0:Math.round(total/members.size()*100)/100.0);return AjaxResult.success(room);}
 @Anonymous @PostMapping("/group/item") public AjaxResult addItem(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);Long roomId=Long.valueOf(String.valueOf(p.get("roomId")));int quantity=p.get("quantity")==null?1:Integer.parseInt(String.valueOf(p.get("quantity")));if(quantity<1||quantity>99)return AjaxResult.error("菜品数量必须在1到99之间");if(mapper.selectGroupRoomById(roomId)==null)return AjaxResult.error("房间不存在或已结束");if(mapper.countGroupMember(roomId,uid)==0)return AjaxResult.error("你不在该房间");p.put("userId",uid);p.put("quantity",quantity);mapper.upsertGroupItem(p);return AjaxResult.success();}
 @Anonymous @PostMapping("/couple/create") public AjaxResult createCouple(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);if(mapper.selectCoupleByUser(uid)!=null)return AjaxResult.error("你已经绑定了情侣空间");p.put("userId",uid);p.put("inviteCode",code());p.putIfAbsent("startDate",LocalDate.now().toString());mapper.insertCouple(p);return AjaxResult.success(p);}
 @Anonymous @PostMapping("/couple/join") public AjaxResult joinCouple(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);if(mapper.selectCoupleByUser(uid)!=null)return AjaxResult.error("你已经绑定了情侣空间");Map<String,Object> c=mapper.selectCoupleByCode(String.valueOf(p.get("inviteCode")).toUpperCase());if(c==null)return AjaxResult.error("邀请码无效");if(mapper.bindCouplePartner(Long.valueOf(String.valueOf(c.get("id"))),uid)==0)return AjaxResult.error("该空间已绑定或不能绑定自己");return AjaxResult.success();}
 @Anonymous @GetMapping("/couple") public AjaxResult couple(HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> c=mapper.selectCoupleByUser(uid);if(c!=null)c.put("anniversaries",mapper.selectAnniversaries(Long.valueOf(String.valueOf(c.get("id")))));return AjaxResult.success(c);}
 @Anonymous @PostMapping("/couple/anniversary") public AjaxResult anniversary(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> c=mapper.selectCoupleByUser(uid);if(c==null)return AjaxResult.error("请先绑定情侣空间");p.put("coupleId",c.get("id"));mapper.insertAnniversary(p);return AjaxResult.success();}
 @Anonymous @GetMapping("/couple/leaderboard") public AjaxResult leaderboard(){return AjaxResult.success(mapper.selectCoupleLeaderboard());}
 @Anonymous @GetMapping("/notifications") public AjaxResult notifications(HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> data=new HashMap<>();data.put("unread",mapper.countUnreadNotifications(uid));data.put("rows",mapper.selectNotifications(uid));return AjaxResult.success(data);}
 @Anonymous @PostMapping("/notifications/read") public AjaxResult readNotifications(HttpServletRequest r){Long uid=token.getRequiredUserId(r);mapper.readNotifications(uid);return AjaxResult.success();}
}
