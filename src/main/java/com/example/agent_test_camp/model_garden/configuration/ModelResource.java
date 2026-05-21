package com.example.agent_test_camp.model_garden.configuration;

import org.tensorflow.SavedModelBundle;

import java.util.List;

public record ModelResource (SavedModelBundle savedModelBundle, List<String> labels) { }
