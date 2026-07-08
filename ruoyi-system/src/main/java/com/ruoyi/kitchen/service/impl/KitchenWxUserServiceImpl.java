package com.ruoyi.kitchen.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenWxUser;
import com.ruoyi.kitchen.mapper.KitchenWxUserMapper;
import com.ruoyi.kitchen.service.IKitchenWxUserService;

/**
 * 小程序用户Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class KitchenWxUserServiceImpl implements IKitchenWxUserService
{
    @Autowired
    private KitchenWxUserMapper kitchenWxUserMapper;

    @Override
    public KitchenWxUser selectKitchenWxUserById(Long id)
    {
        return kitchenWxUserMapper.selectKitchenWxUserById(id);
    }

    @Override
    public KitchenWxUser selectKitchenWxUserByOpenid(String openid)
    {
        return kitchenWxUserMapper.selectKitchenWxUserByOpenid(openid);
    }

    @Override
    public List<KitchenWxUser> selectKitchenWxUserList(KitchenWxUser kitchenWxUser)
    {
        return kitchenWxUserMapper.selectKitchenWxUserList(kitchenWxUser);
    }

    @Override
    public int insertKitchenWxUser(KitchenWxUser kitchenWxUser)
    {
        return kitchenWxUserMapper.insertKitchenWxUser(kitchenWxUser);
    }

    @Override
    public int updateKitchenWxUser(KitchenWxUser kitchenWxUser)
    {
        return kitchenWxUserMapper.updateKitchenWxUser(kitchenWxUser);
    }

    @Override
    public int deleteKitchenWxUserByIds(Long[] ids)
    {
        return kitchenWxUserMapper.deleteKitchenWxUserByIds(ids);
    }

    @Override
    public int deleteKitchenWxUserById(Long id)
    {
        return kitchenWxUserMapper.deleteKitchenWxUserById(id);
    }

    @Override
    public KitchenWxUser registerOrGet(String openid, String unionId)
    {
        KitchenWxUser user = kitchenWxUserMapper.selectKitchenWxUserByOpenid(openid);
        if (user != null)
        {
            return user;
        }
        user = new KitchenWxUser();
        user.setOpenid(openid);
        user.setUnionId(StringUtils.isNotBlank(unionId) ? unionId : "");
        // 默认昵称与用户编码
        String code = openid.length() > 6 ? openid.substring(openid.length() - 6) : openid;
        user.setNickname("御厨" + code);
        user.setUserCode(code);
        user.setGender("0");
        user.setCarrot(0);
        user.setStatus("0");
        kitchenWxUserMapper.insertKitchenWxUser(user);
        return user;
    }
}
