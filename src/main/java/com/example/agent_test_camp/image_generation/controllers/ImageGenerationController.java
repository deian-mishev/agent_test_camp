package com.example.agent_test_camp.image_generation.controllers;

import com.example.agent_test_camp.image_generation.services.ImageGeneration;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.image.ImageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/images")
public class ImageGenerationController {

  private final ImageGeneration imageGeneration;

  public ImageGenerationController(ImageGeneration imageGeneration) {
    this.imageGeneration = imageGeneration;
  }

  @GetMapping("/generate")
  public void generateImage(
          HttpServletResponse response,
          @RequestParam(name = "prompt") String prompt) throws IOException {
    ImageResponse imageResponse = imageGeneration.generateImage(prompt);
    response.sendRedirect(imageResponse.getResult().getOutput().getUrl());
  }
}
