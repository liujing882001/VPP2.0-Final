package com.example.start.encrypt;

import com.example.start.Interceptor.RequestHeaderContextInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 绑定拦截器
 */
@Configuration
public class EncryptInterceptorConfig implements WebMvcConfigurer {

    @Bean
    public HandlerInterceptor requestEncryptInterceptor() {
        return new EncryptInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestEncryptInterceptor())
                .addPathPatterns("/drSouth/**")
                .excludePathPatterns("/drSouth/login");
        WebMvcConfigurer.super.addInterceptors(registry);
    }

}
