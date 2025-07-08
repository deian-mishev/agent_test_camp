package com.example.agent_test_camp.model_garden.services;

import org.springframework.stereotype.Service;
import org.tensorflow.*;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.buffer.ByteDataBuffer;
import org.tensorflow.ndarray.index.Indices;
import org.tensorflow.types.TFloat32;

import java.util.Iterator;

@Service
public class ClassificationService {

  private final SavedModelBundle model;

  public ClassificationService(SavedModelBundle mobilenetModel) {
    this.model = mobilenetModel;
  }

  public float[] classify(ByteDataBuffer dataBuffer) {
    try (TFloat32 inputTensor = Tensor.of(TFloat32.class, Shape.of(1, 128, 128, 3), dataBuffer)) {

      try (TFloat32 result =
          (TFloat32)
              model
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
  }
}
