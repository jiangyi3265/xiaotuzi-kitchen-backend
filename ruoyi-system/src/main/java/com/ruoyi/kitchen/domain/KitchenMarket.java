package com.ruoyi.kitchen.domain;

import java.math.BigDecimal;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 菜市场及其备货群配置对象 kitchen_market
 */
public class KitchenMarket extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long shopId;
    private String marketName;
    private String marketAddress;
    private String businessHours;
    private String phone;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String stockGroupQr;
    private String stockGroupName;
    private String stockGroupNotice;
    private Integer orderNum;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public String getMarketName() { return marketName; }
    public void setMarketName(String marketName) { this.marketName = marketName; }
    public String getMarketAddress() { return marketAddress; }
    public void setMarketAddress(String marketAddress) { this.marketAddress = marketAddress; }
    public String getBusinessHours() { return businessHours; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public String getStockGroupQr() { return stockGroupQr; }
    public void setStockGroupQr(String stockGroupQr) { this.stockGroupQr = stockGroupQr; }
    public String getStockGroupName() { return stockGroupName; }
    public void setStockGroupName(String stockGroupName) { this.stockGroupName = stockGroupName; }
    public String getStockGroupNotice() { return stockGroupNotice; }
    public void setStockGroupNotice(String stockGroupNotice) { this.stockGroupNotice = stockGroupNotice; }
    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
