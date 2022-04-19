package com.sin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.sin.mapper")
@ComponentScan(basePackages = {"org.n3r.idworker", "com.sin.config", "com.sin.controller", "com.sin.service", "com.sin.aspect", "com.sin.mapper", "com.sin.resource", "com.sin.util"})
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        try {
            SpringApplication.run(Application.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
