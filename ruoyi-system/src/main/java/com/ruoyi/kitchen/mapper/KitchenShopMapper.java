package com.ruoyi.kitchen.mapper;

import com.ruoyi.kitchen.domain.KitchenShop;

/**
 * 厨房/店铺设置Mapper接口
 *
 * @author ruoyi
 */
public interface KitchenShopMapper
{
    /** 查询配置（取第一条） */
    public KitchenShop selectKitchenShop();

    public KitchenShop selectKitchenShopById(Long id);

    public int insertKitchenShop(KitchenShop kitchenShop);

    public int updateKitchenShop(KitchenShop kitchenShop);
}
