package com.ruoyi.kitchen.service;

import java.util.List;
import com.ruoyi.kitchen.domain.KitchenWxUser;

/**
 * 小程序用户Service接口
 *
 * @author ruoyi
 */
public interface IKitchenWxUserService
{
    public KitchenWxUser selectKitchenWxUserById(Long id);

    public KitchenWxUser selectKitchenWxUserByOpenid(String openid);

    public List<KitchenWxUser> selectKitchenWxUserList(KitchenWxUser kitchenWxUser);

    public int insertKitchenWxUser(KitchenWxUser kitchenWxUser);

    public int updateKitchenWxUser(KitchenWxUser kitchenWxUser);

    public int deleteKitchenWxUserByIds(Long[] ids);

    public int deleteKitchenWxUserById(Long id);

    /**
     * 根据 openid 获取用户，不存在则自动注册
     */
    public KitchenWxUser registerOrGet(String openid, String unionId);
}
