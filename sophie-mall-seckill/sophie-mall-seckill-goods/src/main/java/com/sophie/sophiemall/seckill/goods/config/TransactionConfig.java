package com.sophie.sophiemall.seckill.goods.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.sophie.sophiemall.seckill.common.config.JdbcConfig;
import com.sophie.sophiemall.seckill.common.config.MyBatisConfig;
import com.sophie.sophiemall.seckill.common.config.RedisConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan(value = {"com.sophie.sophiemall.seckill.goods.mapper"})
@ComponentScan(value = {"com.sophie.sophiemall.seckill.goods"})
@Import({JdbcConfig.class, RedisConfig.class, MyBatisConfig.class})
@EnableTransactionManagement(proxyTargetClass = true)
@ServletComponentScan(basePackages = {"io.binghe.seckill"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class TransactionConfig {

    @Bean
    public TransactionManager transactionManager(DruidDataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }
}
