package com.example.agent_test_camp.image_generation.services;

import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;

@Service
public class ImageGeneration {

  private final OpenAiImageModel openAiImageModel;

  public ImageGeneration(OpenAiImageModel openAiImageModel) {
    this.openAiImageModel = openAiImageModel;
  }

  public ImageResponse generateImage(String prompt, int width, int height) {
    return openAiImageModel.call(
        new ImagePrompt(
            prompt,
            OpenAiImageOptions.builder().height(height).width(width).build()));
  }
}
