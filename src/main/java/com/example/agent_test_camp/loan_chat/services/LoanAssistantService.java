package com.example.agent_test_camp.loan_chat.services;

import com.example.agent_test_camp.loan_chat.tools.LoanAssistantTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import static org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor.TOP_K;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
public class LoanAssistantService {
  private final ChatClient chatClient;

  public LoanAssistantService(
      ChatClient.Builder modelBuilder,
      VectorStore vectorStore,
      ChatMemory chatMemory,
      LoanAssistantTools aiTools) {
    this.chatClient =
        modelBuilder
            .defaultSystem(
                """
                        You are a loan assistant for a banking service.
                        Your assist users with their questions regarding loans, payments and account information.
                        If you are unsure about something, politely inform the user and suggest contacting customer support.
                        If somebody ask about something else, just say you don't know.
                        """)
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                QuestionAnswerAdvisor.builder(vectorStore)
                    .searchRequest(
                        SearchRequest.builder().topK(10).similarityThreshold(0.7).build())
                    .build())
            .defaultTools(aiTools)
            .build();
  }

  public String chat(String chatId, String userMessageContent) {
    return this.chatClient
        .prompt()
        .user(userMessageContent)
        .advisors(a -> a.param(CONVERSATION_ID, chatId).param(TOP_K, 1))
            .call().content();
  }
}
