package io.github.hanpijunbuhanpi.interceptor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 拦截器自动配置
 *
 * @author hanpijun-buhanpi
 * @since 1.0
 * @date 2023/9/16 11:29
 */
@Configuration
@Import({WebInterceptorConfig.class, ApiInterceptorConfig.class})
public class InterceptorAutoConfigure {
}
