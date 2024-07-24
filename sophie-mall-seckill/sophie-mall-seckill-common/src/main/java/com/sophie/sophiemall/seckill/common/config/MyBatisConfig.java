package com.sophie.sophiemall.seckill.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis相关配置
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.sophie.sophiemall.seckill"})
public class MyBatisConfig {
}
