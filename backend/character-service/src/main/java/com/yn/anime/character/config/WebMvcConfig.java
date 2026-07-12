package com.yn.anime.character.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/*
    classDescription:
*/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-folder}")
    private String uploadFolder;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 自动创建文件夹防报错：如果项目目录下还没有 uploads 文件夹，程序启动时自动建一个
        File folder = new File(uploadFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 映射物理路径：将 http://localhost:8081/uploads/** 映射到本地真实目录
        registry.addResourceHandler("/uploads/characters/**")
                .addResourceLocations("file:" + uploadFolder);
    }
}