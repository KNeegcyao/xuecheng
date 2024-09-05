package com.huanf;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author: 35238
 * 功能: content模块的启动类
 * 时间: 2024-03-28 21:21
 */
@EnableSwagger2Doc
@SpringBootApplication
@Import(com.xuecheng.base.exception.GlobalExceptionHandler.class)
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}