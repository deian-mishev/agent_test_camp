package com.example.agent_test_camp.trace_decoder.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "test-traces")
public class TraceTest {

  @Id private String id;

  @Field(type = FieldType.Keyword)
  private String testName;

  @Field(type = FieldType.Double)
  private Double wallTime;

  @Field(type = FieldType.Keyword)
  private String platform;

  @Field(type = FieldType.Keyword)
  private String browserName;

  @Field(type = FieldType.Keyword)
  private String sdkLanguage;

  @Field(type = FieldType.Nested)
  private List<TraceEntry> traceEntrySteps;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTestName() {
    return testName;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }

  public Double getWallTime() {
    return wallTime;
  }

  public void setWallTime(Double wallTime) {
    this.wallTime = wallTime;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public String getBrowserName() {
    return browserName;
  }

  public void setBrowserName(String browserName) {
    this.browserName = browserName;
  }

  public String getSdkLanguage() {
    return sdkLanguage;
  }

  public void setSdkLanguage(String sdkLanguage) {
    this.sdkLanguage = sdkLanguage;
  }

  public List<TraceEntry> getTraceSteps() {
    return traceEntrySteps;
  }

  public void setTraceSteps(List<TraceEntry> traceEntrySteps) {
    this.traceEntrySteps = traceEntrySteps;
  }

  @Override
  public String toString() {
    return "Test{" +
            "id='" + id + '\'' +
            ", testName='" + testName + '\'' +
            ", wallTime=" + wallTime +
            ", platform='" + platform + '\'' +
            ", browserName='" + browserName + '\'' +
            ", sdkLanguage='" + sdkLanguage + '\'' +
            ", traceSteps=" + traceEntrySteps +
            '}';
  }
}
