package com.yn.anime.favorite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.yn.anime.favorite.client")
@SpringBootApplication
public class FavoriteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FavoriteServiceApplication.class, args);
    }
}
