package com.example.agent_test_camp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("recipes_chat")
@ComponentScan(basePackages = "com.example.agent_test_camp.recipes_chat")
public class RecipesChatConfig {}
