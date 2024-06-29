package com.sophie.sophiemall.search.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis相关配置
 * Created by macro on 2019/4/8.
 */
@Configuration
@MapperScan({"com.sophie.sophiemall.mapper","com.sophie.sophiemall.search.dao"})
public class MyBatisConfig {
}