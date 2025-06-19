package com.example.agent_test_camp.trace_decoder.services;

import com.example.agent_test_camp.trace_decoder.domain.TraceTest;
import com.example.agent_test_camp.trace_decoder.domain.TraceEntry;
import com.example.agent_test_camp.trace_decoder.repository.TestRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TestService {

  private final TestRepository repo;

  public TestService(TestRepository repo) {
    this.repo = repo;
  }

  public String createTest(TraceTest testTest) {
    TraceTest saved = repo.save(testTest);
    return saved.getId();
  }

  public boolean addTraceToTest(String id, TraceEntry trace) {
    return repo.findById(id)
        .map(
            testTest -> {
              if (testTest.getTraceSteps() == null) {
                testTest.setTraceSteps(new ArrayList<>());
              }
              testTest.getTraceSteps().add(trace);
              repo.save(testTest);
              return true;
            })
        .orElse(false);
  }

  public List<TraceEntry> getTraceSteps(String id) {
    return repo.findById(id).map(TraceTest::getTraceSteps).orElse(new ArrayList<>());
  }

  public Optional<TraceTest> getTraceTest(String id) {
    return repo.findById(id);
  }

  public boolean saveTraceTest(TraceTest traceTest) {
    TraceTest saved = repo.save(traceTest);
    return saved != null && saved.getId() != null;
  }
}
