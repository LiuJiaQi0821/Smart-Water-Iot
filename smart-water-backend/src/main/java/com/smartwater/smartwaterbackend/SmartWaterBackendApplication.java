package com.smartwater.smartwaterbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.smartwater.smartwaterbackend.mapper")
public class SmartWaterBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartWaterBackendApplication.class, args);
    }

}