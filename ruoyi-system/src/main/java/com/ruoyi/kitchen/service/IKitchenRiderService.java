package com.ruoyi.kitchen.service;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenRider;

/**
 * 配送员Service接口
 *
 * @author ruoyi
 */
public interface IKitchenRiderService
{
    public KitchenRider selectKitchenRiderById(Long id);

    public List<KitchenRider> selectKitchenRiderList(KitchenRider kitchenRider);

    public int insertKitchenRider(KitchenRider kitchenRider);

    public int updateKitchenRider(KitchenRider kitchenRider);

    public int deleteKitchenRiderByIds(Long[] ids);

    public int deleteKitchenRiderById(Long id);
}
