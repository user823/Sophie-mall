package com.sophie.sophiemall.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Mybatis 相关配置
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.sophie.sophiemall.mapper", "com.sophie.sophiemall.dao"})
public class MyBatisConfig {
}
