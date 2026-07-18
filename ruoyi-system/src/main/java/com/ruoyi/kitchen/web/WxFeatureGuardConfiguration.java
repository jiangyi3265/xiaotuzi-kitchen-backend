package com.ruoyi.kitchen.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Registers the mini-program feature guard only for mini-program routes. */
@Configuration
public class WxFeatureGuardConfiguration implements WebMvcConfigurer
{
    private final WxFeatureGuardInterceptor featureGuardInterceptor;

    @Autowired
    public WxFeatureGuardConfiguration(WxFeatureGuardInterceptor featureGuardInterceptor)
    {
        this.featureGuardInterceptor = featureGuardInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(featureGuardInterceptor).addPathPatterns("/api/wx/**");
    }
}
