package com.example.agent_test_camp.model_garden.controllers;

import com.example.agent_test_camp.model_garden.services.ClassificationService;
import com.example.agent_test_camp.model_garden.services.ImageProcessor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@RestController
public class ClassificationController {

  private final ClassificationService classificationService;
  private final ImageProcessor imageProcessor;

  public ClassificationController(
      ClassificationService classificationService, ImageProcessor imageProcessor) {
    this.classificationService = classificationService;
    this.imageProcessor = imageProcessor;
  }

  @PostMapping(value = "/classify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> classifyImage(@RequestParam("image") MultipartFile imageFile,
                                         @RequestParam("topK") Integer topK)
      throws IOException {
    BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());
//    float[][][][] rawBytes = imageProcessor.preprocess(bufferedImage);
    float[][][][] rawBytes = imageProcessor.preprocess(bufferedImage, 224, 224);
    Map<String, Float> result = classificationService.classify(rawBytes, topK);
    return ResponseEntity.ok(result);
  }
}
