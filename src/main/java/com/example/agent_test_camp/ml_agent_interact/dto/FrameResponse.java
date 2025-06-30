package com.example.agent_test_camp.ml_agent_interact.dto;

public class FrameResponse {
    private String base64Frame;

    public FrameResponse(String base64Frame) {
        this.base64Frame = base64Frame;
    }

    public String getBase64Frame() { return base64Frame; }
}
