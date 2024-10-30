package com.huanf;

import com.huanf.base.exception.GlobalExceptionHandler;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/**
 * @author: 35238
 * 功能: content模块的启动类
 * 时间: 2024-03-28 21:21
 */
@EnableFeignClients(basePackages = {"com.huanf.content.feignclient"})
@EnableSwagger2Doc
@SpringBootApplication
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}