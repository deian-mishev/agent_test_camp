package com.example.agent_test_camp.config;

import com.example.agent_test_camp.model_garden.configuration.GardenProperties;
import com.example.agent_test_camp.model_garden.configuration.ModelResource;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tensorflow.SavedModelBundle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Configuration
@Profile("model_garden")
@ComponentScan(basePackages = "com.example.agent_test_camp.model_garden")
public class TFModelGardenConfig {

  @Bean
  public ModelResource mobilenetModel(GardenProperties properties)
      throws IOException, URISyntaxException {
    Path modelPath = extractModelFromResources(properties.getObjectRecognition());
    SavedModelBundle savedModelBundle = SavedModelBundle.load(modelPath.toString(), "serve");
    ObjectMapper mapper = new ObjectMapper();
    Path labelsPath = modelPath.resolve("labels.json");
    List<String> labels = mapper.readValue(labelsPath.toFile(), new TypeReference<>(){});
    return new ModelResource(savedModelBundle, labels);
  }

  private Path extractModelFromResources(String resourceDirName)
      throws IOException, URISyntaxException {
    ClassLoader classLoader = getClass().getClassLoader();
    URL resource = classLoader.getResource(resourceDirName);

    if (resource == null) {
      throw new FileNotFoundException("Resource directory not found: " + resourceDirName);
    }

    Path tempDir = Files.createTempDirectory("tf-model");
    Path resourcePath = Paths.get(resource.toURI());

    Files.walk(resourcePath)
        .forEach(
            source -> {
              try {
                Path dest = tempDir.resolve(resourcePath.relativize(source).toString());
                if (Files.isDirectory(source)) {
                  Files.createDirectories(dest);
                } else {
                  Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
                }
              } catch (IOException e) {
                throw new UncheckedIOException(e);
              }
            });

    return tempDir;
  }
}
