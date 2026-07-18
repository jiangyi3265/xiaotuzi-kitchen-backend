package com.ruoyi.kitchen.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
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
        if (kitchenWxUser == null || kitchenWxUser.getId() == null)
        {
            throw new ServiceException("缺少用户ID");
        }
        if (kitchenWxUser.getIsOwner() != null
                && !"0".equals(kitchenWxUser.getIsOwner())
                && !"1".equals(kitchenWxUser.getIsOwner()))
        {
            throw new ServiceException("店主状态只能为0或1");
        }
        if (kitchenWxUser.getStatus() != null
                && !"0".equals(kitchenWxUser.getStatus())
                && !"1".equals(kitchenWxUser.getStatus()))
        {
            throw new ServiceException("用户状态只能为0或1");
        }
        if (kitchenWxUser.getCarrot() != null && kitchenWxUser.getCarrot() < 0)
        {
            throw new ServiceException("积分不能小于0");
        }
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

        // 后台删除采用逻辑删除，openid 仍受唯一索引保护。用户再次登录时恢复原 ID，
        // 这样历史订单、分享等关系不会断开；恢复时 Mapper 会撤销店主权限。
        user = restoreDeletedUser(openid);
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
        try
        {
            kitchenWxUserMapper.insertKitchenWxUser(user);
            // 回读数据库默认字段（如 is_owner、create_time），保证首登响应与持久化状态一致。
            KitchenWxUser created = kitchenWxUserMapper.selectKitchenWxUserById(user.getId());
            return created != null ? created : user;
        }
        catch (DuplicateKeyException e)
        {
            // 同一微信用户并发发起登录时，另一请求可能已经完成插入或恢复。
            KitchenWxUser concurrent = kitchenWxUserMapper.selectKitchenWxUserByOpenid(openid);
            if (concurrent != null)
            {
                return concurrent;
            }
            concurrent = restoreDeletedUser(openid);
            if (concurrent != null)
            {
                return concurrent;
            }
            throw e;
        }
    }

    private KitchenWxUser restoreDeletedUser(String openid)
    {
        // 不把“查询 + 恢复 + 新增”包在同一个 REPEATABLE READ 事务中：并发登录时
        // 普通查询快照可能看不到另一请求刚恢复/新增的记录。这里直接按唯一 openid
        // 原子恢复；每个 Mapper 调用独立提交，后续查询可以看到并发请求的结果。
        if (kitchenWxUserMapper.restoreKitchenWxUserByOpenid(openid) <= 0)
        {
            return null;
        }
        return kitchenWxUserMapper.selectKitchenWxUserByOpenid(openid);
    }
}
