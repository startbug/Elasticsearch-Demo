package com.ggs.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ggs.project.mapper")
public class IntegratedProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegratedProjectApplication.class, args);
    }

}
