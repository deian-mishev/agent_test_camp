package com.example.agent_test_camp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
@Profile("translator")
@ComponentScan(basePackages = "com.example.agent_test_camp.translator")
public class TranslatorConfig {}
