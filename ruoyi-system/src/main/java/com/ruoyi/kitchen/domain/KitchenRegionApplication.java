package com.ruoyi.kitchen.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class KitchenRegionApplication extends BaseEntity {
    private Long id;
    private Long wxUserId;
    private String applicantName;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String address;
    private String experience;
    private String auditStatus;
    private String enabled;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWxUserId() { return wxUserId; }
    public void setWxUserId(Long wxUserId) { this.wxUserId = wxUserId; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }
    public String getEnabled() { return enabled; }
    public void setEnabled(String enabled) { this.enabled = enabled; }
}
