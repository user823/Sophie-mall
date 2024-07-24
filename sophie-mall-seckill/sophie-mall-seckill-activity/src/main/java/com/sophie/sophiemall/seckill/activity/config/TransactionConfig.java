package com.sophie.sophiemall.seckill.activity.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.sophie.sophiemall.seckill.common.config.JdbcConfig;
import com.sophie.sophiemall.seckill.common.config.MyBatisConfig;
import com.sophie.sophiemall.seckill.common.config.RedisConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan("com.sophie.sophiemall.seckill.activity.mapper")
@Import({JdbcConfig.class, RedisConfig.class, MyBatisConfig.class})
@EnableTransactionManagement(proxyTargetClass = true)
@ServletComponentScan(basePackages = {"com.sophie.sophiemall.seckill.activity"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class TransactionConfig {

    @Bean
    public TransactionManager transactionManager(DruidDataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }
}