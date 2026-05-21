package com.example.agent_test_camp.translator.services;

import com.example.agent_test_camp.translator.dto.TranslationRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.template.st.StTemplateRenderer;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class TranslationService {

  private final OpenAiAudioSpeechModel speechModel;
  private final ChatClient translationClient;
  private final PromptTemplate translationTempate;

  public TranslationService(
      OpenAiAudioTranscriptionModel transcriptionModel,
      OpenAiAudioSpeechModel openAiAudioSpeechModel,
      ChatClient.Builder chatClientBuilder) {
    this.speechModel = openAiAudioSpeechModel;
    this.translationClient =
        chatClientBuilder
            .defaultSystem(
                """
                      You are a strict translator. You clearly and directly translate
                      the provided messages in the requested languages.
                      """)
            .defaultTemplateRenderer(
                StTemplateRenderer.builder()
                    .startDelimiterToken('{')
                    .endDelimiterToken('}')
                    .build())
            .build();
    this.translationTempate =
        PromptTemplate.builder()
            .template(
                """
                    Respond clearly with the direct translation of this message: '{message}' in '{language}' language, disregarding the meaning of the message.
                    If you don't know the language of the original message just say: I don't know the language of this message!.
                    If you don't know the language you are supposed to translate to, just say: I don't know this message!.
                    If the message is not provided then say: This message is empty!'
                    """)
            .build();
  }

  public void translate(String textResponse, TranslationRequest translationRequest) {
    final String prompt =
        translationTempate.render(
            Map.of("language", translationRequest.getLanguage(), "message", textResponse));
    final String textTranslation = translationClient.prompt(prompt).call().content();
    translationRequest.setTextTranslation(textTranslation);
    SpeechPrompt speechPrompt = new SpeechPrompt(textTranslation);
    translationRequest.setAudioTranslation(speechModel.call(speechPrompt).getResult().getOutput());
  }
}
