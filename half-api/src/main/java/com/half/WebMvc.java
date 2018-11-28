package com.half;

import com.half.controller.intercepti.Intercepto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvc extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:F:/file/");
    }

    @Bean
    public Intercepto intercepto(){
        return new Intercepto();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(intercepto())
        .addPathPatterns("/user/**")
        .addPathPatterns("/video/uploadVideo")
        .addPathPatterns("/video/saveComments")
        ;

    }
}
