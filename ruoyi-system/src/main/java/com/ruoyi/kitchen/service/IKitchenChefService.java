package com.ruoyi.kitchen.service;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenChef;

/**
 * 厨师Service接口
 *
 * @author ruoyi
 */
public interface IKitchenChefService
{
    public KitchenChef selectKitchenChefById(Long id);

    public List<KitchenChef> selectKitchenChefList(KitchenChef kitchenChef);

    public int insertKitchenChef(KitchenChef kitchenChef);

    public int updateKitchenChef(KitchenChef kitchenChef);

    public int deleteKitchenChefByIds(Long[] ids);

    public int deleteKitchenChefById(Long id);
}
