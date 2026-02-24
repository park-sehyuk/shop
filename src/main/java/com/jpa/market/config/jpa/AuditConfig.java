package com.jpa.market.config.jpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration      // 빈 등록
@EnableJpaAuditing  // JPA 기능 중 Auditing 기능을 활성화
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorAware(){

        return new AuditorAwareImpl();
    }
}
