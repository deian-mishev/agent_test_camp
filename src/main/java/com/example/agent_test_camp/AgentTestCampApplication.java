package com.example.agent_test_camp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.agent_test_camp.config")
public class AgentTestCampApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgentTestCampApplication.class, args);
	}

}
