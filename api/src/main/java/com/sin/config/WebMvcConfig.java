package com.sin.config;

import com.sin.intercept.UserTokenIntercept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射本地静态资源
        registry.addResourceHandler("/**")
                .addResourceLocations("/fafood/images/")
                .addResourceLocations("classpath:/META-INF/resources"); // swagger 2
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Autowired
    private UserTokenIntercept userTokenIntercept;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTokenIntercept)
                .addPathPatterns("/hello")
                .addPathPatterns("/shopcart/add")
                .addPathPatterns("/shopcart/del")
                .addPathPatterns("/address/list")
                .addPathPatterns("/address/add")
                .addPathPatterns("/address/update")
                .addPathPatterns("/address/setDefalut")
                .addPathPatterns("/address/delete")
                .addPathPatterns("/orders/*")
                .addPathPatterns("/center/*")
                .addPathPatterns("/userInfo/*")
                .addPathPatterns("/myorders/*")
                .addPathPatterns("/mycomments/*")
                .excludePathPatterns("/myorders/deliver")
                .excludePathPatterns("/orders/notifyMerchantOrderPaid");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
