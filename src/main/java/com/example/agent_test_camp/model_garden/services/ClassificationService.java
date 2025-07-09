package com.example.agent_test_camp.model_garden.services;

import com.example.agent_test_camp.model_garden.configuration.ModelResource;
import org.springframework.stereotype.Service;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.index.Indices;
import org.tensorflow.types.TFloat32;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.stream.IntStream;

@Service
public class ClassificationService {

  private final ModelResource model;

  public ClassificationService(ModelResource mobilenetModel) {
    this.model = mobilenetModel;
  }

  public Map<String, Float> classify(float[][][][] rawBytes, int topK) {
    if (rawBytes.length != 1 || rawBytes[0][0][0].length != 3) {
      throw new IllegalArgumentException("Expected input shape: [1][h][w][3]");
    }
    int height = rawBytes[0].length;
    int width = rawBytes[0][0].length;

    FloatNdArray inputNdArray = NdArrays.ofFloats(Shape.of(1, height, width, 3));
    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        for (int c = 0; c < 3; c++) {
          inputNdArray.setFloat(rawBytes[0][h][w][c], 0, h, w, c);
        }
      }
    }

    try (TFloat32 inputTensor = TFloat32.tensorOf(inputNdArray)) {
      return getTopPredictions(infer(inputTensor), model.labels(), topK);
    }
  }

  public float[] infer(Tensor inputTensor) {
    try (TFloat32 result =
                 (TFloat32)
                         model.savedModelBundle()
                                 .session()
                                 .runner()
                                 .feed("serving_default_inputs", inputTensor)
                                 .fetch("StatefulPartitionedCall")
                                 .run()
                                 .get(0)) {

      float[] predictions = new float[(int) result.shape().get(1)];
      FloatNdArray slice = result.slice(Indices.at(0));
      slice.copyTo(NdArrays.vectorOf(predictions));

      return predictions;
    }
  }

  public Map<String, Float> getTopPredictions(float[] predictions, List<String> labels, int topK) {
    return IntStream.range(0, predictions.length)
            .boxed()
            .sorted((i, j) -> Float.compare(predictions[j], predictions[i]))
            .limit(topK)
            .collect(
                    LinkedHashMap::new,
                    (map, i) -> map.put(labels.get(i), predictions[i]),
                    Map::putAll
            );
  }
}
