package com.example.agent_test_camp.ml_agent_interact.controllers;

import com.example.agent_test_camp.ml_agent_interact.configuration.ProjectProperties;
import com.example.agent_test_camp.ml_agent_interact.services.FlaskSocketIOClient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Validated
@Controller
public class AgentController {

  private final ProjectProperties projectProperties;
  private final SimpMessagingTemplate messagingTemplate;
  private final Map<String, FlaskSocketIOClient> clientMap = new ConcurrentHashMap<>();
  private final WebClient webClient;

  public AgentController(
      ProjectProperties projectProperties, SimpMessagingTemplate messagingTemplate) {
    this.projectProperties = projectProperties;
    this.messagingTemplate = messagingTemplate;
    this.webClient = WebClient.builder().baseUrl(projectProperties.getAgent().getUrl()).build();
  }

  @GetMapping("/preconnect")
  public Mono<ResponseEntity<String>> proxyPreconnect() {
    return webClient
        .get()
        .uri("/preconnect")
        .retrieve()
        .toEntity(String.class)
        .onErrorResume(
            e -> Mono.just(
                ResponseEntity.internalServerError()
                    .body("Flask server unreachable")));
  }

  @Async
  @MessageMapping("/input")
  public CompletableFuture<String> handleInput(
      Principal principal,
      StompHeaderAccessor accessor,
      @Header("simpSessionId") String simpSessionId,
      @NotEmpty(message = "Keys must not be empty")
          List<@NotBlank(message = "Each key must not be blank") String> keys) {

    FlaskSocketIOClient socketIOClient =
        clientMap.computeIfAbsent(
            simpSessionId,
            key -> {
              String name = principal.getName();
              System.out.println("Creating and connecting new FlaskSocketIOClient for: " + name);

              FlaskSocketIOClient newClient =
                  new FlaskSocketIOClient(
                      base64Image ->
                          messagingTemplate.convertAndSendToUser(name, "/queue/frame", base64Image),
                      endMessage -> {
                        messagingTemplate.convertAndSendToUser(
                            name, "/queue/episode_end", endMessage);
                        FlaskSocketIOClient removed = clientMap.remove(key);
                        if (removed != null) {
                          removed.disconnect();
                        }
                      },
                      projectProperties.getAgent());

              try {
                newClient.connect(
                        (String) accessor.getSessionAttributes().get("env"),
                        (String) accessor.getSessionAttributes().get("ai_player"));
              } catch (URISyntaxException e) {
                e.printStackTrace();
                return null; // don't store failed connection
              }

              return newClient;
            });

    if (socketIOClient != null && socketIOClient.isConnected()) {
      socketIOClient.sendInput(keys);
    }

    return CompletableFuture.completedFuture("");
  }

  @EventListener
  public void handleSessionDisconnect(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = accessor.getSessionId();

    System.out.println("Session disconnected: " + sessionId);

    FlaskSocketIOClient client = clientMap.remove(sessionId);
    if (client != null) {
      client.disconnect();
      System.out.println("Cleaned up FlaskSocketIOClient for session: " + sessionId);
    } else {
      System.out.println("No client to clean up for session: " + sessionId);
    }
  }
}
