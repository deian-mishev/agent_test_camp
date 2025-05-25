package com.example.agent_test_camp.chat.output;

public enum OUTPUT_TYPE {
    PLAIN,
    LIST,
    JSON;

    @Override
    public String toString() {
        return name();
    }
}
