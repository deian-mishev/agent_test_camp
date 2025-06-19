package com.example.agent_test_camp.trace_decoder.controllers;

import com.example.agent_test_camp.trace_decoder.domain.TraceTest;
import com.example.agent_test_camp.trace_decoder.domain.TraceEntry;
import com.example.agent_test_camp.trace_decoder.dto.TraceAnalyzesResponse;
import com.example.agent_test_camp.trace_decoder.services.TestService;
import com.example.agent_test_camp.trace_decoder.services.TraceAssistantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trace")
public class TraceController {

  private final TestService service;
  private final TraceAssistantService traceAssistantService;

  public TraceController(TestService service, TraceAssistantService traceAssistantService) {
    this.traceAssistantService = traceAssistantService;
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<String> createTest(@RequestBody TraceTest testTest) {
    String id = service.createTest(testTest);
    return ResponseEntity.status(HttpStatus.CREATED).body(id);
  }

  @GetMapping("/{id}")
  public ResponseEntity<List<TraceEntry>> getTraceSteps(@PathVariable String id) {
    List<TraceEntry> traceSteps = service.getTraceSteps(id);
    if (traceSteps.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(traceSteps);
  }

  @PostMapping("/{id}/trace")
  public ResponseEntity<String> addTrace(
      @PathVariable String id, @Valid @RequestBody TraceEntry trace) {
    return ResponseEntity.ok(traceAssistantService.analyzeTrace(id, trace));
  }
}
