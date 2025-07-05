package com.example.agent_test_camp.ml_agent_interact.controllers;

import com.example.agent_test_camp.ml_agent_interact.configuration.ProjectProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
public class PageController {

  private final ProjectProperties projectProperties;

  public PageController(ProjectProperties projectProperties) {
    this.projectProperties = projectProperties;
  }

  @GetMapping(value = "/js/agent-client.js", produces = "application/javascript")
  public ResponseEntity<String> serveAgentClientJs() throws IOException {
    ClassPathResource jsFile = new ClassPathResource("templates/js/agent-client.js");
    String jsContent = StreamUtils.copyToString(jsFile.getInputStream(), StandardCharsets.UTF_8);
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/javascript")
            .body(jsContent);
  }

  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("width", projectProperties.getAgent().getWidth());
    model.addAttribute("height", projectProperties.getAgent().getHeight());
    return "index";
  }
}
