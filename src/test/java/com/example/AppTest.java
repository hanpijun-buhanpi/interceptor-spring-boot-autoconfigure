package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * 测试类
 *
 * @author hanpijun-buhanpi
 * @since 1.0
 * @date 2023/9/16 13:37
 */
@SpringBootTest
public class AppTest {
    private final RestTemplate restTemplate = new RestTemplate();
    @Test
    public void test() {
        String body1 = restTemplate.getForEntity("http://localhost:8080/test/hello1", String.class).getBody();
        Assert.isTrue("hello".equals(body1), "@WebInterceptor测试失败");
        String body2 = restTemplate.getForEntity("http://localhost:8080/test/hello2", String.class).getBody();
        Assert.isTrue("hello".equals(body2), "@ApiInterceptor方法测试失败");
        String body3 = restTemplate.getForEntity("http://localhost:8080/test/hello3", String.class).getBody();
        Assert.isTrue("hello".equals(body3), "自定义注解继承@ApiInterceptor测试失败");

        String body4 = restTemplate.getForEntity("http://localhost:8080/test/hello4", String.class).getBody();
        Assert.isTrue("hello".equals(body4), "@ApiInterceptor类测试失败");
        String body5 = restTemplate.getForEntity("http://localhost:8080/test/hello5", String.class).getBody();
        Assert.isTrue("hello".equals(body5), "@ApiInterceptor类测试失败");
        String body6 = restTemplate.getForEntity("http://localhost:8080/test/hello6", String.class).getBody();
        Assert.isTrue("hello".equals(body6), "@ApiInterceptor类测试失败");

        String body7 = restTemplate.getForEntity("http://localhost:8080/test/hello7", String.class).getBody();
        Assert.isTrue("hellohellohi7".equals(body7), "@ApiInterceptor列表测试失败");
    }
}
