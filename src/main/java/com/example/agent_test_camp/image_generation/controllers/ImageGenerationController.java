package com.example.agent_test_camp.image_generation.controllers;

import com.example.agent_test_camp.image_generation.dto.ImageRequest;
import com.example.agent_test_camp.image_generation.services.ImageGeneration;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @Valid ImageRequest imageRequest
    ) throws IOException {
        byte[] imageBytes = imageGeneration.generateImage(
                imageRequest.getPrompt(),
                imageRequest.getWidth(),
                imageRequest.getHeight()
        );
        writeImage(response, imageBytes);
    }

    @PostMapping(value = "/generate-by-ref", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void generateByRef(
            HttpServletResponse response,
            @RequestPart("image") MultipartFile image,
            @RequestParam String prompt
    ) throws IOException {
        writeImage(response, imageGeneration.generateImageByRef(image, prompt));
    }

    private void writeImage(HttpServletResponse response, byte[] imageBytes) throws IOException {
        response.setContentType("image/png");
        response.setHeader("Content-Disposition", "inline; filename=\"image.png\"");
        response.getOutputStream().write(imageBytes);
        response.getOutputStream().flush();
    }
}
