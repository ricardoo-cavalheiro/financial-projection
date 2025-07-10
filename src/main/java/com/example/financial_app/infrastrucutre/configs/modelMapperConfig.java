package com.example.financial_app.infrastrucutre.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;

@Configuration
public class modelMapperConfig {
  @Bean
  public org.modelmapper.ModelMapper modelMapper() {
    var modelMapper = new ModelMapper();

    return modelMapper;
  }
}
