package com.example.agent_test_camp.translator.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class AudioRequest {
  @Min(value = 1, message = "Audio duration cannot be less than 1 second.")
  @Max(value = 10, message = "Max audio duration cannot be mode than 10 second.")
  private int durationInSeconds = 10;

  public int getDurationInSeconds() {
    return durationInSeconds;
  }

  public void setDurationInSeconds(int durationInSeconds) {
    this.durationInSeconds = durationInSeconds;
  }
}
