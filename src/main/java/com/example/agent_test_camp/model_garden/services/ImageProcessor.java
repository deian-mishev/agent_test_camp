package com.example.agent_test_camp.model_garden.services;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageProcessor {

  public byte[] inputStreamToByteArray(InputStream input) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[4096];
    int read;
    while ((read = input.read(buffer)) != -1) {
      baos.write(buffer, 0, read);
    }
    return baos.toByteArray();
  }

  public float[][][][] preprocess(BufferedImage image) {
    int width = 128;
    int height = 128;
    BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = resized.createGraphics();
    g.drawImage(image, 0, 0, width, height, null);
    g.dispose();

    float[][][][] result = new float[1][height][width][3];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int rgb = resized.getRGB(x, y);
        result[0][y][x][0] = ((rgb >> 16) & 0xFF) / 255.0f; // R
        result[0][y][x][1] = ((rgb >> 8) & 0xFF) / 255.0f; // G
        result[0][y][x][2] = (rgb & 0xFF) / 255.0f; // B
      }
    }
    return result;
  }
}
