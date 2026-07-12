package com.ruoyi.kitchen.mapper;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenRegionApplication;

public interface KitchenRegionApplicationMapper {
    List<KitchenRegionApplication> selectList(KitchenRegionApplication query);
    KitchenRegionApplication selectById(Long id);
    KitchenRegionApplication selectLatestByUser(Long wxUserId);
    int countEnabledRegion(KitchenRegionApplication query);
    int insert(KitchenRegionApplication application);
    int audit(KitchenRegionApplication application);
    int updateEnabled(KitchenRegionApplication application);
}
