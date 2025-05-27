package com.example.agent_test_camp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("image_generation")
@ComponentScan(basePackages = "com.example.agent_test_camp.image_generation")
public class ImageConfig {}
