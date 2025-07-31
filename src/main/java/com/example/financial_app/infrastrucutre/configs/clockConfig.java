package com.example.financial_app.infrastrucutre.configs;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class clockConfig {
  @Bean
  public Clock clock() {
    return Clock.system(ZoneId.of("America/Sao_Paulo"));
  }
}
