package com.ruoyi.kitchen.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 小程序功能总开关管理接口。
 *
 * 该接口只允许读写固定配置键，避免运营角色为了操作一个开关而获得全部系统参数权限。
 */
@RestController
@RequestMapping("/system/applet/switch")
public class KitchenAppletSwitchController extends BaseController
{
    static final String CONFIG_KEY = "wx.feature.enabled";

    private final ISysConfigService configService;

    @Autowired
    public KitchenAppletSwitchController(ISysConfigService configService)
    {
        this.configService = configService;
    }

    @PreAuthorize("@ss.hasPermi('applet:switch:list')")
    @GetMapping
    public AjaxResult getSwitch()
    {
        SysConfig config = selectFixedConfig();
        Map<String, Object> data = new HashMap<>();
        data.put("configured", config != null);
        data.put("enabled", config != null && "true".equalsIgnoreCase(config.getConfigValue()));
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('applet:switch:edit')")
    @Log(title = "小程序功能开关", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult updateSwitch(@RequestBody(required = false) JsonNode body)
    {
        if (body == null || !body.isObject() || body.size() != 1
                || !body.has("enabled") || !body.get("enabled").isBoolean())
        {
            return AjaxResult.error("请求参数仅允许 enabled 布尔值");
        }

        SysConfig existing = selectFixedConfig();
        if (existing == null || existing.getConfigId() == null)
        {
            return AjaxResult.error("未找到配置项 " + CONFIG_KEY + "，请先执行数据库补丁");
        }

        SysConfig update = new SysConfig();
        update.setConfigId(existing.getConfigId());
        update.setConfigKey(CONFIG_KEY);
        update.setConfigValue(Boolean.toString(body.get("enabled").booleanValue()));
        update.setUpdateBy(getUsername());
        if (configService.updateConfig(update) <= 0)
        {
            return AjaxResult.error("开关保存失败，请刷新后重试");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("configured", true);
        data.put("enabled", body.get("enabled").booleanValue());
        return AjaxResult.success("开关已保存", data);
    }

    private SysConfig selectFixedConfig()
    {
        SysConfig query = new SysConfig();
        query.setConfigKey(CONFIG_KEY);
        List<SysConfig> configs = configService.selectConfigList(query);
        if (configs == null)
        {
            return null;
        }
        for (SysConfig config : configs)
        {
            if (config != null && StringUtils.equals(CONFIG_KEY, config.getConfigKey()))
            {
                return config;
            }
        }
        return null;
    }
}
