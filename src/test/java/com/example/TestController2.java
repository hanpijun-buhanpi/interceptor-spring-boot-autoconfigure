package com.example;

import io.github.hanpijunbuhanpi.interceptor.annotation.ApiInterceptor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试Controller2
 *
 * @author hanpijun-buhanpi
 * @since 1.0
 * @date 2023/9/16 13:35
 */
@RestController
@RequestMapping("/test")
@ApiInterceptor(classes = Test1Interceptor.class)
public class TestController2 {
    @GetMapping("/hello4")
    public String hello4() {
        return "hi4";
    }

    @GetMapping("/hello5")
    @ApiInterceptor(classes = Test1Interceptor.class)
    public String hello5() {
        return "hi5";
    }

    @GetMapping("/hello6")
    @CustomInterceptorAnnotation
    public String hello6() {
        return "hi6";
    }
}
