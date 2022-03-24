package com.sin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    public CorsConfig() {
    }

    @Bean
    public CorsFilter corsFilter() {
        // 1. 添加cors配置信息
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:8080");
        // 2. 是否发送cookie信息
        config.setAllowCredentials(true);

        // 3. 设置允许请求方式
        config.addAllowedMethod("*");

        // 4. 设置允许的header
        config.addAllowedHeader("*");

        //5. 为url添加映射路径
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", config);

        // 6. 放回重新定义好的corsSources
        return new CorsFilter(corsConfigurationSource);
    }
}
