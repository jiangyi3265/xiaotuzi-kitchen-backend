package com.ruoyi.kitchen.util;

import java.util.List;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;

/**
 * 小程序端分页工具
 * 小程序接口不经过 BaseController，统一在此封装 PageHelper 分页与返回结构
 *
 * @author ruoyi
 */
public class WxPageUtils
{
    /**
     * 开启分页（读取请求中的 pageNum/pageSize）
     */
    public static void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (pageNum != null && pageSize != null)
        {
            PageHelper.startPage(pageNum, pageSize);
        }
    }

    /**
     * 封装分页返回结构
     */
    public static TableDataInfo getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }
}
