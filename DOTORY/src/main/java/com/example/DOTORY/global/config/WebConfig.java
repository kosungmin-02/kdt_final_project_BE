package com.example.DOTORY.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /avatars/** 요청 → 서버의 uploads/avatars/ 폴더 매핑
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:uploads/avatars/");

        // 게시글 이미지
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }




}
