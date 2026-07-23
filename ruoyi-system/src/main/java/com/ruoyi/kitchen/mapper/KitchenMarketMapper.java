package com.ruoyi.kitchen.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.kitchen.domain.KitchenMarket;

public interface KitchenMarketMapper
{
    List<KitchenMarket> selectByShopId(@Param("shopId") Long shopId, @Param("status") String status);

    int insertKitchenMarket(KitchenMarket market);

    int deleteByShopId(Long shopId);
}
