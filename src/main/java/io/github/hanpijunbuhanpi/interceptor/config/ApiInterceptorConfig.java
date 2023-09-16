package io.github.hanpijunbuhanpi.interceptor.config;

import io.github.hanpijunbuhanpi.interceptor.annotation.ApiInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Api拦截器配置
 *
 * @author hanpijun-buhanpi
 * @since 1.0
 * @date 2023/9/16 9:46
 */
@Configuration
public class ApiInterceptorConfig implements WebMvcConfigurer {
    @Resource
    private ApplicationContext applicationContext;
    @Lazy
    @Resource
    private ApiHandlerInterceptor apiHandlerInterceptor;
    private final Map<Class<? extends HandlerInterceptor>, HandlerInterceptor> interceptorMap = new HashMap<>(16);
    private final Map<String, List<HandlerInterceptor>> interceptorMappingMap = new HashMap<>(16);

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Map<String, Object> controllerBeans = applicationContext.getBeansWithAnnotation(Controller.class);
        controllerBeans.forEach((k, v) -> {
            // 获取需要拦截的方法
            List<Method> interceptorMethods = getInterceptorMethods(v.getClass());
            // 遍历需要拦截的方法
            for (Method method : interceptorMethods) {
                // 获取映射路径
                List<String> mappingPaths = getMappingPaths(method);
                // 注册拦截器
                for (String path : mappingPaths) {
                    List<HandlerInterceptor> handlerInterceptors = interceptorMappingMap.getOrDefault(path, new ArrayList<>());
                    handlerInterceptors.addAll(getHandlerInterceptors(method));
                    interceptorMappingMap.put(path, handlerInterceptors);
                }
            }
        });
        // 拦截器排序
        interceptorMappingMap.forEach((k, v) -> v.sort((i1, i2) -> getOrder(i1) - getOrder(i2)));
        registry.addInterceptor(apiHandlerInterceptor).addPathPatterns("/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    /**
     * 获取需要拦截的方法列表
     *
     * @param clazz 类
     * @return 需要拦截的方法列表
     */
    private List<Method> getInterceptorMethods(Class<?> clazz) {
        // 判断是否为Controller
        Controller controller = AnnotatedElementUtils.findMergedAnnotation(clazz, Controller.class);
        if (controller == null) {
            throw new RuntimeException(clazz.getName() + " 不是Controller类");
        }
        // 获取所有RequestMapping方法
        List<Method> list = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> {
                    RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
                    return requestMapping != null;
                })
                .collect(Collectors.toList());
        // 过滤不需要拦截的方法
        ApiInterceptor apiInterceptor = AnnotatedElementUtils.findMergedAnnotation(clazz, ApiInterceptor.class);
        if (apiInterceptor != null) {
            return list;
        } else {
            return list.stream()
                    .filter(method -> AnnotatedElementUtils.findMergedAnnotation(method, ApiInterceptor.class) != null)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 获取RequestMapping方法的映射路径列表
     *
     * @param method RequestMapping方法
     * @return 映射路径列表
     */
    private List<String> getMappingPaths(Method method) {
        // 判断是否是RequestMapping方法，顺便获取方法上的@RequestMapping注解及映射路径
        List<String> paths;
        RequestMapping methodRequestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (methodRequestMapping == null) {
            throw new RuntimeException(method.getName() + " 不是RequestMapping方法");
        } else {
            paths = new ArrayList<>(Arrays.asList(methodRequestMapping.path()));
        }
        // 获取类上的@RequestMapping注解及映射路径
        List<String> basePaths;
        RequestMapping controllerRequestMapping = AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), RequestMapping.class);
        if (controllerRequestMapping != null) {
            basePaths = new ArrayList<>(Arrays.asList(controllerRequestMapping.path()));
        } else {
            basePaths = new ArrayList<>();
        }
        // 拼接映射路径并返回
        int size = paths.size();
        for (int i = 0; i < size; i++) {
            for (String basePath : basePaths) {
                paths.add(joinPath(basePath, paths.get(0)));
            }
            paths.remove(0);
        }
        return paths;
    }

