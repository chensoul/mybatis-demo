package io.github.rosestack.myapp.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan("io.github.rosestack.myapp.repository")
@EnableTransactionManagement
public class DomainConfig {
}
