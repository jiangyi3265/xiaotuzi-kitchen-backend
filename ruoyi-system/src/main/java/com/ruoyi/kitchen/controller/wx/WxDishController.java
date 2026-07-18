package com.ruoyi.kitchen.controller.wx;

import java.util.List;
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
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenDish;
import com.ruoyi.kitchen.service.IKitchenDishService;
import com.ruoyi.kitchen.util.WxTokenService;

/**
 * 菜品Controller（微信小程序端）
 * 读接口公开只读；写接口（店主在小程序里管理菜品）需登录。
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/wx/dish")
public class WxDishController
{
    @Autowired
    private IKitchenDishService kitchenDishService;

    @Autowired
    private WxTokenService wxTokenService;

    /**
     * 小程序：菜品列表（按分类筛选，仅上架，支持分页）
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(KitchenDish kitchenDish)
    {
        kitchenDish.setStatus("1"); // 强制只查上架，防止越权看到下架菜品
        TableDataInfo result = page(kitchenDish);
        for (Object row : result.getRows())
        {
            ((KitchenDish) row).setCookingExp(null);
        }
        return result;
    }

    /**
     * 小程序：菜品管理列表（店主用，含下架，支持按分类/分页）
     */
    @Anonymous
    @GetMapping("/manage/list")
    public TableDataInfo manageList(KitchenDish kitchenDish, HttpServletRequest request)
    {
        wxTokenService.getRequiredOwnerId(request);
        return page(kitchenDish);
    }

    private TableDataInfo page(KitchenDish kitchenDish)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (pageNum != null && pageSize != null)
        {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<KitchenDish> list = kitchenDishService.selectKitchenDishList(kitchenDish);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new com.github.pagehelper.PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 小程序：菜品详情（含规格、做法步骤）
     */
    @Anonymous
    @GetMapping("/detail/{id}")
    public AjaxResult detail(@PathVariable("id") Long id)
    {
        KitchenDish dish = kitchenDishService.selectKitchenDishById(id);
        if (dish == null || !"1".equals(dish.getStatus()))
        {
            return AjaxResult.error("菜品不存在或已下架");
        }
        // 做法未公开则隐藏步骤与经验
        if (!"1".equals(dish.getRecipeOpen()))
        {
            dish.setSteps(null);
            dish.setCookingExp(null);
        }
        return AjaxResult.success(dish);
    }

    /**
     * 小程序：新增菜品（店主，级联规格、步骤）
     */
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody KitchenDish dish, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredOwnerId(request);
        if (StringUtils.isBlank(dish.getDishName()))
        {
            return AjaxResult.error("请填写菜品名称");
        }
        if (dish.getCategoryId() == null)
        {
            return AjaxResult.error("请选择分类");
        }
        dish.setCreateBy("wx:" + userId);
        int rows = kitchenDishService.insertKitchenDish(dish);
        AjaxResult ajax = toAjax(rows);
        ajax.put("id", dish.getId());
        return ajax;
    }

    /**
     * 小程序：修改菜品（店主）
     */
    @Anonymous
    @PutMapping
    public AjaxResult edit(@RequestBody KitchenDish dish, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredOwnerId(request);
        if (dish.getId() == null)
        {
            return AjaxResult.error("缺少菜品ID");
        }
        dish.setUpdateBy("wx:" + userId);
        return toAjax(kitchenDishService.updateKitchenDish(dish));
    }

    /**
     * 小程序：上下架（店主，仅改状态，不动规格/步骤）
     */
    @Anonymous
    @PutMapping("/status")
    public AjaxResult changeStatus(@RequestBody KitchenDish dish, HttpServletRequest request)
    {
        wxTokenService.getRequiredOwnerId(request);
        if (dish.getId() == null || StringUtils.isBlank(dish.getStatus()))
        {
            return AjaxResult.error("参数不完整");
        }
        return toAjax(kitchenDishService.updateDishStatus(dish.getId(), dish.getStatus()));
    }

    /**
     * 小程序：删除菜品（店主）
     */
    @Anonymous
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") Long id, HttpServletRequest request)
    {
        wxTokenService.getRequiredOwnerId(request);
        return toAjax(kitchenDishService.deleteKitchenDishById(id));
    }

    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
