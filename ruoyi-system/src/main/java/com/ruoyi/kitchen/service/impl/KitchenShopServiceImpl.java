package com.ruoyi.kitchen.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenMarket;
import com.ruoyi.kitchen.domain.KitchenShop;
import com.ruoyi.kitchen.mapper.KitchenMarketMapper;
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

    @Autowired
    private KitchenMarketMapper kitchenMarketMapper;

    @Override
    public KitchenShop getShop()
    {
        KitchenShop shop = kitchenShopMapper.selectKitchenShop();
        if (shop == null)
        {
            return new KitchenShop();
        }
        shop.setMarkets(kitchenMarketMapper.selectByShopId(shop.getId(), null));
        return shop;
    }

    @Override
    public KitchenShop getPublicShop()
    {
        KitchenShop shop = kitchenShopMapper.selectKitchenShop();
        if (shop == null)
        {
            return new KitchenShop();
        }
        shop.setMarkets(kitchenMarketMapper.selectByShopId(shop.getId(), "0"));
        return shop;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveShop(KitchenShop kitchenShop)
    {
        List<KitchenMarket> markets = kitchenShop.getMarkets();
        if (markets != null)
        {
            markets = validateAndNormalizeMarkets(markets);
            kitchenShop.setMarkets(markets);
            syncLegacyMarket(kitchenShop, markets);
        }
        KitchenShop exist = kitchenShopMapper.selectKitchenShop();
        int rows;
        if (exist == null)
        {
            rows = kitchenShopMapper.insertKitchenShop(kitchenShop);
        }
        else
        {
            kitchenShop.setId(exist.getId());
            rows = kitchenShopMapper.updateKitchenShop(kitchenShop);
        }
        if (rows <= 0)
        {
            throw new ServiceException("厨房设置保存失败");
        }
        if (markets != null)
        {
            kitchenMarketMapper.deleteByShopId(kitchenShop.getId());
            for (KitchenMarket market : markets)
            {
                market.setShopId(kitchenShop.getId());
                if (kitchenMarketMapper.insertKitchenMarket(market) <= 0)
                {
                    throw new ServiceException("菜市场配置保存失败");
                }
            }
        }
        return rows;
    }

    private List<KitchenMarket> validateAndNormalizeMarkets(List<KitchenMarket> source)
    {
        if (source.size() > 100)
        {
            throw new ServiceException("菜市场最多配置100个");
        }
        List<KitchenMarket> markets = new ArrayList<>();
        int index = 0;
        for (KitchenMarket market : source)
        {
            if (market == null)
            {
                continue;
            }
            market.setMarketName(trim(market.getMarketName()));
            market.setMarketAddress(trim(market.getMarketAddress()));
            market.setBusinessHours(trim(market.getBusinessHours()));
            market.setPhone(trim(market.getPhone()));
            market.setStockGroupQr(trim(market.getStockGroupQr()));
            market.setStockGroupName(trim(market.getStockGroupName()));
            market.setStockGroupNotice(trim(market.getStockGroupNotice()));
            if (StringUtils.isBlank(market.getMarketName()) || StringUtils.isBlank(market.getMarketAddress()))
            {
                throw new ServiceException("请完整填写每个菜市场的名称和地址");
            }
            if (tooLong(market.getMarketName(), 100) || tooLong(market.getMarketAddress(), 255)
                    || tooLong(market.getBusinessHours(), 100) || tooLong(market.getPhone(), 30)
                    || tooLong(market.getStockGroupQr(), 255) || tooLong(market.getStockGroupName(), 100)
                    || tooLong(market.getStockGroupNotice(), 500))
            {
                throw new ServiceException("菜市场配置内容过长，请精简后保存");
            }
            validateCoordinate(market.getLatitude(), new BigDecimal("-90"), new BigDecimal("90"), "纬度");
            validateCoordinate(market.getLongitude(), new BigDecimal("-180"), new BigDecimal("180"), "经度");
            if ((market.getLatitude() == null) != (market.getLongitude() == null))
            {
                throw new ServiceException("菜市场经纬度需要同时填写");
            }
            market.setOrderNum(market.getOrderNum() == null ? index : Math.max(0, market.getOrderNum()));
            market.setStatus("1".equals(market.getStatus()) ? "1" : "0");
            markets.add(market);
            index++;
        }
        return markets;
    }

    private void validateCoordinate(BigDecimal value, BigDecimal min, BigDecimal max, String label)
    {
        if (value != null && (value.compareTo(min) < 0 || value.compareTo(max) > 0))
        {
            throw new ServiceException(label + "超出有效范围");
        }
    }

    private void syncLegacyMarket(KitchenShop shop, List<KitchenMarket> markets)
    {
        KitchenMarket first = null;
        for (KitchenMarket market : markets)
        {
            if ("0".equals(market.getStatus()))
            {
                first = market;
                break;
            }
        }
        if (first == null && !markets.isEmpty())
        {
            first = markets.get(0);
        }
        shop.setStoreName(first == null ? "" : first.getMarketName());
        shop.setStoreAddress(first == null ? "" : first.getMarketAddress());
        shop.setBusinessHours(first == null ? "" : first.getBusinessHours());
        shop.setStorePhone(first == null ? "" : first.getPhone());
        shop.setStockGroupQr(first == null ? "" : first.getStockGroupQr());
        shop.setStockGroupName(first == null ? "" : first.getStockGroupName());
        shop.setStockGroupNotice(first == null ? "" : first.getStockGroupNotice());
    }

    private String trim(String value)
    {
        return value == null ? "" : value.trim();
    }

    private boolean tooLong(String value, int max)
    {
        return value != null && value.length() > max;
    }
}
