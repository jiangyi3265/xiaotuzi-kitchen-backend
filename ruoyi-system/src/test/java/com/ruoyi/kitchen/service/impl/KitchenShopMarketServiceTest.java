package com.ruoyi.kitchen.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.kitchen.domain.KitchenMarket;
import com.ruoyi.kitchen.domain.KitchenShop;
import com.ruoyi.kitchen.mapper.KitchenMarketMapper;
import com.ruoyi.kitchen.mapper.KitchenShopMapper;

@ExtendWith(MockitoExtension.class)
class KitchenShopMarketServiceTest
{
    @Mock
    private KitchenShopMapper shopMapper;

    @Mock
    private KitchenMarketMapper marketMapper;

    @InjectMocks
    private KitchenShopServiceImpl service;

    @Test
    void publicShopOnlyReturnsEnabledMarkets()
    {
        KitchenShop shop = new KitchenShop();
        shop.setId(1L);
        KitchenMarket market = market("迎龙市场", "右江区迎龙路", 0);
        when(shopMapper.selectKitchenShop()).thenReturn(shop);
        when(marketMapper.selectByShopId(1L, "0")).thenReturn(Collections.singletonList(market));

        KitchenShop result = service.getPublicShop();

        assertEquals(1, result.getMarkets().size());
        assertEquals("迎龙市场", result.getMarkets().get(0).getMarketName());
        verify(marketMapper).selectByShopId(1L, "0");
    }

    @Test
    void savesMultipleMarketsAndKeepsFirstEnabledMarketForOldMiniVersions()
    {
        KitchenShop existing = new KitchenShop();
        existing.setId(7L);
        KitchenShop input = new KitchenShop();
        KitchenMarket first = market("迎龙市场", "迎龙路1号", 0);
        first.setStockGroupName("迎龙市场备货群");
        KitchenMarket second = market("东合市场", "东合一路8号", 1);
        input.setMarkets(Arrays.asList(first, second));

        when(shopMapper.selectKitchenShop()).thenReturn(existing);
        when(shopMapper.updateKitchenShop(input)).thenReturn(1);
        when(marketMapper.insertKitchenMarket(any(KitchenMarket.class))).thenReturn(1);

        assertEquals(1, service.saveShop(input));
        assertEquals(7L, input.getId());
        assertEquals("迎龙市场", input.getStoreName());
        assertEquals("迎龙路1号", input.getStoreAddress());
        assertEquals("迎龙市场备货群", input.getStockGroupName());
        verify(marketMapper).deleteByShopId(7L);
        verify(marketMapper, times(2)).insertKitchenMarket(any(KitchenMarket.class));
        ArgumentCaptor<KitchenMarket> captor = ArgumentCaptor.forClass(KitchenMarket.class);
        verify(marketMapper, times(2)).insertKitchenMarket(captor.capture());
        assertEquals(7L, captor.getAllValues().get(0).getShopId());
        assertEquals(7L, captor.getAllValues().get(1).getShopId());
    }

    @Test
    void rejectsIncompleteCoordinatesBeforeChangingStoredData()
    {
        KitchenShop input = new KitchenShop();
        KitchenMarket market = market("迎龙市场", "迎龙路1号", 0);
        market.setLatitude(new BigDecimal("23.9000000"));
        input.setMarkets(Collections.singletonList(market));

        ServiceException error = assertThrows(ServiceException.class, () -> service.saveShop(input));

        assertEquals("菜市场经纬度需要同时填写", error.getMessage());
    }

    private KitchenMarket market(String name, String address, int order)
    {
        KitchenMarket market = new KitchenMarket();
        market.setMarketName(name);
        market.setMarketAddress(address);
        market.setOrderNum(order);
        market.setStatus("0");
        return market;
    }
}
