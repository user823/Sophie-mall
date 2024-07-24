package com.sophie.sophiemall.seckill.order.config;

import com.sophie.sophiemall.seckill.common.config.RedisConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan(value = {"com.sophie.sophiemall.seckill.order.mapper"})
@ComponentScan(value = {"com.sophie.sophiemall.seckill"})
@Import({RedisConfig.class})
@EnableTransactionManagement(proxyTargetClass = true)
@ServletComponentScan(basePackages = {"io.binghe.seckill"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class TransactionConfig {

}
