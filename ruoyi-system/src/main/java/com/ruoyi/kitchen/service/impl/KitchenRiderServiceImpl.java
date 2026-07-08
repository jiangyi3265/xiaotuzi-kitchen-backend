package com.ruoyi.kitchen.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.kitchen.domain.KitchenRider;
import com.ruoyi.kitchen.mapper.KitchenRiderMapper;
import com.ruoyi.kitchen.service.IKitchenRiderService;

/**
 * 配送员Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class KitchenRiderServiceImpl implements IKitchenRiderService
{
    @Autowired
    private KitchenRiderMapper kitchenRiderMapper;

    @Override
    public KitchenRider selectKitchenRiderById(Long id)
    {
        return kitchenRiderMapper.selectKitchenRiderById(id);
    }

    @Override
    public List<KitchenRider> selectKitchenRiderList(KitchenRider kitchenRider)
    {
        return kitchenRiderMapper.selectKitchenRiderList(kitchenRider);
    }

    @Override
    public int insertKitchenRider(KitchenRider kitchenRider)
    {
        return kitchenRiderMapper.insertKitchenRider(kitchenRider);
    }

    @Override
    public int updateKitchenRider(KitchenRider kitchenRider)
    {
        return kitchenRiderMapper.updateKitchenRider(kitchenRider);
    }

    @Override
    public int deleteKitchenRiderByIds(Long[] ids)
    {
        return kitchenRiderMapper.deleteKitchenRiderByIds(ids);
    }

    @Override
    public int deleteKitchenRiderById(Long id)
    {
        return kitchenRiderMapper.deleteKitchenRiderById(id);
    }
}
