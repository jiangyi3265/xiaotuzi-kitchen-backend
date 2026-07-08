package com.ruoyi.kitchen.mapper;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenRider;

/**
 * 配送员Mapper接口
 *
 * @author ruoyi
 */
public interface KitchenRiderMapper
{
    public KitchenRider selectKitchenRiderById(Long id);

    public List<KitchenRider> selectKitchenRiderList(KitchenRider kitchenRider);

    public int insertKitchenRider(KitchenRider kitchenRider);

    public int updateKitchenRider(KitchenRider kitchenRider);

    public int deleteKitchenRiderById(Long id);

    public int deleteKitchenRiderByIds(Long[] ids);
}
