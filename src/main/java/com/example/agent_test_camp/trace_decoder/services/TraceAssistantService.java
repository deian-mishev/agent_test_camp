package com.example.agent_test_camp.trace_decoder.services;

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

  private static final String ERROR = "error";
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
    ParameterizedTypeReference<TraceAnalyzesResponse> typeRef =
        new ParameterizedTypeReference<TraceAnalyzesResponse>() {};
    this.outputConverter = new BeanOutputConverter<>(typeRef);
    this.chatClient =
        modelBuilder
            .defaultSystem(
                """
                        You are a test trace assistant service.
                        You translate failing traces into human-readable format.
                        Use it whenever you need more context or suspect an error might be better understood with the full trace history.
                        If you detect an error in the trace, call the `getTraceEntries` tool with the testId to get all trace entries of the test
                        before responding in order to make a more knowledgeable analysis of the context of the error. If this error apears in more than
                        once in the responses then say how many times it appears in the test.

                        If someone asks about something else, say you don't know.
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
                If you detect an error in the trace, call the `getTraceEntries` tool with the testId to get all trace entries before responding.

                Test ID:
                {testId}

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
  public String analyzeTrace(String testId, TraceEntry traceEntry) {
    if (!testService.existsById(testId)) {
      throw new NoSuchElementException(String.format("Test with id '%s' does not exist", testId));
    }

    if (!traceEntry.getType().equalsIgnoreCase(ERROR)) {
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
