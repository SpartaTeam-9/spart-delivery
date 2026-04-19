package com.sparta.spartadelivery.Address.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@TestConfiguration
@EnableJpaAuditing
public class TestJpaConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("TEST_USER"); // 테스트 시 작성자를 "TEST_USER"로 고정
    }
}
