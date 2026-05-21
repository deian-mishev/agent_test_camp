package com.example.agent_test_camp.ml_agent_interact.configuration;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class ConnectInterceptor implements ChannelInterceptor {
  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      accessor.getSessionAttributes().put("env", accessor.getFirstNativeHeader("env"));
      accessor.getSessionAttributes().put("ai_player", accessor.getFirstNativeHeader("ai_player"));
    }
    return message;
  }
}
