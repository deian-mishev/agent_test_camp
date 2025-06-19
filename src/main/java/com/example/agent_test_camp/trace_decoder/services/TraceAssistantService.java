package com.example.agent_test_camp.trace_decoder.services;

import static org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor.TOP_K;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import com.example.agent_test_camp.loan_chat.tools.LoanAssistantTools;
import com.example.agent_test_camp.trace_decoder.domain.TraceEntry;
import com.example.agent_test_camp.trace_decoder.domain.TraceTest;
import com.example.agent_test_camp.trace_decoder.dto.TraceAnalyzesResponse;
import jakarta.transaction.Transactional;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class TraceAssistantService {

  private final TestService testService;
  private final ChatClient chatClient;
  private final PromptTemplate promptTemplate;
  private final BeanOutputConverter<TraceAnalyzesResponse> outputConverter;

  public TraceAssistantService(
      ChatClient.Builder modelBuilder,
      VectorStore vectorStore,
      ChatMemory chatMemory,
//      LoanAssistantTools aiTools,
      TestService testService) {
    this.testService = testService;
    ParameterizedTypeReference<TraceAnalyzesResponse> typeRef =
        new ParameterizedTypeReference<TraceAnalyzesResponse>() {};
    this.outputConverter = new BeanOutputConverter<>(typeRef);
    this.chatClient =
        modelBuilder
            .defaultSystem(
                """
                        You are a test trace assistant service.
                        Your translate traces when you find an exception or error in the trace to human readable format.
                        You to translate failing trace you use the provided RAG document information.
                        If somebody ask about something else, just say you don't know.
                        """)
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                QuestionAnswerAdvisor.builder(vectorStore)
                    .searchRequest(
                        SearchRequest.builder().topK(10).similarityThreshold(0.7).build())
                    .build())
//            .defaultTools(aiTools)
            .build();

    this.promptTemplate =
        PromptTemplate.builder()
            .renderer(
                StTemplateRenderer.builder()
                    .startDelimiterToken('{')
                    .endDelimiterToken('}')
                    .build())
            .template(
                """
                You are a trace assistant. You receive a trace and must return a JSON object with:
                - "response": a human-readable explanation of the trace
                - "isError": true if there is an error/exception in the trace, false otherwise

                Trace:
                ---
                {trace}

                Format:
                {format}
            """)
            .build();
    this.promptTemplate.add("format", outputConverter.getFormat());
  }

  @Transactional
  public TraceAnalyzesResponse analyzeTrace(String testId, TraceEntry traceEntry) {
    Optional<TraceTest> optionalTraceTest = testService.getTraceTest(testId);
    if (optionalTraceTest.isEmpty()) {
      throw new RuntimeException(String.format("Test with id '%s' does not exist", testId));
    }
    TraceTest traceTest = optionalTraceTest.get();
    String responseText =
        chatClient
            .prompt(promptTemplate.render(Map.of("trace", traceEntry.toString())))
            .advisors(a -> a.param(CONVERSATION_ID, testId).param(TOP_K, 1))
            .call()
            .content();

    TraceAnalyzesResponse traceAnalyzesResponse = outputConverter.convert(responseText);

    traceTest.getTraceSteps().add(traceEntry);
    testService.saveTraceTest(traceTest);

    return traceAnalyzesResponse;
  }
}
