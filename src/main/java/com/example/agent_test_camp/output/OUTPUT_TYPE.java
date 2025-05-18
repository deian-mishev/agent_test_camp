package com.example.agent_test_camp.output;

public enum OUTPUT_TYPE {
    PLAIN,
    LIST,
    JSON;

    @Override
    public String toString() {
        return name();
    }
}
