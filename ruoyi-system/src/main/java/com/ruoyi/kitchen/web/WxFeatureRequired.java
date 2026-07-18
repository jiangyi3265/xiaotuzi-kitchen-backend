package com.ruoyi.kitchen.web;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a mini-program endpoint that may create new business data only while
 * the global feature switch is enabled.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface WxFeatureRequired
{
    /** Guard only state-changing HTTP methods when placed on a controller. */
    boolean writeMethodsOnly() default false;

    /** Exact servlet paths that remain available while the switch is off. */
    String[] excludedPaths() default {};
}
