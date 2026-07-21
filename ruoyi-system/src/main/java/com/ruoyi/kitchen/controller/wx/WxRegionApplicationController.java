package com.ruoyi.kitchen.controller.wx;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenRegionApplication;
import com.ruoyi.kitchen.mapper.KitchenRegionApplicationMapper;
import com.ruoyi.kitchen.mapper.KitchenWxUserMapper;
import com.ruoyi.kitchen.util.WxTokenService;
import com.ruoyi.kitchen.web.WxFeatureRequired;

@RestController
@RequestMapping("/api/wx/region")
public class WxRegionApplicationController {
    @Autowired private KitchenRegionApplicationMapper mapper;
    @Autowired private KitchenWxUserMapper wxUserMapper;
    @Autowired private WxTokenService tokenService;
    @Anonymous @GetMapping("/opened") public AjaxResult opened(HttpServletResponse response) {
        disableCache(response);
        return AjaxResult.success(mapper.selectEnabledRegions());
    }
    @Anonymous @GetMapping("/status") public AjaxResult status(KitchenRegionApplication q, HttpServletRequest request, HttpServletResponse response) {
        disableCache(response);
        List<KitchenRegionApplication> enabledRegions=mapper.selectEnabledRegions();
        KitchenRegionApplication matched=findEnabledRegion(enabledRegions,q);
        KitchenRegionApplication resolvedRegion=matched;
        Long uid=tokenService.getUserId(request); KitchenRegionApplication latest=uid==null?null:mapper.selectLatestByUser(uid);
        if(matched==null&&!hasCompleteRegion(q)&&latest!=null&&"1".equals(latest.getAuditStatus())&&"1".equals(latest.getEnabled())) {
            matched=findEnabledRegion(enabledRegions,latest);
            if(matched!=null) resolvedRegion=latest;
        }
        Map<String,Object> data=new HashMap<>();
        data.put("opened",matched!=null);
        data.put("application",latest);
        data.put("region",resolvedRegion==null?null:regionData(resolvedRegion));
        return AjaxResult.success(data);
    }
    @Anonymous @PostMapping("/apply") @WxFeatureRequired @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class) public AjaxResult apply(@RequestBody KitchenRegionApplication a, HttpServletRequest request) {
        Long uid=tokenService.getRequiredUserId(request);
        normalize(a);
        if(StringUtils.isBlank(a.getApplicantName())||StringUtils.isBlank(a.getPhone())||StringUtils.isBlank(a.getProvince())||StringUtils.isBlank(a.getCity())||StringUtils.isBlank(a.getDistrict())) return AjaxResult.error("请完整填写申请信息");
        if(!a.getPhone().matches("^1\\d{10}$")) return AjaxResult.error("请输入正确的11位手机号");
        if(tooLong(a.getApplicantName(),50)||tooLong(a.getPhone(),20)||tooLong(a.getProvince(),50)||tooLong(a.getCity(),50)||tooLong(a.getDistrict(),50)||tooLong(a.getAddress(),255)||tooLong(a.getExperience(),500)) return AjaxResult.error("申请信息过长，请精简后再提交");
        if(wxUserMapper.lockActiveUser(uid)==null) return AjaxResult.error("用户不存在或已停用");
        KitchenRegionApplication old=mapper.selectLatestByUser(uid); if(old!=null&&"0".equals(old.getAuditStatus())) return AjaxResult.error("申请正在审核中，请勿重复提交");
        a.setWxUserId(uid); mapper.insert(a); return AjaxResult.success("申请已提交，请等待后台审核",a);
    }

    private void normalize(KitchenRegionApplication a) {
        a.setApplicantName(trim(a.getApplicantName())); a.setPhone(trim(a.getPhone()));
        a.setProvince(trim(a.getProvince())); a.setCity(trim(a.getCity())); a.setDistrict(trim(a.getDistrict()));
        a.setAddress(trim(a.getAddress())); a.setExperience(trim(a.getExperience()));
    }
    private String trim(String value) { return value==null?null:value.trim(); }
    private boolean tooLong(String value,int max) { return value!=null&&value.length()>max; }
    private boolean hasCompleteRegion(KitchenRegionApplication region) {
        return region!=null&&StringUtils.isNotBlank(region.getProvince())&&StringUtils.isNotBlank(region.getCity())&&StringUtils.isNotBlank(region.getDistrict());
    }
    private KitchenRegionApplication findEnabledRegion(List<KitchenRegionApplication> regions,KitchenRegionApplication query) {
        if(!hasCompleteRegion(query)||regions==null) return null;
        for(KitchenRegionApplication region:regions) {
            if(sameRegionPart(region.getProvince(),query.getProvince())
                    &&sameRegionPart(region.getCity(),query.getCity())
                    &&sameRegionPart(region.getDistrict(),query.getDistrict())) return region;
        }
        return null;
    }
    private boolean sameRegionPart(String left,String right) { return canonicalRegionPart(left).equals(canonicalRegionPart(right)); }
    private String canonicalRegionPart(String value) {
        String normalized=StringUtils.defaultString(value).trim().replaceAll("\\s+","").toLowerCase(Locale.ROOT);
        String[] suffixes={"维吾尔自治区","壮族自治区","回族自治区","特别行政区","自治区","自治州","地区","省","市","区","县","盟"};
        for(String suffix:suffixes) {
            if(normalized.endsWith(suffix)&&normalized.length()>suffix.length()) return normalized.substring(0,normalized.length()-suffix.length());
        }
        return normalized;
    }
    private Map<String,String> regionData(KitchenRegionApplication region) {
        Map<String,String> data=new HashMap<>();
        data.put("province",region.getProvince()); data.put("city",region.getCity()); data.put("district",region.getDistrict()); data.put("address",region.getAddress());
        return data;
    }
    private void disableCache(HttpServletResponse response) {
        response.setHeader("Cache-Control","no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma","no-cache");
        response.setDateHeader("Expires",0L);
    }
}