    /**
     * 拼接映射路径
     *
     * @param basePath Controller上的基础路径
     * @param path 方法上的请求路径
     * @return 拼接好的映射路径
     */
    private String joinPath(String basePath, String path) {
        if (isEmpty(basePath) && isEmpty(path)) {
            return "/";
        }
        if (isEmpty(basePath)) {
            basePath = "/";
        } else if (basePath.charAt(0) != '/') {
            basePath = "/" + basePath;
        }
        if (isEmpty(path)) {
            path = "/";
        } else if (path.charAt(0) != '/') {
            path = "/" + path;
        }
        String result = basePath + path;
        if (result.startsWith("//")) {
            return result.substring(1);
        } else {
            return result;
        }
    }

    /**
     * 字符串判空
     *
     * @param s 字符串
     * @return 为null或全部为空字符则返回true
     */
    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * 获取需要拦截的方法的拦截器列表
     *
     * @param method 需要拦截的方法
     * @return 拦截器列表
     */
    private List<HandlerInterceptor> getHandlerInterceptors(Method method) {
        // 获取@ApiInterceptor信息
        Class<?> clazz = method.getDeclaringClass();
        ApiInterceptor apiInterceptor = AnnotatedElementUtils.findMergedAnnotation(clazz, ApiInterceptor.class);
        ApiInterceptor annotation = AnnotatedElementUtils.findMergedAnnotation(method, ApiInterceptor.class);
        apiInterceptor = annotation != null ? annotation : apiInterceptor;
        if (apiInterceptor == null) {
            throw new RuntimeException(method.getName() + " 不是需要拦截的方法");
        }
        // 获取HandlerInterceptor
        List<HandlerInterceptor> list = new ArrayList<>();
        Class<? extends HandlerInterceptor>[] classes = apiInterceptor.classes();
        for (Class<? extends HandlerInterceptor> hic : classes) {
            // 从集合中获取
            HandlerInterceptor handlerInterceptor = interceptorMap.get(hic);
            if (handlerInterceptor != null) {
                list.add(handlerInterceptor);
                continue;
            }
            try {
                // 反射新建对象，存储并记录
                handlerInterceptor = hic.newInstance();
                interceptorMap.put(hic, hic.newInstance());
                list.add(handlerInterceptor);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Api拦截器初始化失败，请检查是否存在public " + clazz.getName() + "(){}方法", e);
            }
        }
        // 返回
        return list;
    }

    /**
     * 获取排序值
     *
     * @param o 对象
     * @return 通过@Order注解或者Ordered接口来获取排序值
     */
    private int getOrder(Object o) {
        int order = 0;
        Class<?> clazz = o.getClass();
        Order annotation = clazz.getAnnotation(Order.class);
        // 接口优先
        if (o instanceof Ordered) {
            order = ((Ordered) o).getOrder();
        }
        // 其后注解
        else if (annotation != null) {
            order = annotation.value();
        }
        return order;
    }

    /**
     * 注入Api拦截器
     */
    @Bean
    @ConditionalOnMissingBean(ApiHandlerInterceptor.class)
    public ApiHandlerInterceptor apiHandlerInterceptor() {
        return new ApiHandlerInterceptor() {
            @Override
            public PathMatcher getPathMatcher() {
                return new AntPathMatcher();
            }
        };
    }

    /**
     * Api拦截器
     */
    public abstract class ApiHandlerInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String servletPath = request.getServletPath();
            for (HandlerInterceptor handlerInterceptor : getHandlerInterceptor(servletPath)) {
                boolean b = handlerInterceptor.preHandle(request, response, handler);
                if (!b) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
            String servletPath = request.getServletPath();
            for (HandlerInterceptor handlerInterceptor : getHandlerInterceptor(servletPath)) {
                handlerInterceptor.postHandle(request, response, handler, modelAndView);
            }
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            String servletPath = request.getServletPath();
            for (HandlerInterceptor handlerInterceptor : getHandlerInterceptor(servletPath)) {
                handlerInterceptor.afterCompletion(request, response, handler, ex);
            }
        }

        /**
         * 获取路径匹配器
         *
         * @return 路径匹配器
         */
        public abstract PathMatcher getPathMatcher();

        // 获取匹配的拦截器
        private List<HandlerInterceptor> getHandlerInterceptor(String servletPath) {
            PathMatcher pathMatcher = getPathMatcher();
            for (String k : interceptorMappingMap.keySet()) {
                if (pathMatcher.match(k, servletPath)) {
                    return interceptorMappingMap.get(k);
                }
            }
            return new ArrayList<>();
        }
    }
}
