package com.example.agent_test_camp.image_generation.controllers;

import com.example.agent_test_camp.image_generation.dto.ImageRequest;
import com.example.agent_test_camp.image_generation.services.ImageGeneration;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.ai.image.ImageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/images")
public class ImageGenerationController {

  private final ImageGeneration imageGeneration;

  public ImageGenerationController(ImageGeneration imageGeneration) {
    this.imageGeneration = imageGeneration;
  }

  @GetMapping("/generate")
  public void  generateImage(
          HttpServletResponse response,
          @Valid ImageRequest imageRequest
          ) throws IOException {
    ImageResponse imageResponse = imageGeneration.generateImage(
            imageRequest.getPrompt(),
            imageRequest.getWidth(),
            imageRequest.getHeight()
    );

    String base64 = imageResponse.getResult().getOutput().getB64Json();

    byte[] imageBytes = Base64.getDecoder().decode(base64);

    response.setContentType("image/png");
    response.setHeader("Content-Disposition", "inline; filename=\"image.png\"");
    response.getOutputStream().write(imageBytes);
    response.getOutputStream().flush();
  }
}
