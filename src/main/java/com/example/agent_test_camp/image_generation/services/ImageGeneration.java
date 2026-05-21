package com.example.agent_test_camp.image_generation.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class ImageGeneration {

    private final OpenAiImageModel openAiImageModel;
    private final RestClient restClient;
    private final String apiKey;
    private final String imageModel;

    public ImageGeneration(OpenAiImageModel openAiImageModel,
                           RestClient.Builder restClientBuilder,
                           @Value("${spring.ai.openai.api-key}") String apiKey,
                           @Value("${spring.ai.openai.image.options.model}") String imageModel) {
        this.openAiImageModel = openAiImageModel;
        this.restClient = restClientBuilder.baseUrl("https://api.openai.com").build();
        this.apiKey = apiKey;
        this.imageModel = imageModel;
    }

    public byte[] generateImage(String prompt, int width, int height) {
        ImageResponse imageResponse = openAiImageModel.call(
            new ImagePrompt(prompt, OpenAiImageOptions.builder().height(height).width(width).build()));
        return Base64.getDecoder().decode(imageResponse.getResult().getOutput().getB64Json());
    }

    public byte[] generateImageByRef(MultipartFile ref, String prompt) throws IOException {
        if (ref.isEmpty()) throw new IOException("No image provided");

        HttpHeaders pngHeaders = new HttpHeaders();
        pngHeaders.setContentType(MediaType.IMAGE_PNG);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image[]", new HttpEntity<>(new ByteArrayResource(toPng(ref.getBytes())) {
            @Override
            public String getFilename() { return "image.png"; }
        }, pngHeaders));
        body.add("model", imageModel);
        body.add("prompt", prompt);
        body.add("n", "1");
        body.add("size", "auto");

        OpenAiEditResponse response = restClient.post()
                .uri("/v1/images/edits")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(OpenAiEditResponse.class);

        return Base64.getDecoder().decode(response.data().get(0).b64Json());
    }

    private byte[] toPng(byte[] bytes) throws IOException {
        BufferedImage src = ImageIO.read(new ByteArrayInputStream(bytes));
        if (src == null) throw new IOException("Cannot read image — unsupported format");
        BufferedImage canvas = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(canvas, "png", out);
        return out.toByteArray();
    }

    private record OpenAiEditResponse(List<ImageData> data) {}

    private record ImageData(@JsonProperty("b64_json") String b64Json) {}
}
