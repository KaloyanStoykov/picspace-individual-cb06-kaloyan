package com.picspace.project.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/entries")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST")
                .allowedHeaders("*");

        registry.addMapping("/signin")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("POST")
                .allowedHeaders("*");

        registry.addMapping("/signup")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("POST")
                .allowedHeaders("*");



    }
}
