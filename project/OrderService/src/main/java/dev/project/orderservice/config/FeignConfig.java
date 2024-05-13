package dev.project.orderservice.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        // 모든 요청과 응답의 세부 사항을 로그로 출력합니다.
        return Logger.Level.FULL;
    }
}
