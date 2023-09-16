package io.github.hanpijunbuhanpi.interceptor.annotation;

import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.annotation.*;

/**
 * Api接口拦截注解
 *
 * @author hanpijun-buhanpi
 * @since 1.0
 * @date 2023/9/16 9:43
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ApiInterceptor {
    /**
     * 拦截器数组
     */
    Class<? extends HandlerInterceptor>[] classes();
}
