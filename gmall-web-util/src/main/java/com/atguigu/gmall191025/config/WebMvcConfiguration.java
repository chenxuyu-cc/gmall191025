package com.atguigu.gmall191025.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;



@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    // 获取自定义拦截器

//    @Autowired
//    private Auth auth;
    @Autowired
    private AuthInterceptor authInterceptor;


    public void addInterceptors(InterceptorRegistry registry) {
        // 设置自定义拦截器拦截的请求
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
        // 将拦截器放入拦截器栈
        super.addInterceptors(registry);
    }
}

//@Configuration
//public class WebMvcConfiguration implements WebMvcConfigurer {
//
////    @Autowired
////    private AuthInterceptor authInterceptor;
//
//    @Autowired
//    private Auth auth;
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//         //设置自定义拦截器拦截的请求
//        registry.addInterceptor(auth).addPathPatterns("/**");
//        // 将拦截器放入拦截器栈
//        super.
//
//
//    }
//
//    @Override
//    public void configurePathMatch(PathMatchConfigurer pathMatchConfigurer) {
//
//    }
//
//    @Override
//    public void configureContentNegotiation(ContentNegotiationConfigurer contentNegotiationConfigurer) {
//
//    }
//
//    @Override
//    public void configureAsyncSupport(AsyncSupportConfigurer asyncSupportConfigurer) {
//
//    }
//
//    @Override
//    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer defaultServletHandlerConfigurer) {
//
//    }
//
//    @Override
//    public void addFormatters(FormatterRegistry formatterRegistry) {
//
//    }
//
//
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
//
//    }
//
//    @Override
//    public void addCorsMappings(CorsRegistry corsRegistry) {
//
//    }
//
//    @Override
//    public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {
//
//    }
//
//    @Override
//    public void configureViewResolvers(ViewResolverRegistry viewResolverRegistry) {
//
//    }
//
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> list) {
//
//    }
//
//    @Override
//    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> list) {
//
//    }
//
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> list) {
//
//    }
//
//    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> list) {
//
//    }
//
//    @Override
//    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> list) {
//
//    }
//
//    @Override
//    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> list) {
//
//    }
//
//    @Override
//    public Validator getValidator() {
//        return null;
//    }
//
//    @Override
//    public MessageCodesResolver getMessageCodesResolver() {
//        return null;
//    }
//}

