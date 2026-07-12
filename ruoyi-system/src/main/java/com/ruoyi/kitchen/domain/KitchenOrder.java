package com.ruoyi.kitchen.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 订单对象 kitchen_order
 *
 * @author ruoyi
 */
public class KitchenOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long id;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 小程序用户ID */
    private Long wxUserId;

    /** 情侣空间ID（异地投喂） */
    private Long coupleSpaceId;

    /** 收餐用户ID（异地投喂） */
    private Long recipientWxUserId;

    /** 前端提交标记，非DB字段 */
    private String remoteFeed;

    /** 用户昵称（非DB字段，列表展示） */
    @Excel(name = "下单用户")
    private String userNickname;

    /** 服务方式(0同城配送 1厨师代炒 2店内自提) */
    @Excel(name = "服务方式", readConverterExp = "0=同城配送,1=厨师代炒,2=店内自提")
    private String serviceType;

    /** 厨师ID(代炒) */
    private Long chefId;

    /** 厨师名称（非DB字段） */
    @Excel(name = "厨师")
    private String chefName;

    /** 配送员ID(同城配送) */
    private Long riderId;

    /** 配送员名称（非DB字段） */
    @Excel(name = "配送员")
    private String riderName;

    /** 收货人 */
    @Excel(name = "收货人")
    private String receiverName;

    /** 收货电话 */
    @Excel(name = "收货电话")
    private String receiverPhone;

    /** 收货地址 */
    @Excel(name = "收货地址")
    private String receiverAddress;

    /** 商品总件数 */
    @Excel(name = "总件数")
    private Integer totalCount;

    /** 订单金额 */
    @Excel(name = "订单金额")
    private BigDecimal totalAmount;

    /** 订单状态(0待处理 1已接单 2制作中 3已完成 4已取消 5申请退款) */
    @Excel(name = "订单状态", readConverterExp = "0=待处理,1=已接单,2=制作中,3=已完成,4=已取消,5=申请退款")
    private String orderStatus;

    /** 支付状态(0未支付 1已支付) */
    @Excel(name = "支付状态", readConverterExp = "0=未支付,1=已支付")
    private String payStatus;

    /** 是否分享成品(0否 1是) */
    private String shareFlag;

    /** 订单明细（非DB字段） */
    private List<KitchenOrderItem> items = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Long getWxUserId() { return wxUserId; }
    public void setWxUserId(Long wxUserId) { this.wxUserId = wxUserId; }

    public Long getCoupleSpaceId() { return coupleSpaceId; }
    public void setCoupleSpaceId(Long coupleSpaceId) { this.coupleSpaceId = coupleSpaceId; }
    public Long getRecipientWxUserId() { return recipientWxUserId; }
    public void setRecipientWxUserId(Long recipientWxUserId) { this.recipientWxUserId = recipientWxUserId; }
    public String getRemoteFeed() { return remoteFeed; }
    public void setRemoteFeed(String remoteFeed) { this.remoteFeed = remoteFeed; }

    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public Long getChefId() { return chefId; }
    public void setChefId(Long chefId) { this.chefId = chefId; }

    public String getChefName() { return chefName; }
    public void setChefName(String chefName) { this.chefName = chefName; }

    public Long getRiderId() { return riderId; }
    public void setRiderId(Long riderId) { this.riderId = riderId; }

    public String getRiderName() { return riderName; }
    public void setRiderName(String riderName) { this.riderName = riderName; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

    public String getReceiverAddress() { return receiverAddress; }
    public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getPayStatus() { return payStatus; }
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }

    public String getShareFlag() { return shareFlag; }
    public void setShareFlag(String shareFlag) { this.shareFlag = shareFlag; }

    public List<KitchenOrderItem> getItems() { return items; }
    public void setItems(List<KitchenOrderItem> items) { this.items = items; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("orderNo", getOrderNo())
                .append("serviceType", getServiceType())
                .append("orderStatus", getOrderStatus())
                .append("totalAmount", getTotalAmount())
                .toString();
    }
}
