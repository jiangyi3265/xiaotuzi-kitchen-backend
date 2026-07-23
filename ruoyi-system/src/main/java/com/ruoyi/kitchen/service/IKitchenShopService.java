package com.ruoyi.kitchen.service;

import com.ruoyi.kitchen.domain.KitchenShop;

/**
 * 厨房/店铺设置Service接口
 *
 * @author ruoyi
 */
public interface IKitchenShopService
{
    /** 获取配置（不存在则返回空对象） */
    public KitchenShop getShop();

    /** 获取小程序公开配置（只返回启用的菜市场） */
    public KitchenShop getPublicShop();

    /** 保存配置（无则新增，有则更新） */
    public int saveShop(KitchenShop kitchenShop);
}
