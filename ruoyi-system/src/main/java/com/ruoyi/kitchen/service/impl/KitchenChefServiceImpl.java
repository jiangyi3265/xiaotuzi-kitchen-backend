package com.ruoyi.kitchen.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.kitchen.domain.KitchenChef;
import com.ruoyi.kitchen.mapper.KitchenChefMapper;
import com.ruoyi.kitchen.service.IKitchenChefService;

/**
 * 厨师Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class KitchenChefServiceImpl implements IKitchenChefService
{
    @Autowired
    private KitchenChefMapper kitchenChefMapper;

    @Override
    public KitchenChef selectKitchenChefById(Long id)
    {
        return kitchenChefMapper.selectKitchenChefById(id);
    }

    @Override
    public List<KitchenChef> selectKitchenChefList(KitchenChef kitchenChef)
    {
        return kitchenChefMapper.selectKitchenChefList(kitchenChef);
    }

    @Override
    public int insertKitchenChef(KitchenChef kitchenChef)
    {
        return kitchenChefMapper.insertKitchenChef(kitchenChef);
    }

    @Override
    public int updateKitchenChef(KitchenChef kitchenChef)
    {
        return kitchenChefMapper.updateKitchenChef(kitchenChef);
    }

    @Override
    public int deleteKitchenChefByIds(Long[] ids)
    {
        return kitchenChefMapper.deleteKitchenChefByIds(ids);
    }

    @Override
    public int deleteKitchenChefById(Long id)
    {
        return kitchenChefMapper.deleteKitchenChefById(id);
    }
}
