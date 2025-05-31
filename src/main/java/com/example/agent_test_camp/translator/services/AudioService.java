package com.example.agent_test_camp.translator.services;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;

@Service
public class AudioService {
  private static String lastRecordedMessage;
  private static final AudioFormat AUDIO_FORMAT = new AudioFormat(16000, 16, 1, true, true);

  private final OpenAiAudioTranscriptionModel transcriptionModel;
  private TargetDataLine line;
  private boolean recording = false;

  public AudioService(OpenAiAudioTranscriptionModel transcriptionModel) {
    this.transcriptionModel = transcriptionModel;
  }

  @Async
  public CompletableFuture<String> recordAndTranscript(int maxDurationInSeconds) {
    if (recording) {
      return CompletableFuture.failedFuture(new RuntimeException("Another recording in progress"));
    }

    recording = true;
    try {
      DataLine.Info info = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT);
      if (!AudioSystem.isLineSupported(info)) {
        recording = false;
        return CompletableFuture.failedFuture(new RuntimeException("No audio system connected"));
      }

      line = (TargetDataLine) AudioSystem.getLine(info);
      line.open(AUDIO_FORMAT);
      line.start();

      ByteArrayOutputStream rawAudio = new ByteArrayOutputStream();
      byte[] buffer = new byte[4096];
      long endTime = System.currentTimeMillis() + maxDurationInSeconds * 1000L;

      System.out.println("Recording started...");

      while (recording && System.currentTimeMillis() < endTime) {
        int bytesRead = line.read(buffer, 0, buffer.length);
        if (bytesRead > 0) {
          rawAudio.write(buffer, 0, bytesRead);
        }
      }

      stopRecording();
      byte[] rawBytes = rawAudio.toByteArray();

      AudioInputStream ais =
          new AudioInputStream(
              new ByteArrayInputStream(rawBytes),
              AUDIO_FORMAT,
              rawBytes.length / AUDIO_FORMAT.getFrameSize());

      ByteArrayOutputStream audioData = new ByteArrayOutputStream();
      AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioData);

      OpenAiAudioTranscriptionOptions transcriptionOptions =
          OpenAiAudioTranscriptionOptions.builder().build();

      AudioTranscriptionPrompt transcriptionRequest =
          new AudioTranscriptionPrompt(
              new InputStreamResource(new ByteArrayInputStream(audioData.toByteArray())),
              transcriptionOptions);

      AudioTranscriptionResponse response = transcriptionModel.call(transcriptionRequest);
      lastRecordedMessage = response.getResult().getOutput().replaceAll("[\\r\\n]+", "");
      return CompletableFuture.completedFuture(lastRecordedMessage);
    } catch (Exception e) {
      recording = false;
      return CompletableFuture.failedFuture(e);
    }
  }

  public void playTranslation(byte[] audioData) {
    if (audioData == null) {
      return;
    }

    try (ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(bais)) {
      AudioFormat format = audioStream.getFormat();
      DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

      try (SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info)) {
        audioLine.open(format);
        audioLine.start();

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = audioStream.read(buffer, 0, buffer.length)) != -1) {
          audioLine.write(buffer, 0, bytesRead);
        }

        audioLine.drain();
      }

    } catch (UnsupportedAudioFileException e) {
      System.err.println("Unsupported format: " + e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void stopRecording() {
    if (recording && line != null) {
      System.out.println("Stopping recording...");
      line.stop();
      line.close();
      recording = false;
    }
  }

  public String getLastRecordedMessage() {
    return lastRecordedMessage != null ? lastRecordedMessage : null;
  }
}
