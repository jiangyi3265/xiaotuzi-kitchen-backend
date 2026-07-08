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
import com.ruoyi.kitchen.domain.KitchenOrder;
import com.ruoyi.kitchen.service.IKitchenOrderService;

/**
 * 订单Controller（后台管理端）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/kitchen/order")
public class KitchenOrderController extends BaseController
{
    @Autowired
    private IKitchenOrderService kitchenOrderService;

    @PreAuthorize("@ss.hasPermi('kitchen:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(KitchenOrder kitchenOrder)
    {
        startPage();
        List<KitchenOrder> list = kitchenOrderService.selectKitchenOrderList(kitchenOrder);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('kitchen:order:export')")
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KitchenOrder kitchenOrder)
    {
        List<KitchenOrder> list = kitchenOrderService.selectKitchenOrderList(kitchenOrder);
        ExcelUtil<KitchenOrder> util = new ExcelUtil<>(KitchenOrder.class);
        util.exportExcel(response, list, "订单数据");
    }

    @PreAuthorize("@ss.hasPermi('kitchen:order:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kitchenOrderService.selectKitchenOrderById(id));
    }

    /**
     * 修改订单（收货信息/备注等）
     */
    @PreAuthorize("@ss.hasPermi('kitchen:order:edit')")
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KitchenOrder kitchenOrder)
    {
        return toAjax(kitchenOrderService.updateKitchenOrder(kitchenOrder));
    }

    /**
     * 修改订单状态（接单/制作中/完成/取消）
     */
    @PreAuthorize("@ss.hasPermi('kitchen:order:edit')")
    @Log(title = "订单状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody KitchenOrder kitchenOrder)
    {
        return toAjax(kitchenOrderService.changeOrderStatus(kitchenOrder.getId(), kitchenOrder.getOrderStatus()));
    }

    @PreAuthorize("@ss.hasPermi('kitchen:order:remove')")
    @Log(title = "订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kitchenOrderService.deleteKitchenOrderByIds(ids));
    }
}
