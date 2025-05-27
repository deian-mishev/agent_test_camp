package com.example.agent_test_camp.recipes_chat.enums;

public enum OUTPUT_TYPE {
    PLAIN,
    LIST,
    JSON;

    @Override
    public String toString() {
        return name();
    }
}
