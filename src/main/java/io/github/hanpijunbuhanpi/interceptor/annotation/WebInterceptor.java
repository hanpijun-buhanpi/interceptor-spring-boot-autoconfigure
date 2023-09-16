package io.github.hanpijunbuhanpi.interceptor.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 拦截器注解
 *
 * @author hanpijun-buhanpi
 * @since 1.0
 * @date 2023/9/16 8:43
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Inherited
public @interface WebInterceptor {
    /**
     * 拦截地址
     */
    @AliasFor("pathPatterns")
    String[] value() default {"/**"};
    /**
     * 拦截地址
     */
    @AliasFor("value")
    String[] pathPatterns() default {"/**"};
}
