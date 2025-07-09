package com.example.agent_test_camp.config;

import com.example.agent_test_camp.ml_agent_interact.configuration.ConnectInterceptor;
import com.example.agent_test_camp.ml_agent_interact.configuration.ProjectProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;
import java.util.stream.Collectors;

@EnableAsync
@Configuration
@Profile("ml_agent_interact")
@EnableWebSocketMessageBroker
@ComponentScan(basePackages = "com.example.agent_test_camp.ml_agent_interact")
public class MLAgentInteractConfig implements WebSocketMessageBrokerConfigurer {

  @Autowired private ProjectProperties projectProperties;

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ConnectInterceptor());
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic", "/queue");
    config.setApplicationDestinationPrefixes("/app");
    config.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws/agent").setAllowedOriginPatterns("*").withSockJS();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .formLogin(form -> form.defaultSuccessUrl("/", true))
        .csrf(csrf -> csrf.ignoringRequestMatchers("/ws/**"));
    return http.build();
  }

  @Bean
  public UserDetailsService users() {
    List<UserDetails> userDetailsList =
        projectProperties.getUsers().stream()
            .map(
                u ->
                    User.withDefaultPasswordEncoder()
                        .username(u.getName())
                        .password(u.getPassword())
                        .roles("USER")
                        .build())
            .collect(Collectors.toList());

    return new InMemoryUserDetailsManager(userDetailsList);
  }
}
