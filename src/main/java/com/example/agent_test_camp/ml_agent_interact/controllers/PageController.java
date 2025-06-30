package com.example.agent_test_camp.ml_agent_interact.controllers;

import com.example.agent_test_camp.ml_agent_interact.configuration.AgentProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final AgentProperties agentProperties;

    public PageController(AgentProperties agentProperties) {
        this.agentProperties = agentProperties;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("width", agentProperties.getWidth());
        model.addAttribute("height", agentProperties.getHeight());
        return "index";
    }
}