package com.example.agent_test_camp.image_generation.dto;

import com.example.agent_test_camp.image_generation.validation.ValidImageRequest;
import com.example.agent_test_camp.image_generation.validation.ValidImageSize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@ValidImageRequest
public class ImageRequest {
  @NotNull(message = "Width is required")
  @ValidImageSize
  private Integer width;

  @NotNull(message = "Height is required")
  @ValidImageSize
  private Integer height;

  @NotBlank(message = "Prompt is required")
  @Size(max = 200, message = "Prompt must be less than 200 characters")
  private String prompt;

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public String getPrompt() {
    return prompt;
  }

  public void setPrompt(String prompt) {
    this.prompt = prompt;
  }
}
