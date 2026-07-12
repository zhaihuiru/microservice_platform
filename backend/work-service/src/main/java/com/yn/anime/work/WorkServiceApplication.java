package com.yn.anime.work;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan("com.yn.anime.work.mapper")
public class WorkServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkServiceApplication.class, args);
    }
}
