package com.example.agent_test_camp.trace_decoder.domain;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;
import java.util.List;

public class TraceEntry {

  @Id private String id;

  @Field(type = FieldType.Keyword)
  @NotBlank(message = "Type must not be blank")
  private String type;

  @Field(type = FieldType.Keyword)
  private String message;

  @Field(type = FieldType.Keyword)
  private String callId;

  @Field(type = FieldType.Keyword)
  private String parentId;

  @Field(type = FieldType.Keyword)
  private String apiName;

  @Field(type = FieldType.Keyword)
  private String method;

  @Field(type = FieldType.Keyword)
  private String clazz;

  @Field(type = FieldType.Double)
  private Double startTime;

  @Field(type = FieldType.Double)
  private Double endTime;

  @Field(type = FieldType.Object)
  private Map<String, Object> params;

  @Field(type = FieldType.Object)
  private List<Map<String, Object>> stack;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCallId() {
    return callId;
  }

  public void setCallId(String callId) {
    this.callId = callId;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getApiName() {
    return apiName;
  }

  public void setApiName(String apiName) {
    this.apiName = apiName;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getClazz() {
    return clazz;
  }

  public void setClazz(String clazz) {
    this.clazz = clazz;
  }

  public Double getStartTime() {
    return startTime;
  }

  public void setStartTime(Double startTime) {
    this.startTime = startTime;
  }

  public Double getEndTime() {
    return endTime;
  }

  public void setEndTime(Double endTime) {
    this.endTime = endTime;
  }

  public Map<String, Object> getParams() {
    return params;
  }

  public void setParams(Map<String, Object> params) {
    this.params = params;
  }

  public List<Map<String, Object>> getStack() {
    return stack;
  }

  public void setStack(List<Map<String, Object>> stack) {
    this.stack = stack;
  }

  @Override
  public String toString() {
    return "TraceEntry{" +
            "id='" + id + '\'' +
            ", type='" + type + '\'' +
            ", message='" + message + '\'' +
            ", callId='" + callId + '\'' +
            ", parentId='" + parentId + '\'' +
            ", apiName='" + apiName + '\'' +
            ", method='" + method + '\'' +
            ", clazz='" + clazz + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", params=" + params +
            ", stack=" + stack +
            '}';
  }

}
