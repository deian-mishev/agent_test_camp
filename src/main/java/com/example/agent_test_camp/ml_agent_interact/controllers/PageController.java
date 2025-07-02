package com.example.agent_test_camp.ml_agent_interact.controllers;

import com.example.agent_test_camp.ml_agent_interact.configuration.ProjectProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  private final ProjectProperties projectProperties;

  public PageController(ProjectProperties projectProperties) {
    this.projectProperties = projectProperties;
  }

  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("width", projectProperties.getAgent().getWidth());
    model.addAttribute("height", projectProperties.getAgent().getHeight());
    return "index";
  }
}
