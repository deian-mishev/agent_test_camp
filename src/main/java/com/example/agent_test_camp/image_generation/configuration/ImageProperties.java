package com.example.agent_test_camp.image_generation.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@ConfigurationProperties(prefix = "project.properties.images")
public class ImageProperties {
  private Set<Integer> allowedSizes;
  private Boolean uniformSize;

  public Boolean getUniformSize() {
    return uniformSize;
  }

  public void setUniformSize(Boolean uniformSize) {
    this.uniformSize = uniformSize;
  }

  public Set<Integer> getAllowedSizes() {
    return allowedSizes;
  }

  public void setAllowedSizes(Set<Integer> allowedSizes) {
    this.allowedSizes = allowedSizes;
  }
}
