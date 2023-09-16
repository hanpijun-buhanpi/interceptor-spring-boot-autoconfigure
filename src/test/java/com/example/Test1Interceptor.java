package com.example;

import io.github.hanpijunbuhanpi.interceptor.annotation.WebInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * 测试拦截器1
 *
 * @author hanpijun-buhanpi
 * @since 1.0
 * @date 2023/9/16 13:34
 */
@WebInterceptor("/test/hello1")
public class Test1Interceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.write("hello".getBytes(StandardCharsets.UTF_8));
        out.flush();
        return false;
    }
}
