package com.ruoyi.kitchen.mapper;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenChef;

/**
 * 厨师Mapper接口
 *
 * @author ruoyi
 */
public interface KitchenChefMapper
{
    public KitchenChef selectKitchenChefById(Long id);

    public List<KitchenChef> selectKitchenChefList(KitchenChef kitchenChef);

    public int insertKitchenChef(KitchenChef kitchenChef);

    public int updateKitchenChef(KitchenChef kitchenChef);

    public int deleteKitchenChefById(Long id);

    public int deleteKitchenChefByIds(Long[] ids);
}
