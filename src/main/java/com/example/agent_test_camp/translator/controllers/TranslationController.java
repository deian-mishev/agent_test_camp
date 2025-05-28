package com.example.agent_test_camp.translator.controllers;

import com.example.agent_test_camp.translator.dto.AudioRequest;
import com.example.agent_test_camp.translator.dto.TranslationRequest;
import com.example.agent_test_camp.translator.services.AudioService;
import com.example.agent_test_camp.translator.services.TranslationService;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/translation")
public class TranslationController {

  private final TranslationService translationService;
  private final AudioService audioService;

  public TranslationController(
      TranslationService translationService, AudioService audioRecorderService) {
    this.translationService = translationService;
    this.audioService = audioRecorderService;
  }

  @GetMapping("/translate")
  public ResponseEntity<String> translate(
      @Validated AudioRequest audioRequest, @Validated TranslationRequest translationRequest)
      throws ExecutionException, InterruptedException {
    String textResponse =
        audioService.recordAndTranscript(audioRequest.getDurationInSeconds()).get();
    if (textResponse == null) {
      return ResponseEntity.badRequest().body("No content found.");
    }
    translationService.translate(textResponse, translationRequest);
    audioService.playTranslation(translationRequest.getAudioTranslation());
    return ResponseEntity.ok(translationRequest.getTextTranslation());
  }

  @GetMapping("/record")
  public CompletableFuture<String> record(@Validated AudioRequest audioRequest) {
    return audioService.recordAndTranscript(audioRequest.getDurationInSeconds());
  }

  @GetMapping("/translate-last")
  public ResponseEntity<Object> translateLast(@Validated TranslationRequest translationRequest) {
    String textResponse = audioService.getLastRecordedMessage();
    if (textResponse == null) {
      return ResponseEntity.badRequest().body("No content found.");
    }
    translationService.translate(textResponse, translationRequest);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType("audio/wav"));
    headers.setContentDisposition(
        ContentDisposition.inline()
            .filename(
                String.format(
                    "%s_recording.wav",
                    translationRequest.getLanguage().replaceAll("[^a-zA-Z0-9]+", "_")))
            .build());
    headers.setContentLength(translationRequest.getAudioTranslation().length);
    return new ResponseEntity<>(translationRequest.getAudioTranslation(), headers, HttpStatus.OK);
  }
}
