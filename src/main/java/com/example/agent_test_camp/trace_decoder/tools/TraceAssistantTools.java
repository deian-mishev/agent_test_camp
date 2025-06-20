package com.example.agent_test_camp.trace_decoder.tools;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.example.agent_test_camp.trace_decoder.domain.TraceEntry;
import com.example.agent_test_camp.trace_decoder.services.TestService;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class TraceAssistantTools {

  private final TestService testService;

  public TraceAssistantTools(TestService testService) {
    this.testService = testService;
  }

  @Tool(
      name = "getTraceEntries",
      description =
          """
            This tool responds with all trace entries of all tests for the last hour in order to
            make a more knowledgeable analysis of the context of the error. In the response always include the number
            of times a similar errors have occurred based on this tools response knowing these are the errors for the last hour.
            """)
  public List<TraceEntry> getTraceEntries(
      @ToolParam(description = "test ID of the current trace") String testId) {
    // Chat memory handles test context, testId here is used to remove entries from response
    Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
    return testService.getRecentTracesSinceExcluding(testId, oneHourAgo);
  }
}
