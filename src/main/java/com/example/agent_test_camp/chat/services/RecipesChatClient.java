package com.example.agent_test_camp.chat.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipesChatClient {
  private final ChatClient chatClient;

  public RecipesChatClient(ChatClient.Builder chatClientBuilder) {
    this.chatClient =
        chatClientBuilder
            .defaultSystem(
                """
                You are a helpful cooking assistant.
                Respond clearly and include recipes when relevant.
                If somebody ask about something else, just say you don't know.
                """)
            .build();
  }

  public String getResponse(List<Message> messages) {
    return this.chatClient.prompt().messages(messages).call().content();
  }

  public String getResponse(String prompt) {
    return this.chatClient.prompt(prompt).call().content();
  }
}
