package com.example.agent_test_camp.trace_decoder.services;

import static com.example.agent_test_camp.trace_decoder.services.TestService.ERROR_CODE;
import static org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor.TOP_K;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import com.example.agent_test_camp.trace_decoder.domain.TraceEntry;
import com.example.agent_test_camp.trace_decoder.dto.TraceAnalyzesResponse;
import com.example.agent_test_camp.trace_decoder.tools.TraceAssistantTools;
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
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;

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
      TraceAssistantTools aiTools,
      TestService testService) {
    this.testService = testService;
    this.chatClient =
        modelBuilder
            .defaultSystem(
                """
                        You are a test trace analyze assistant. You translate traces from tests into human-readable format.
                        You identify error traces according to the provided documentation which feature specific traces for the analysed system.
                        If you detect an error in the trace, call the `getTraceEntries` tool with the 'testId' and in the response and use the response to
                        better understand the cause of the error. If the `getTraceEntries` tool was called also always include the number of times
                        a similar errors have occurred based on this tools response knowing these are the errors for the last hour for other test
                        aka not the one currently running identified by 'testId'.
                        """)
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                QuestionAnswerAdvisor.builder(vectorStore)
                    .searchRequest(
                        SearchRequest.builder().topK(10).similarityThreshold(0.7).build())
                    .build())
            .defaultTools(aiTools)
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

                Test ID:
                {testId}

                Trace:
                ---
                {trace}

                Format:
                {format}
            """)
            .build();
    ParameterizedTypeReference<TraceAnalyzesResponse> typeRef =
        new ParameterizedTypeReference<TraceAnalyzesResponse>() {};
    this.outputConverter = new BeanOutputConverter<>(typeRef);
    this.promptTemplate.add("format", outputConverter.getFormat());
  }

  @Transactional
  public String analyzeTrace(String testId, TraceEntry traceEntry) {
    if (!testService.existsById(testId)) {
      throw new NoSuchElementException(String.format("Test with id '%s' does not exist", testId));
    }

    if (!traceEntry.getType().equalsIgnoreCase(ERROR_CODE)) {
      testService.addTraceToTest(testId, traceEntry);
      return "Regular trace, nothing to analyze.";
    }

    String responseText =
        chatClient
            .prompt(promptTemplate.render(Map.of("trace", traceEntry.toString(), "testId", testId)))
            .advisors(a -> a.param(CONVERSATION_ID, testId).param(TOP_K, 1))
            .call()
            .content();

    TraceAnalyzesResponse traceAnalyzesResponse = outputConverter.convert(responseText);
    testService.addTraceToTest(testId, traceEntry);
    return traceAnalyzesResponse.getResponse();
  }
}
