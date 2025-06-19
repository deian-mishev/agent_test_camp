package com.example.agent_test_camp.trace_decoder.tools;

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
      Retrieve all previous trace entries for a given test.
      Use this information to provide more indepth information about the error case. Take notice that
      the same error case might be included as the last entry.""")
  public List<TraceEntry> getTraceEntries(
      @ToolParam(description = "test ID of the current trace") String testId) {
    return testService.getTraceSteps(testId);
  }
}
