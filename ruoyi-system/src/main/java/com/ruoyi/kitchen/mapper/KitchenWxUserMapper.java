package com.ruoyi.kitchen.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.kitchen.domain.KitchenWxUser;

/**
 * 小程序用户Mapper接口
 *
 * @author ruoyi
 */
public interface KitchenWxUserMapper
{
    public KitchenWxUser selectKitchenWxUserById(Long id);

    public KitchenWxUser selectKitchenWxUserByOpenid(String openid);

    public List<KitchenWxUser> selectKitchenWxUserList(KitchenWxUser kitchenWxUser);

    public int insertKitchenWxUser(KitchenWxUser kitchenWxUser);

    public int updateKitchenWxUser(KitchenWxUser kitchenWxUser);

    public int deleteKitchenWxUserById(Long id);

    public int deleteKitchenWxUserByIds(Long[] ids);

    /**
     * 恢复逻辑删除用户。恢复时撤销店主权限，避免被删除的高权限账号重新登录后自动取回管理权。
     */
    public int restoreKitchenWxUserByOpenid(String openid);

    /** 原子扣减胡萝卜积分（余额不足返回 0，不会扣成负数） */
    public int deductCarrot(@Param("id") Long id, @Param("amount") Integer amount);
}
