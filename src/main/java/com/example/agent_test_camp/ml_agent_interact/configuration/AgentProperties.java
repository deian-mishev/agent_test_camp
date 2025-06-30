package com.example.agent_test_camp.ml_agent_interact.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "project.properties.agent")
public class AgentProperties {
  private String url;
  private int width;
  private int height;

  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }

  public int getWidth() { return width; }
  public void setWidth(int width) { this.width = width; }

  public int getHeight() { return height; }
  public void setHeight(int height) { this.height = height; }
}
