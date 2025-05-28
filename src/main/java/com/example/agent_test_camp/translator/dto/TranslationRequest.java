package com.example.agent_test_camp.translator.dto;

import jakarta.validation.constraints.NotBlank;

public class TranslationRequest {
  @NotBlank(message = "Required translation language must be provided.")
  private String language;

  private String textTranslation;

  private byte[] audioTranslation;

  public byte[] getAudioTranslation() {
    return audioTranslation;
  }

  public void setAudioTranslation(byte[] audioTranslation) {
    this.audioTranslation = audioTranslation;
  }

  public String getTextTranslation() {
    return textTranslation;
  }

  public void setTextTranslation(String textTranslation) {
    this.textTranslation = textTranslation;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}
