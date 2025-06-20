package com.example.agent_test_camp.trace_decoder.repository;

import com.example.agent_test_camp.trace_decoder.domain.TraceTest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TestRepository extends ElasticsearchRepository<TraceTest, String> {

    List<TraceTest> findByCreatedAtAfterAndIdNot(Instant time, String id);
}
