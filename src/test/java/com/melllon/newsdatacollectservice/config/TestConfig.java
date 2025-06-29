package com.melllon.newsdatacollectservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true", matchIfMissing = false)
@EnableScheduling
public class TestConfig {
    // 테스트 환경에서는 스케줄러가 비활성화됨
} 