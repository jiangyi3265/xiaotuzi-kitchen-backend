package com.ruoyi.kitchen.controller.wx;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenCategory;
import com.ruoyi.kitchen.service.IKitchenCategoryService;
import com.ruoyi.kitchen.util.WxTokenService;

/**
 * 菜品分类Controller（微信小程序端）
 * 读接口公开；写接口（店主在小程序里管理分类）需登录。
 * 说明：单店铺设计，小程序内的分类管理直接维护后台同一批数据；
 *       生产环境如需区分"店主/顾客"，可在此加 owner 校验。
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/wx/category")
public class WxCategoryController
{
    @Autowired
    private IKitchenCategoryService kitchenCategoryService;

    @Autowired
    private WxTokenService wxTokenService;

    /**
     * 小程序：获取三级分类树（仅正常状态）
     */
    @Anonymous
    @GetMapping("/tree")
    public AjaxResult tree()
    {
        KitchenCategory query = new KitchenCategory();
        query.setStatus("0");
        return AjaxResult.success(kitchenCategoryService.buildCategoryTree(query));
    }

    /**
     * 小程序：分类管理列表（店主用，含停用，按顺序返回一级分类）
     */
    @Anonymous
    @GetMapping("/manage/list")
    public AjaxResult manageList(HttpServletRequest request)
    {
        wxTokenService.getRequiredOwnerId(request);
        KitchenCategory query = new KitchenCategory();
        query.setParentId(0L);
        return AjaxResult.success(kitchenCategoryService.selectKitchenCategoryList(query));
    }

    /**
     * 小程序：新增分类（店主，默认一级）
     */
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody KitchenCategory category, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredOwnerId(request);
        if (StringUtils.isBlank(category.getCatName()))
        {
            return AjaxResult.error("请填写分类名称");
        }
        if (category.getParentId() == null)
        {
            category.setParentId(0L);
        }
        category.setCreateBy("wx:" + userId);
        return toAjax(kitchenCategoryService.insertKitchenCategory(category));
    }

    /**
     * 小程序：修改分类（店主）
     */
    @Anonymous
    @PutMapping
    public AjaxResult edit(@RequestBody KitchenCategory category, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredOwnerId(request);
        if (category.getId() == null)
        {
            return AjaxResult.error("缺少分类ID");
        }
        category.setUpdateBy("wx:" + userId);
        return toAjax(kitchenCategoryService.updateKitchenCategory(category));
    }

    /**
     * 小程序：删除分类（店主）
     */
    @Anonymous
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") Long id, HttpServletRequest request)
    {
        wxTokenService.getRequiredOwnerId(request);
        return toAjax(kitchenCategoryService.deleteKitchenCategoryById(id));
    }

    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
