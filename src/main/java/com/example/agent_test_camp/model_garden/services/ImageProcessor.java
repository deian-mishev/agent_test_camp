package com.example.agent_test_camp.model_garden.services;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
@Service
public class ImageProcessor {
  public BufferedImage resizeWithPadding(BufferedImage input, int targetWidth, int targetHeight) {
    float scale = Math.min(
            (float) targetWidth / input.getWidth(),
            (float) targetHeight / input.getHeight()
    );

    int newWidth = Math.round(input.getWidth() * scale);
    int newHeight = Math.round(input.getHeight() * scale);

    BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = resizedImage.createGraphics();

    g.setColor(Color.BLACK);
    g.fillRect(0, 0, targetWidth, targetHeight);

    int x = (targetWidth - newWidth) / 2;
    int y = (targetHeight - newHeight) / 2;
    g.drawImage(input, x, y, newWidth, newHeight, null);
    g.dispose();

    return resizedImage;
  }

  public float[][][][] preprocess(BufferedImage image, int height, int width) {
    BufferedImage padded = resizeWithPadding(image, 224, 224);
    return normalizePixels(padded, height, width);
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
