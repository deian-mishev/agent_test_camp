package com.example.agent_test_camp.image_generation.dto;

import com.example.agent_test_camp.image_generation.validation.ValidImageQuality;
import com.example.agent_test_camp.image_generation.validation.ValidImageRequest;
import com.example.agent_test_camp.image_generation.validation.ValidImageSize;
import com.example.agent_test_camp.image_generation.validation.ValidImageStyle;
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
  @Size(max = 1000, message = "Prompt must be less than 1000 characters")
  private String prompt;

  @NotNull(message = "Quality of the image is required")
  @ValidImageQuality
  private String quality;

  @NotNull(message = "Style of the image is required")
  @ValidImageStyle
  private String style;

  public @NotNull(message = "Quality of the image is required") String getQuality() {
    return quality;
  }

  public void setQuality(@NotNull(message = "Quality of the image is required") String quality) {
    this.quality = quality;
  }

  public @NotNull(message = "Style of the image is required") String getStyle() {
    return style;
  }

  public void setStyle(@NotNull(message = "Style of the image is required") String style) {
    this.style = style;
  }

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
