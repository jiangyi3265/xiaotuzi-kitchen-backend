package com.ruoyi.kitchen.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.kitchen.domain.KitchenComment;
import com.ruoyi.kitchen.service.IKitchenCommentService;

/**
 * 成品评论Controller（后台管理端，含内容审核）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/kitchen/comment")
public class KitchenCommentController extends BaseController
{
    @Autowired
    private IKitchenCommentService kitchenCommentService;

    @PreAuthorize("@ss.hasPermi('kitchen:comment:list')")
    @GetMapping("/list")
    public TableDataInfo list(KitchenComment kitchenComment)
    {
        startPage();
        List<KitchenComment> list = kitchenCommentService.selectKitchenCommentList(kitchenComment);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('kitchen:comment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kitchenCommentService.selectKitchenCommentById(id));
    }

    @PreAuthorize("@ss.hasPermi('kitchen:comment:edit')")
    @Log(title = "成品评论", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KitchenComment kitchenComment)
    {
        return toAjax(kitchenCommentService.updateKitchenComment(kitchenComment));
    }

    /**
     * 内容审核（通过/驳回）
     */
    @PreAuthorize("@ss.hasPermi('kitchen:comment:edit')")
    @Log(title = "成品评论审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody KitchenComment kitchenComment)
    {
        return toAjax(kitchenCommentService.audit(kitchenComment.getId(), kitchenComment.getAuditStatus()));
    }

    @PreAuthorize("@ss.hasPermi('kitchen:comment:remove')")
    @Log(title = "成品评论", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kitchenCommentService.deleteKitchenCommentByIds(ids));
    }
}
