package com.example;

import io.github.hanpijunbuhanpi.interceptor.annotation.ApiInterceptor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试Controller3
 *
 * @author hanpijun-buhanpi
 * @since 1.0
 * @date 2023/9/16 13:35
 */
@RestController
@RequestMapping("/test")
public class TestController3 {
    @GetMapping("/hello7")
    @ApiInterceptor(classes = {Test2Interceptor.class, Test3Interceptor.class})
    public String hello7() {
        return "hi7";
    }
}
