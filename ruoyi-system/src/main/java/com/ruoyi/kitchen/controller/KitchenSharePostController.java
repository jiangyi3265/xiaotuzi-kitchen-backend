package com.ruoyi.kitchen.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.kitchen.domain.KitchenSharePost;
import com.ruoyi.kitchen.service.IKitchenSharePostService;

/**
 * 分享广场Controller（后台管理端，含内容审核）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/kitchen/sharePost")
public class KitchenSharePostController extends BaseController
{
    @Autowired
    private IKitchenSharePostService kitchenSharePostService;

    @PreAuthorize("@ss.hasPermi('kitchen:sharePost:list')")
    @GetMapping("/list")
    public TableDataInfo list(KitchenSharePost kitchenSharePost)
    {
        startPage();
        List<KitchenSharePost> list = kitchenSharePostService.selectKitchenSharePostList(kitchenSharePost);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('kitchen:sharePost:export')")
    @Log(title = "分享广场", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KitchenSharePost kitchenSharePost)
    {
        List<KitchenSharePost> list = kitchenSharePostService.selectKitchenSharePostList(kitchenSharePost);
        ExcelUtil<KitchenSharePost> util = new ExcelUtil<>(KitchenSharePost.class);
        util.exportExcel(response, list, "分享广场数据");
    }

    @PreAuthorize("@ss.hasPermi('kitchen:sharePost:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kitchenSharePostService.selectKitchenSharePostById(id));
    }

    @PreAuthorize("@ss.hasPermi('kitchen:sharePost:edit')")
    @Log(title = "分享广场", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KitchenSharePost kitchenSharePost)
    {
        return toAjax(kitchenSharePostService.updateKitchenSharePost(kitchenSharePost));
    }

    /**
     * 内容审核（通过/驳回）
     */
    @PreAuthorize("@ss.hasPermi('kitchen:sharePost:audit')")
    @Log(title = "分享广场审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody KitchenSharePost kitchenSharePost)
    {
        return toAjax(kitchenSharePostService.audit(kitchenSharePost.getId(), kitchenSharePost.getAuditStatus()));
    }

    @PreAuthorize("@ss.hasPermi('kitchen:sharePost:remove')")
    @Log(title = "分享广场", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kitchenSharePostService.deleteKitchenSharePostByIds(ids));
    }
}
