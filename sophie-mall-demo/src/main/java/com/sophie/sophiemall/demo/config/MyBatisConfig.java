package com.sophie.sophiemall.demo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis相关配置
 */
@Configuration
@MapperScan("com.sophie.sophiemall.mapper")
public class MyBatisConfig {
}
