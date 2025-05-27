package com.example.agent_test_camp.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

@Configuration
@Profile("loan_chat")
@ComponentScan(basePackages = "com.example.agent_test_camp.loan_chat")
public class LoanChatConfig {
  @Autowired ChatMemoryRepository chatMemoryRepository;

  @Bean
  CommandLineRunner ingestDocsForAi(
      @Value("classpath:loan_agreements.txt") Resource termsOfServiceResource, VectorStore vectorStore) {
    return args -> {
      vectorStore.write(
          new TokenTextSplitter(30, 20, 1, 10000, true)
              .transform(new TextReader(termsOfServiceResource).read()));
    };
  }

  @Bean
  public VectorStore vectorStore(EmbeddingModel embeddingModel) {
    return SimpleVectorStore.builder(embeddingModel).build();
  }

  @Bean
  public ChatMemory chatMemory() {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(chatMemoryRepository)
        .maxMessages(10)
        .build();
  }
}
