package io.github.hanpijunbuhanpi.interceptor.config;

import io.github.hanpijunbuhanpi.interceptor.annotation.WebInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 拦截器配置
 *
 * @author hanpijun-buhanpi
 * @since 1.0
 * @since 2023/9/16 8:32
 */
@Configuration
public class WebInterceptorConfig implements WebMvcConfigurer {
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(WebInterceptor.class);
        beans.forEach((k, v) -> {
            WebInterceptor annotation = AnnotatedElementUtils.findMergedAnnotation(v.getClass(), WebInterceptor.class);
            if (v instanceof HandlerInterceptor) {
                registry.addInterceptor((HandlerInterceptor) v).addPathPatterns(annotation != null ? annotation.pathPatterns() : new String[]{"/**"});
            }
        });
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
