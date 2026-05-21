package com.example.agent_test_camp.model_garden.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "model-paths")
public class GardenProperties {

  private String objectRecognition;

  private String objectRecognitionV2;

  public void setObjectRecognitionV2(String objectRecognitionV2) {
    this.objectRecognitionV2 = objectRecognitionV2;
  }

  public String getObjectRecognitionV2() {
    return objectRecognitionV2;
  }

  public String getObjectRecognition() {
    return objectRecognition;
  }

  public void setObjectRecognition(String objectRecognition) {
    this.objectRecognition = objectRecognition;
  }
}
