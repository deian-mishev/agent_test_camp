package com.example.agent_test_camp.image_generation.controllers;

import com.example.agent_test_camp.image_generation.dto.ImageRefRequest;
import com.example.agent_test_camp.image_generation.dto.ImageRefResponse;
import com.example.agent_test_camp.image_generation.dto.ImageRequest;
import com.example.agent_test_camp.image_generation.services.ImageGeneration;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.ai.image.ImageResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
            @Valid ImageRequest imageRequest
    ) throws IOException {
        ImageResponse imageResponse = imageGeneration.generateImage(
                imageRequest.getPrompt(),
                imageRequest.getWidth(),
                imageRequest.getHeight()
        );
        response.sendRedirect(imageResponse.getResult().getOutput().getUrl());
    }

    @PostMapping(value = "/generate-by-ref", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageRefResponse> generateByRef(
            @RequestPart("images") List<MultipartFile> images,
            @Valid @ModelAttribute ImageRefRequest request
    ) throws IOException {
        return ResponseEntity.ok(imageGeneration.generateImagesByRef(images, request));
    }
}
