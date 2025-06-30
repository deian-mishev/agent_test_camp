package com.example.agent_test_camp.ml_agent_interact.controllers;

import com.example.agent_test_camp.ml_agent_interact.configuration.AgentProperties;
import com.example.agent_test_camp.ml_agent_interact.dto.FrameResponse;
import com.example.agent_test_camp.ml_agent_interact.dto.InputAction;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Controller
public class AgentController {

    private final AgentProperties agentProperties;

    public AgentController(AgentProperties agentProperties) {
        this.agentProperties = agentProperties;
    }

    @MessageMapping("/input")
    @SendTo("/topic/frame")
    public FrameResponse handleInput(InputAction message) throws Exception {
        String keyInput = message.getKey();
        byte[] videoData = sendKeyToAgent(keyInput);
        String base64 = Base64.getEncoder().encodeToString(videoData);
        return new FrameResponse(base64);
    }

    private byte[] sendKeyToAgent(String keyInput) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(keyInput, headers);
        ResponseEntity<byte[]> response = new RestTemplate().postForEntity(agentProperties.getUrl()
                , entity, byte[].class);
        return response.getBody();
    }
}