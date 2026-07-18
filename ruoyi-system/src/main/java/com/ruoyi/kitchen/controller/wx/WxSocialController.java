package com.ruoyi.kitchen.controller.wx;

import java.time.LocalDate;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenDish;
import com.ruoyi.kitchen.mapper.KitchenDishMapper;
import com.ruoyi.kitchen.mapper.KitchenSocialMapper;
import com.ruoyi.kitchen.util.WxTokenService;
import com.ruoyi.kitchen.web.WxFeatureRequired;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

@WxFeatureRequired(writeMethodsOnly = true, excludedPaths = {
        "/api/wx/social/couple/unbind", "/api/wx/social/notifications/read" })
@RestController @RequestMapping("/api/wx/social")
public class WxSocialController {
 @Autowired private KitchenSocialMapper mapper; @Autowired private KitchenDishMapper dishMapper; @Autowired private WxTokenService token;
 private String code(){return UUID.randomUUID().toString().replace("-","").substring(0,6).toUpperCase();}
 @Anonymous @PostMapping("/group/create") @Transactional public AjaxResult createGroup(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);if(p==null)p=new HashMap<>();String title=String.valueOf(p.getOrDefault("title","")).trim();if(StringUtils.isBlank(title))title="今天一起吃饭";if(title.length()>100)return AjaxResult.error("聚餐名称最多100个字");p.put("title",title);p.put("ownerUserId",uid);p.put("roomCode",code());mapper.insertGroupRoom(p);Map<String,Object> m=new HashMap<>();m.put("roomId",p.get("id"));m.put("userId",uid);mapper.insertGroupMember(m);return AjaxResult.success(p);}
 @Anonymous @PostMapping("/group/join") @Transactional public AjaxResult joinGroup(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);String roomCode=p==null?"":String.valueOf(p.getOrDefault("roomCode","")).trim().toUpperCase();if(!roomCode.matches("^[A-Z0-9]{6}$"))return AjaxResult.error("请输入正确的6位房间码");Map<String,Object> room=mapper.selectGroupRoomByCodeForUpdate(roomCode);if(room==null)return AjaxResult.error("房间不存在或已结束");p.put("roomId",room.get("id"));p.put("userId",uid);mapper.insertGroupMember(p);return AjaxResult.success(room);}
 @Anonymous @GetMapping("/group/my") public AjaxResult myGroups(HttpServletRequest r){Long uid=token.getRequiredUserId(r);return AjaxResult.success(mapper.selectMyGroupRooms(uid));}
 @Anonymous @GetMapping("/group/{id}") public AjaxResult group(@PathVariable Long id,HttpServletRequest r){Long uid=token.getRequiredUserId(r);if(mapper.countGroupMember(id,uid)==0)return AjaxResult.error("请先加入房间");Map<String,Object> room=mapper.selectGroupRoomById(id);if(room==null)return AjaxResult.error("房间不存在");List<Map<String,Object>> members=mapper.selectGroupMembers(id),items=mapper.selectGroupItems(id),purchasedItems=mapper.selectGroupPurchasedItems(id);double cartTotal=items.stream().mapToDouble(x->x.get("subtotal")==null?0:Double.parseDouble(String.valueOf(x.get("subtotal")))).sum();double purchasedTotal=purchasedItems.stream().mapToDouble(x->x.get("subtotal")==null?0:Double.parseDouble(String.valueOf(x.get("subtotal")))).sum();double total=cartTotal+purchasedTotal;room.put("members",members);room.put("items",items);room.put("purchasedItems",purchasedItems);room.put("cartTotal",Math.round(cartTotal*100)/100.0);room.put("purchasedTotal",Math.round(purchasedTotal*100)/100.0);room.put("total",Math.round(total*100)/100.0);room.put("aa",members.isEmpty()?0:Math.round(total/members.size()*100)/100.0);return AjaxResult.success(room);}
 @Anonymous @GetMapping("/group/qrcode/{id}") public AjaxResult groupQrCode(@PathVariable Long id,HttpServletRequest r){Long uid=token.getRequiredUserId(r);if(mapper.countGroupMember(id,uid)==0)return AjaxResult.error("你不在该房间");Map<String,Object> room=mapper.selectGroupRoomById(id);if(room==null)return AjaxResult.error("房间不存在");try{String code=String.valueOf(room.get("roomCode"));BitMatrix bits=new MultiFormatWriter().encode("pages/group-dining/group-dining?code="+code,BarcodeFormat.QR_CODE,420,420);BufferedImage image=new BufferedImage(420,420,BufferedImage.TYPE_INT_RGB);for(int x=0;x<420;x++){for(int y=0;y<420;y++){image.setRGB(x,y,bits.get(x,y)?0xFF172E28:0xFFFFFFFF);}}ByteArrayOutputStream out=new ByteArrayOutputStream();ImageIO.write(image,"png",out);Map<String,Object> data=new HashMap<>();data.put("roomCode",code);data.put("image","data:image/png;base64,"+Base64.getEncoder().encodeToString(out.toByteArray()));return AjaxResult.success(data);}catch(Exception e){return AjaxResult.error("二维码生成失败");}}
 @Anonymous @PostMapping("/group/item") @Transactional public AjaxResult addItem(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);Long roomId=longValue(p,"roomId"),dishId=longValue(p,"dishId");Integer quantity=intValue(p,"quantity",1);if(roomId==null||dishId==null)return AjaxResult.error("房间或菜品参数不正确");if(quantity==null||quantity<1||quantity>99)return AjaxResult.error("菜品数量必须在1到99之间");if(!dishAvailable(dishId))return AjaxResult.error("菜品不存在或已下架");if(mapper.selectGroupRoomByIdForUpdate(roomId)==null)return AjaxResult.error("房间不存在或已结束");if(mapper.countGroupMember(roomId,uid)==0)return AjaxResult.error("你不在该房间");p.put("roomId",roomId);p.put("dishId",dishId);p.put("userId",uid);p.put("quantity",quantity);mapper.upsertGroupItem(p);return AjaxResult.success();}
 @Anonymous @PostMapping("/group/item/quantity") @Transactional public AjaxResult changeItemQuantity(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);Long roomId=longValue(p,"roomId"),dishId=longValue(p,"dishId");Integer delta=intValue(p,"delta",null);if(roomId==null||dishId==null||delta==null)return AjaxResult.error("房间或菜品参数不正确");if(delta!=-1&&delta!=1)return AjaxResult.error("每次只能增减1份");if(mapper.selectGroupRoomByIdForUpdate(roomId)==null)return AjaxResult.error("房间不存在或已结束");if(mapper.countGroupMember(roomId,uid)==0)return AjaxResult.error("你不在该房间");p.put("roomId",roomId);p.put("dishId",dishId);p.put("userId",uid);p.put("delta",delta);if(mapper.changeGroupItemQuantity(p)==0)return AjaxResult.error(delta>0?"菜品数量已达上限":"菜品已被移除");mapper.deleteEmptyGroupItem(roomId,dishId);return AjaxResult.success();}
 @Anonymous @PostMapping("/group/clear/{id}") @Transactional public AjaxResult clearGroupCart(@PathVariable Long id,HttpServletRequest r){Long uid=token.getRequiredUserId(r);if(mapper.selectGroupRoomByIdForUpdate(id)==null)return AjaxResult.error("房间不存在或已结束");if(mapper.countGroupMember(id,uid)==0)return AjaxResult.error("你不在该房间");mapper.clearGroupItems(id);return AjaxResult.success("购物车已清空");}
 @Anonymous @PostMapping("/group/finish/{id}") @Transactional public AjaxResult finishGroup(@PathVariable Long id,HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> room=mapper.selectGroupRoomByIdForUpdate(id);if(room==null)return AjaxResult.error("房间不存在或已经结束");if(!uid.equals(Long.valueOf(String.valueOf(room.get("ownerUserId")))))return AjaxResult.error("只有发起人可以结束聚餐");return mapper.finishGroup(id,uid)>0?AjaxResult.success("聚餐已结束"):AjaxResult.error("结束聚餐失败");}
 @Anonymous
 @PostMapping("/couple/create")
 @Transactional(isolation = Isolation.READ_COMMITTED)
 public AjaxResult createCouple(@RequestBody Map<String,Object> p,HttpServletRequest r){
  Long uid=token.getRequiredUserId(r);
  if(p==null)p=new HashMap<>();
  String startDate=String.valueOf(p.getOrDefault("startDate",LocalDate.now().toString())).trim();
  try{LocalDate date=LocalDate.parse(startDate);if(date.isAfter(LocalDate.now()))return AjaxResult.error("相恋日期不能晚于今天");}catch(Exception e){return AjaxResult.error("请选择正确的相恋日期");}
  // 先锁稳定的用户行，再重查关系；同一用户的 create/join 必须串行。
  if(mapper.lockWxUser(uid)==null)return AjaxResult.error("用户状态异常，请重新登录");
  if(mapper.selectCoupleByUser(uid)!=null)return AjaxResult.error("你已经绑定了情侣空间");
  p.put("startDate",startDate);p.put("userId",uid);p.put("inviteCode",code());mapper.insertCouple(p);return AjaxResult.success(p);
 }
 @Anonymous
 @PostMapping("/couple/join")
 @Transactional(isolation = Isolation.READ_COMMITTED)
 public AjaxResult joinCouple(@RequestBody Map<String,Object> p,HttpServletRequest r){
  Long uid=token.getRequiredUserId(r);
  String inviteCode=p==null?"":String.valueOf(p.getOrDefault("inviteCode","")).trim().toUpperCase();
  if(!inviteCode.matches("^[A-Z0-9]{6}$"))return AjaxResult.error("请输入正确的6位邀请码");
  // 锁序始终是当前用户 -> 目标情侣空间，避免并发绑定和死锁。
  if(mapper.lockWxUser(uid)==null)return AjaxResult.error("用户状态异常，请重新登录");
  if(mapper.selectCoupleByUser(uid)!=null)return AjaxResult.error("你已经绑定了情侣空间");
  Map<String,Object> c=mapper.selectCoupleByCodeForUpdate(inviteCode);
  if(c==null)return AjaxResult.error("邀请码无效或该空间已解除");
  if(mapper.bindCouplePartner(Long.valueOf(String.valueOf(c.get("id"))),uid)==0)return AjaxResult.error("该空间已绑定或不能绑定自己");
  return AjaxResult.success();
 }
 @Anonymous @GetMapping("/couple") public AjaxResult couple(HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> c=mapper.selectCoupleByUser(uid);if(c!=null){Long id=Long.valueOf(String.valueOf(c.get("id")));c.put("currentUserId",uid);c.put("anniversaries",mapper.selectAnniversaries(id));c.put("items",mapper.selectCoupleItems(id));}return AjaxResult.success(c);}
 @Anonymous @PostMapping("/couple/item") @Transactional public AjaxResult setCoupleItem(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> c=mapper.selectCoupleByUserForUpdate(uid);if(c==null)return AjaxResult.error("请先绑定情侣空间");Object partner=c.get("userB");if(partner==null)partner=c.get("userb");if(partner==null)partner=c.get("user_b");if(partner==null)return AjaxResult.error("对方尚未完成情侣绑定");Long dishId=longValue(p,"dishId");Integer quantity=intValue(p,"quantity",null);if(dishId==null||quantity==null)return AjaxResult.error("菜品参数不正确");if(quantity<0||quantity>99)return AjaxResult.error("菜品数量必须在0到99之间");if(quantity>0&&!dishAvailable(dishId))return AjaxResult.error("菜品不存在或已下架");Long coupleId=Long.valueOf(String.valueOf(c.get("id")));if(quantity==0)mapper.deleteCoupleItem(coupleId,dishId,uid);else{p.put("coupleId",coupleId);p.put("dishId",dishId);p.put("userId",uid);p.put("quantity",quantity);mapper.upsertCoupleItem(p);}return AjaxResult.success(mapper.selectCoupleItems(coupleId));}
 @Anonymous @PostMapping("/couple/anniversary") public AjaxResult anniversary(@RequestBody Map<String,Object> p,HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> c=mapper.selectCoupleByUser(uid);if(c==null)return AjaxResult.error("请先绑定情侣空间");String title=p==null?"":String.valueOf(p.getOrDefault("title","")).trim(),date=p==null?"":String.valueOf(p.getOrDefault("anniversaryDate","")).trim();if(StringUtils.isBlank(title))return AjaxResult.error("请填写纪念日名称");if(title.length()>80)return AjaxResult.error("纪念日名称最多80个字");try{LocalDate.parse(date);}catch(Exception e){return AjaxResult.error("请选择正确的纪念日日期");}p.put("title",title);p.put("anniversaryDate",date);p.put("coupleId",c.get("id"));mapper.insertAnniversary(p);return AjaxResult.success();}
 @Anonymous @PostMapping("/couple/unbind") @Transactional public AjaxResult unbindCouple(HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> c=mapper.selectCoupleByUserForUpdate(uid);if(c==null)return AjaxResult.error("当前没有可解除的情侣空间");return mapper.unbindCouple(Long.valueOf(String.valueOf(c.get("id"))),uid)>0?AjaxResult.success("已解除情侣空间"):AjaxResult.error("解除失败，请稍后重试");}
 @Anonymous @GetMapping("/couple/leaderboard") public AjaxResult leaderboard(HttpServletRequest r){token.getRequiredUserId(r);return AjaxResult.success(mapper.selectCoupleLeaderboard());}
 @Anonymous @GetMapping("/notifications") public AjaxResult notifications(HttpServletRequest r){Long uid=token.getRequiredUserId(r);Map<String,Object> data=new HashMap<>();data.put("unread",mapper.countUnreadNotifications(uid));data.put("rows",mapper.selectNotifications(uid));return AjaxResult.success(data);}
 @Anonymous @PostMapping("/notifications/read") public AjaxResult readNotifications(HttpServletRequest r){Long uid=token.getRequiredUserId(r);mapper.readNotifications(uid);return AjaxResult.success();}
 private Long longValue(Map<String,Object> p,String key){if(p==null||p.get(key)==null)return null;try{return Long.valueOf(String.valueOf(p.get(key)));}catch(Exception e){return null;}}
 private Integer intValue(Map<String,Object> p,String key,Integer fallback){if(p==null||p.get(key)==null)return fallback;try{return Integer.valueOf(String.valueOf(p.get(key)));}catch(Exception e){return null;}}
 private boolean dishAvailable(Long id){KitchenDish dish=id==null?null:dishMapper.selectKitchenDishById(id);return dish!=null&&"1".equals(dish.getStatus());}
}
