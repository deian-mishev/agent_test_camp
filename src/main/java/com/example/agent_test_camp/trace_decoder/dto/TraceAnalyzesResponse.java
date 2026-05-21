package com.example.agent_test_camp.trace_decoder.dto;

public class TraceAnalyzesResponse {
  private String response;
  private boolean isError;

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public boolean isError() {
    return isError;
  }

  public void setError(boolean error) {
    isError = error;
  }

  public TraceAnalyzesResponse(String response, boolean isError) {
    this.response = response;
    this.isError = isError;
  }
}
