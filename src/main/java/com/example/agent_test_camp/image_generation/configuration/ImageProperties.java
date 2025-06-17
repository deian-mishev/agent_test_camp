package com.example.agent_test_camp.image_generation.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@ConfigurationProperties(prefix = "project.properties.images")
public class ImageProperties {
  private Set<Integer> allowedSizes;
  private Set<String> allowedQualities;
  private Set<String> allowedStyles;
  private Boolean uniformSize;

  public Set<String> getAllowedStyles() {
    return allowedStyles;
  }

  public void setAllowedStyles(Set<String> allowedStyles) {
    this.allowedStyles = allowedStyles;
  }

  public Set<String> getAllowedQualities() {
    return allowedQualities;
  }

  public void setAllowedQualities(Set<String> allowedQualities) {
    this.allowedQualities = allowedQualities;
  }

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
