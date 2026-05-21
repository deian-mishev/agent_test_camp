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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;

@Configuration
@Profile("trace_decoder")
@ComponentScan(basePackages = "com.example.agent_test_camp.trace_decoder")
public class TraceDecoderConfig {
  @Autowired
  ChatMemoryRepository chatMemoryRepository;

  @Bean
  CommandLineRunner ingestDocsForAi(
          @Value("${docs.path}") String docsPath, VectorStore vectorStore) {
    return args -> {
      File folder = new File(docsPath);

      if (folder.isDirectory()) {
        File[] files = folder.listFiles();
        if (files != null) {
          for (File file : files) {
            if (file.isFile()) {
              Resource fileResource = new FileSystemResource(file);

              vectorStore.write(
                  new TokenTextSplitter(30, 20, 1, 10000, true)
                      .transform(new TextReader(fileResource).read()));
            }
          }
        }
      }
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
