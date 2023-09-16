package com.example;

import io.github.hanpijunbuhanpi.interceptor.annotation.ApiInterceptor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试Controller1
 *
 * @author hanpijun-buhanpi
 * @since 1.0
 * @date 2023/9/16 13:35
 */
@RestController
@RequestMapping("/test")
public class TestController1 {
    @GetMapping("/hello1")
    public String hello1() {
        return "hi1";
    }

    @GetMapping("/hello2")
    @ApiInterceptor(classes = Test1Interceptor.class)
    public String hello2() {
        return "hi2";
    }

    @GetMapping("/hello3")
    @CustomInterceptorAnnotation
    public String hello3() {
        return "hi3";
    }
}
