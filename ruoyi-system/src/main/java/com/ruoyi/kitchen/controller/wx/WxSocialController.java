package com.ruoyi.kitchen.controller.wx;

import java.time.LocalDate;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.mapper.KitchenSocialMapper;
import com.ruoyi.kitchen.util.WxTokenService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

@RestController @RequestMapping("/api/wx/social")
public class WxSocialController {
 @Autowired private KitchenSocialMapper mapper; @Autowired private WxTokenService token;
 private String code(){return UUID.randomUUID().toString().replace("-","").substring(0,6).toUpperCase();}
 @Anonymous @PostMapping("/group/create") @Transactional public AjaxResult createGroup(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);p.put("ownerUserId",uid);p.put("roomCode",code());if(StringUtils.isBlank((String)p.get("title")))p.put("title","今天一起吃饭");mapper.insertGroupRoom(p);Map<String,Object> m=new HashMap<>();m.put("roomId",p.get("id"));m.put("userId",uid);mapper.insertGroupMember(m);return AjaxResult.success(p);}
 @Anonymous @PostMapping("/group/join") @Transactional public AjaxResult joinGroup(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> room=mapper.selectGroupRoomByCode(String.valueOf(p.get("roomCode")).toUpperCase());if(room==null)return AjaxResult.error("房间不存在或已结束");p.put("roomId",room.get("id"));p.put("userId",uid);mapper.insertGroupMember(p);return AjaxResult.success(room);}
 @Anonymous @GetMapping("/group/my") public AjaxResult myGroups(HttpServletRequest r){Long uid=token.getRequiredUserId(r);return AjaxResult.success(mapper.selectMyGroupRooms(uid));}
 @Anonymous @GetMapping("/group/{id}") public AjaxResult group(@PathVariable Long id,HttpServletRequest r){Long uid=token.getRequiredUserId(r);if(mapper.countGroupMember(id,uid)==0)return AjaxResult.error("请先加入房间");Map<String,Object> room=mapper.selectGroupRoomById(id);if(room==null)return AjaxResult.error("房间不存在");List<Map<String,Object>> members=mapper.selectGroupMembers(id),items=mapper.selectGroupItems(id);double total=items.stream().mapToDouble(x->x.get("subtotal")==null?0:Double.parseDouble(String.valueOf(x.get("subtotal")))).sum();room.put("members",members);room.put("items",items);room.put("total",Math.round(total*100)/100.0);room.put("aa",members.isEmpty()?0:Math.round(total/members.size()*100)/100.0);return AjaxResult.success(room);}
 @Anonymous @GetMapping("/group/qrcode/{id}") public AjaxResult groupQrCode(@PathVariable Long id,HttpServletRequest r){Long uid=token.getRequiredUserId(r);if(mapper.countGroupMember(id,uid)==0)return AjaxResult.error("你不在该房间");Map<String,Object> room=mapper.selectGroupRoomById(id);if(room==null)return AjaxResult.error("房间不存在");try{String code=String.valueOf(room.get("roomCode"));BitMatrix bits=new MultiFormatWriter().encode("pages/group-dining/group-dining?code="+code,BarcodeFormat.QR_CODE,420,420);BufferedImage image=new BufferedImage(420,420,BufferedImage.TYPE_INT_RGB);for(int x=0;x<420;x++){for(int y=0;y<420;y++){image.setRGB(x,y,bits.get(x,y)?0xFF172E28:0xFFFFFFFF);}}ByteArrayOutputStream out=new ByteArrayOutputStream();ImageIO.write(image,"png",out);Map<String,Object> data=new HashMap<>();data.put("roomCode",code);data.put("image","data:image/png;base64,"+Base64.getEncoder().encodeToString(out.toByteArray()));return AjaxResult.success(data);}catch(Exception e){return AjaxResult.error("二维码生成失败");}}
 @Anonymous @PostMapping("/group/item") public AjaxResult addItem(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);Long roomId=Long.valueOf(String.valueOf(p.get("roomId")));int quantity=p.get("quantity")==null?1:Integer.parseInt(String.valueOf(p.get("quantity")));if(quantity<1||quantity>99)return AjaxResult.error("菜品数量必须在1到99之间");if(mapper.selectGroupRoomById(roomId)==null)return AjaxResult.error("房间不存在或已结束");if(mapper.countGroupMember(roomId,uid)==0)return AjaxResult.error("你不在该房间");p.put("userId",uid);p.put("quantity",quantity);mapper.upsertGroupItem(p);return AjaxResult.success();}
 @Anonymous @PostMapping("/group/finish/{id}") public AjaxResult finishGroup(@PathVariable Long id,HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> room=mapper.selectGroupRoomAnyById(id);if(room==null)return AjaxResult.error("房间不存在");if(!uid.equals(Long.valueOf(String.valueOf(room.get("ownerUserId")))))return AjaxResult.error("只有发起人可以结束聚餐");if(!"1".equals(String.valueOf(room.get("status"))))return AjaxResult.error("聚餐已经结束");return mapper.finishGroup(id,uid)>0?AjaxResult.success("聚餐已结束"):AjaxResult.error("结束聚餐失败");}
 @Anonymous @PostMapping("/couple/create") public AjaxResult createCouple(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);if(mapper.selectCoupleByUser(uid)!=null)return AjaxResult.error("你已经绑定了情侣空间");p.put("userId",uid);p.put("inviteCode",code());p.putIfAbsent("startDate",LocalDate.now().toString());mapper.insertCouple(p);return AjaxResult.success(p);}
 @Anonymous @PostMapping("/couple/join") public AjaxResult joinCouple(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);if(mapper.selectCoupleByUser(uid)!=null)return AjaxResult.error("你已经绑定了情侣空间");Map<String,Object> c=mapper.selectCoupleByCode(String.valueOf(p.get("inviteCode")).toUpperCase());if(c==null)return AjaxResult.error("邀请码无效");if(mapper.bindCouplePartner(Long.valueOf(String.valueOf(c.get("id"))),uid)==0)return AjaxResult.error("该空间已绑定或不能绑定自己");return AjaxResult.success();}
 @Anonymous @GetMapping("/couple") public AjaxResult couple(HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> c=mapper.selectCoupleByUser(uid);if(c!=null)c.put("anniversaries",mapper.selectAnniversaries(Long.valueOf(String.valueOf(c.get("id")))));return AjaxResult.success(c);}
 @Anonymous @PostMapping("/couple/anniversary") public AjaxResult anniversary(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> c=mapper.selectCoupleByUser(uid);if(c==null)return AjaxResult.error("请先绑定情侣空间");p.put("coupleId",c.get("id"));mapper.insertAnniversary(p);return AjaxResult.success();}
 @Anonymous @PostMapping("/couple/unbind") public AjaxResult unbindCouple(HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> c=mapper.selectCoupleByUser(uid);if(c==null)return AjaxResult.error("当前没有可解除的情侣空间");return mapper.unbindCouple(Long.valueOf(String.valueOf(c.get("id"))),uid)>0?AjaxResult.success("已解除情侣空间"):AjaxResult.error("解除失败，请稍后重试");}
 @Anonymous @GetMapping("/couple/leaderboard") public AjaxResult leaderboard(){return AjaxResult.success(mapper.selectCoupleLeaderboard());}
 @Anonymous @GetMapping("/notifications") public AjaxResult notifications(HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> data=new HashMap<>();data.put("unread",mapper.countUnreadNotifications(uid));data.put("rows",mapper.selectNotifications(uid));return AjaxResult.success(data);}
 @Anonymous @PostMapping("/notifications/read") public AjaxResult readNotifications(HttpServletRequest r){Long uid=token.getRequiredUserId(r);mapper.readNotifications(uid);return AjaxResult.success();}
}
