package com.example;

import io.github.hanpijunbuhanpi.interceptor.annotation.ApiInterceptor;

import java.lang.annotation.*;

/**
 * 自定义拦截器注解
 *
 * @author hanpijun-buhanpi
 * @since 1.0
 * @date 2023/9/16 13:52
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiInterceptor(classes = Test1Interceptor.class)
public @interface CustomInterceptorAnnotation {
}
