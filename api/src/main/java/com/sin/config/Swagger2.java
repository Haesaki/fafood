package com.sin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class Swagger2 implements WebMvcConfigurer{

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)      // swagger2核心配置 docket -> SWAGGER_2
                .apiInfo(apiInfo())                         // 定义api文档汇总信息
                .select().apis(RequestHandlerSelectors.
                        basePackage("com.sin.controller"))  // 指定controller包
                .paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("FaFood 平台接口API")
                .contact(new Contact("sin", "sin.com", "laohapi@gmail.com"))
                .description("为了美妙的食物")
                .version("1.0.0")
                .build();
    }
}