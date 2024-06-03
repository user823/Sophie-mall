package com.sophie.sophiemall.main.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis相关配置
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.sophie.sophiemall.mapper","com.sophie.sophiemall.main.dao"})
public class MyBatisConfig {
}
