# Interceptor Spring Boot Autoconfigure
## 介绍

基于SpringBoot的拦截器自动配置，提供两个注解，可基于请求路径和Mapping方法进行拦截。

## 使用

1、实现HandlerInterceptor，并使用@WebInterceptor注解声明拦截的路径，需要确保拦截器能被SpringBoot扫描到。

```java
@Slf4j
@WebInterceptor("/test/**")
public class TestInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.error("进入拦截器，请求路径: {}", request.getServletPath());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
```

2、实现HandlerInterceptor（比如上面那个），并在需要拦截的Mapping方法上使用注解声明，如果声明在类上，则整个Controller类的所有RequestMapping方法都会被拦截

```java
@RestController
@RequestMapping("/test")
public class TestController {
    @ApiInterceptor(classes = {TestInterceptor.class})
    @GetMapping({"/hello"})
    public String hello() {
        return "hi";
    }
}
```

> 注意：
>
> 两种方式的拦截器是互相独立的，如果使用@WebInterceptor拦截了某一接口后，又使用@ApiInterceptor拦截了某一个接口，这将导致该接口被拦截两次，这会造成重复执行和性能的降低。
>
> 推荐使用@WebInterceptor来进行拦截，它只是做了一个自动注册的功能，底层逻辑由SpringBoot负责，而@ApiInterceptor是拦截所有接口然后匹配请求路径，当存在大量接口后，这会造成性能的降低

## 测试

1、新建Controller

```java
@RestController
@RequestMapping("/test")
public class TestController1 {
    @GetMapping("/hello")
    public String hello1() {
        return "hi";
    }
}
```

2、使用单元测试进行测试

```java
@SpringBootTest
public class AppTest {
    private RestTemplate restTemplate = new RestTemplate();
    @Test
    public void test() {
        String body = restTemplate.getForEntity("http://localhost:8080/test/hello", String.class).getBody();
        Assert.isTrue("hello".equals(body), "测试失败");
    }
}
```

项目中提供了较为完整的测试用例，可供大家参考

## 修改

@ApiInterceptor是通过一个拦截器来调用注册的拦截器的，该拦截器是一个内部类（io.github.hanpijunbuhanpi.interceptor.config.ApiInterceptorConfig.ApiHandlerInterceptor），它是一个抽象类，里面只有一个抽象方法（getPathMatcher），默认实现是通过匿名类返回SpringBoot中默认的AntPathMatcher，如果你自己修改了PathMatcher，那么也需要重写该类。
