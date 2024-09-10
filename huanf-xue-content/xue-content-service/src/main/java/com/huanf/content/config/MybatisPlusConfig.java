package com.huanf.content.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: 35238
 * 功能: MyBatisPlus的分页插件
 * 时间: 2024-03-30 23:13
 *
 * 分页插件的原理：
 * 首先分页参数放到ThreadLocal中，拦截执行的sql，根据数据库类型添加对应的分页语句重写sql，
 * 例如：(select * from table where a) 转换为 (select count(*) from table where a)和(select * from table where a limit ,)
 * 计算出了total总条数、pageNum当前第几页、pageSize每页大小和当前页的数据，是否为首页，是否为尾页，总页数等
 */
@Configuration
@MapperScan("com.huanf.content.mapper")
public class MybatisPlusConfig {
    /**
     * 定义分页拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}