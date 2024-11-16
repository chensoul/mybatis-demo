package com.mycompany.myapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@OpenAPIDefinition(info = @Info(title = "mybatis-demo", version = "0.0.1-SNAPSHOT"), servers = @Server(url = "/"))
class SpringdocConfig {}
