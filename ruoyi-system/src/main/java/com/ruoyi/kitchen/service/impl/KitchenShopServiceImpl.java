package com.ruoyi.kitchen.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.kitchen.domain.KitchenShop;
import com.ruoyi.kitchen.mapper.KitchenShopMapper;
import com.ruoyi.kitchen.service.IKitchenShopService;

/**
 * 厨房/店铺设置Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class KitchenShopServiceImpl implements IKitchenShopService
{
    @Autowired
    private KitchenShopMapper kitchenShopMapper;

    @Override
    public KitchenShop getShop()
    {
        KitchenShop shop = kitchenShopMapper.selectKitchenShop();
        return shop == null ? new KitchenShop() : shop;
    }

    @Override
    public int saveShop(KitchenShop kitchenShop)
    {
        KitchenShop exist = kitchenShopMapper.selectKitchenShop();
        if (exist == null)
        {
            return kitchenShopMapper.insertKitchenShop(kitchenShop);
        }
        kitchenShop.setId(exist.getId());
        return kitchenShopMapper.updateKitchenShop(kitchenShop);
    }
}
