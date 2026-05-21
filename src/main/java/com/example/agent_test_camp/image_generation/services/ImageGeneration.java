package com.example.agent_test_camp.image_generation.services;

import com.example.agent_test_camp.image_generation.dto.ImageRefRequest;
import com.example.agent_test_camp.image_generation.dto.ImageRefResponse;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageGeneration {

    private final OpenAiImageModel openAiImageModel;
    private final RestClient restClient;
    private final String apiKey;

    public ImageGeneration(OpenAiImageModel openAiImageModel,
                           RestClient.Builder restClientBuilder,
                           @Value("${spring.ai.openai.api-key}") String apiKey) {
        this.openAiImageModel = openAiImageModel;
        this.restClient = restClientBuilder.baseUrl("https://api.openai.com").build();
        this.apiKey = apiKey;
    }

    public ImageResponse generateImage(String prompt, int width, int height) {
        return openAiImageModel.call(
            new ImagePrompt(prompt, OpenAiImageOptions.builder().height(height).width(width).build()));
    }

    public ImageRefResponse generateImagesByRef(List<MultipartFile> refs, ImageRefRequest req) throws IOException {
        List<BufferedImage> srcImages = new ArrayList<>(refs.size());
        for (MultipartFile ref : refs) {
            if (ref.isEmpty()) continue;
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(ref.getBytes()));
            if (img == null) throw new IOException("Cannot read image '" + ref.getOriginalFilename() + "' — unsupported format");
            srcImages.add(img);
        }
        if (srcImages.isEmpty()) throw new IOException("No valid images provided");

        // Canvas = max width × max height across all uploads; each image is scaled to fit
        // with transparent padding so no image is distorted
        int targetW = srcImages.stream().mapToInt(BufferedImage::getWidth).max().orElseThrow();
        int targetH = srcImages.stream().mapToInt(BufferedImage::getHeight).max().orElseThrow();

        HttpHeaders pngHeaders = new HttpHeaders();
        pngHeaders.setContentType(MediaType.IMAGE_PNG);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (int i = 0; i < srcImages.size(); i++) {
            byte[] normalized = padToCanvas(srcImages.get(i), targetW, targetH);
            final String filename = "image_" + i + ".png";
            body.add("image[]", new HttpEntity<>(new ByteArrayResource(normalized) {
                @Override
                public String getFilename() { return filename; }
            }, pngHeaders));
        }

        body.add("model", req.getModel());
        body.add("prompt", req.getPrompt());
        body.add("n", String.valueOf(req.getN()));
        body.add("size", "auto");
        if (req.getQuality() != null)        body.add("quality", req.getQuality());
        if (req.getUser() != null)           body.add("user", req.getUser());
        if (req.getResponseFormat() != null) body.add("response_format", req.getResponseFormat());

        OpenAiEditResponse response = restClient.post()
                .uri("/v1/images/edits")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(OpenAiEditResponse.class);

        List<String> images = response.data().stream()
                .map(d -> d.b64Json() != null ? d.b64Json() : d.url())
                .toList();

        return new ImageRefResponse(images);
    }

    // Place the image at native resolution, centered on the canvas — no scaling, no resampling
    private byte[] padToCanvas(BufferedImage src, int targetW, int targetH) throws IOException {
        int offsetX = (targetW - src.getWidth()) / 2;
        int offsetY = (targetH - src.getHeight()) / 2;

        BufferedImage canvas = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        g.drawImage(src, offsetX, offsetY, null);
        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(canvas, "png", out);
        return out.toByteArray();
    }

    private record OpenAiEditResponse(List<ImageData> data) {}

    private record ImageData(@JsonProperty("b64_json") String b64Json, String url) {}
}
