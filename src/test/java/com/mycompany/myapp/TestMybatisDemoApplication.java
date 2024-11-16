package com.mycompany.myapp;

import com.mycompany.myapp.common.ContainersConfig;
import org.springframework.boot.SpringApplication;

public class TestMybatisDemoApplication {

    public static void main(String[] args) {
        SpringApplication.from(MybatisDemoApplication::main)
                .with(ContainersConfig.class)
                .run(args);
    }
}
