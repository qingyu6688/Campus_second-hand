package com.campus.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件配置
 * 该方法用于配置Mybatis-Plus的分页插件，使其支持MySQL数据库的分页查询功能
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
    // 创建MybatisPlusInterceptor实例，用于添加各种内部拦截器
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    // 添加MySQL数据库的分页插件，实现分页功能
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    // 返回配置好的拦截器实例
        return interceptor;
    }
}
