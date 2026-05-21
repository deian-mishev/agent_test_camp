package com.example.agent_test_camp.image_generation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ImageRefRequest {

    @NotBlank(message = "Prompt is required")
    private String prompt;

    private String model = "gpt-image-1";

    @Min(value = 1, message = "n must be at least 1")
    private Integer n = 1;

    private String quality;

    private String user;

    private String responseFormat;

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getN() { return n; }
    public void setN(Integer n) { this.n = n; }

    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getResponseFormat() { return responseFormat; }
    public void setResponseFormat(String responseFormat) { this.responseFormat = responseFormat; }
}
