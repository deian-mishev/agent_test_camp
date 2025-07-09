package com.example.agent_test_camp.model_garden.services;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
@Service
public class ImageProcessor {

  public float[][][][] preprocess(BufferedImage image, int height, int width) {
    BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = resized.createGraphics();
    g.drawImage(image, 0, 0, width, height, null);
    g.dispose();

    return normalizePixels(image, height, width);
  }

  public float[][][][] preprocess(BufferedImage image) {
    int height = image.getHeight();
    int width = image.getWidth();

    return normalizePixels(image, height, width);
  }

  private float[][][][] normalizePixels(BufferedImage image, int height, int width) {
    float[][][][] result = new float[1][height][width][3];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int rgb = image.getRGB(x, y);
        result[0][y][x][0] = ((rgb >> 16) & 0xFF) / 255.0f;
        result[0][y][x][1] = ((rgb >> 8) & 0xFF) / 255.0f;
        result[0][y][x][2] = (rgb & 0xFF) / 255.0f;
      }
    }
    return result;
  }
}
