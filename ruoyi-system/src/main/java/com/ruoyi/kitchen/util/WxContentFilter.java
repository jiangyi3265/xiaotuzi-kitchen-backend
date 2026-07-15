package com.ruoyi.kitchen.util;

import java.util.Locale;

/**
 * 小程序用户发布内容的首层关键词过滤。
 *
 * 该过滤用于在写库前拒绝明显违法违规内容；通过后的内容在生产环境仍进入人工审核，
 * 图片也由后台审核后才会公开展示。
 */
public final class WxContentFilter
{
    private static final String[] BLOCKED_TERMS = {
        "色情", "成人视频", "色情交易", "裸聊", "约炮", "卖淫", "嫖娼",
        "赌博", "博彩", "下注", "赌场", "六合彩", "赌球",
        "毒品", "冰毒", "海洛因", "摇头丸", "枪支", "买枪", "卖枪",
        "办假证", "假币", "洗钱", "刷单返利", "裸贷"
    };

    private WxContentFilter()
    {
    }

    public static boolean containsBlockedContent(String... values)
    {
        if (values == null)
        {
            return false;
        }
        StringBuilder text = new StringBuilder();
        for (String value : values)
        {
            if (value != null)
            {
                text.append(value);
            }
        }
        String normalized = text.toString()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[\\s\\p{Punct}，。！？、；：‘’“”（）【】《》]+", "");
        for (String term : BLOCKED_TERMS)
        {
            if (normalized.contains(term))
            {
                return true;
            }
        }
        return false;
    }
}
