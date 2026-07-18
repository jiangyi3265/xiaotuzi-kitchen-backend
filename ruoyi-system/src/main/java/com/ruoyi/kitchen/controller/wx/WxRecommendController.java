package com.ruoyi.kitchen.controller.wx;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.kitchen.domain.KitchenDish;
import com.ruoyi.kitchen.domain.KitchenWxUser;
import com.ruoyi.kitchen.mapper.KitchenWxUserMapper;
import com.ruoyi.kitchen.service.IKitchenDishService;
import com.ruoyi.kitchen.util.WxTokenService;

/**
 * 智能菜谱推荐Controller（微信小程序端，需登录）
 * 真实实现：基于本店在架菜品，按用户需求文本做关键词匹配打分推荐，并扣减胡萝卜积分。
 * 不依赖外部 AI，推荐结果始终来自真实菜单。
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/wx/recommend")
public class WxRecommendController
{
    @Autowired
    private IKitchenDishService kitchenDishService;

    @Autowired
    private KitchenWxUserMapper wxUserMapper;

    @Autowired
    private WxTokenService wxTokenService;

    /** 每次推荐消耗的胡萝卜积分 */
    @Value("${wx.recommendCost:30}")
    private int recommendCost;

    /**
     * 生成推荐：根据 desc 从在架菜品中匹配一道最合适的，扣积分后返回菜品详情与剩余积分。
     * 请求体：{ desc }
     */
    @Anonymous
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult recommend(@RequestBody JSONObject body, HttpServletRequest request)
    {
        Long userId = wxTokenService.getRequiredUserId(request);
        String desc = body != null ? body.getString("desc") : null;
        desc = desc == null ? "" : desc.trim();
        if (desc.length() > 500)
        {
            return AjaxResult.error("推荐需求不能超过500字");
        }
        if (recommendCost <= 0)
        {
            throw new ServiceException("推荐积分配置异常");
        }

        // 候选：仅在架菜品
        KitchenDish query = new KitchenDish();
        query.setStatus("1");
        List<KitchenDish> candidates = kitchenDishService.selectKitchenDishList(query);
        if (candidates == null || candidates.isEmpty())
        {
            return AjaxResult.error("厨房暂无可推荐的菜品");
        }

        // 先扣积分（原子，余额不足直接失败，不进行推荐）
        if (wxUserMapper.deductCarrot(userId, recommendCost) <= 0)
        {
            return AjaxResult.error("胡萝卜不足，无法生成推荐");
        }

        // 关键词匹配打分：desc 的 2-gram 命中菜品(名称/描述/分类)的数量
        KitchenDish best = pickBest(candidates, desc);
        // 返回完整详情（含规格、步骤）
        KitchenDish detail = kitchenDishService.selectKitchenDishById(best.getId());
        if (detail == null || !"1".equals(detail.getStatus()))
        {
            throw new ServiceException("推荐菜谱已失效，请重试");
        }
        if (!"1".equals(detail.getRecipeOpen()))
        {
            detail.setSteps(null);
            detail.setCookingExp(null);
        }

        KitchenWxUser user = wxUserMapper.selectKitchenWxUserById(userId);
        if (user == null)
        {
            throw new ServiceException("用户状态异常，本次推荐已取消");
        }
        AjaxResult ajax = AjaxResult.success(detail);
        ajax.put("carrot", user != null ? user.getCarrot() : 0);
        ajax.put("cost", recommendCost);
        return ajax;
    }

    /**
     * 选出与需求文本最匹配的菜品；无有效关键词或全部 0 分时，回退为销量最高者。
     */
    private KitchenDish pickBest(List<KitchenDish> dishes, String desc)
    {
        String d = desc == null ? "" : desc.trim();
        KitchenDish best = null;
        int bestScore = -1;
        for (KitchenDish dish : dishes)
        {
            String text = StringUtils.nvl(dish.getDishName(), "") + " "
                    + StringUtils.nvl(dish.getStory(), "") + " "
                    + StringUtils.nvl(dish.getCategoryName(), "") + " "
                    + StringUtils.nvl(dish.getDifficulty(), "");
            int score = matchScore(d, text);
            if (score > bestScore)
            {
                bestScore = score;
                best = dish;
            }
        }
        if (best == null || bestScore <= 0)
        {
            // 无关键词命中：推荐最受欢迎（销量最高）的一道
            best = dishes.get(0);
            int maxSales = -1;
            for (KitchenDish dish : dishes)
            {
                int s = dish.getSales() == null ? 0 : dish.getSales();
                if (s > maxSales)
                {
                    maxSales = s;
                    best = dish;
                }
            }
        }
        return best;
    }

    /** desc 的相邻 2 字组合在 text 中出现的个数 */
    private int matchScore(String desc, String text)
    {
        if (desc.length() < 2)
        {
            return 0;
        }
        int score = 0;
        for (int i = 0; i + 2 <= desc.length(); i++)
        {
            String gram = desc.substring(i, i + 2);
            if (gram.trim().length() == 2 && text.contains(gram))
            {
                score++;
            }
        }
        return score;
    }
}
