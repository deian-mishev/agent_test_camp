package com.example.agent_test_camp.recipes_chat.services;

import com.example.agent_test_camp.recipes_chat.dto.Recipe;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RecipesOutput {

  private final RecipesChatClient recipesChatClient;
  private final PromptTemplate respondWithJSONTemplate;
  private final PromptTemplate respondWithListTemplate;
  private final PromptTemplate respondPlainTemplate;

  public RecipesOutput(RecipesChatClient recipesChatClient) {
    this.recipesChatClient = recipesChatClient;
    final StTemplateRenderer renderer =
        StTemplateRenderer.builder().startDelimiterToken('{').endDelimiterToken('}').build();

    this.respondWithJSONTemplate =
        PromptTemplate.builder()
            .renderer(renderer)
            .template(
                """
                    You are a helpful cooking assistant.
                    Given the following ingredients: {ingredients}
                    Suggest {amount} dishes I could make. Include the dish name, country of origin
                    and the number of calories in the dish. Just say "I don't know" if you don't know
                    the answer. Respond in this format: {format}
                    """)
            .build();
    this.respondWithListTemplate =
        PromptTemplate.builder()
            .renderer(renderer)
            .template(
                """
                You are a helpful cooking assistant.
                Given the following ingredients: {ingredients}
                Suggest a list of {amount} dishes I could make. Respond in this format: {format}
                """)
            .build();
    this.respondPlainTemplate =
        PromptTemplate.builder()
            .renderer(renderer)
            .template(
                """
                    Given the following ingredients: {ingredients}
                    Suggest a recipe.
                    """)
            .build();
  }

  public List<String> respondWithList(String ingredients, Integer amount) {
    final ListOutputConverter listOutputConverter =
        new ListOutputConverter(new DefaultConversionService());
    final String prompt =
        respondWithListTemplate.render(
            Map.of(
                "ingredients", ingredients,
                "amount", amount,
                "format", listOutputConverter.getFormat()));

    return listOutputConverter.convert(
        Objects.requireNonNull(recipesChatClient.getResponse(prompt)));
  }

  public Object respondPlain(String ingredients) {
    final String prompt = respondPlainTemplate.render(Map.of("ingredients", ingredients));
    return recipesChatClient.getResponse(prompt);
  }

  public Object respondWithJSON(String ingredients, Integer amount) {
    ParameterizedTypeReference<List<Recipe>> typeReference = new ParameterizedTypeReference<>() {};
    final BeanOutputConverter<List<Recipe>> beanOutputConverter =
        new BeanOutputConverter<>(typeReference);
    final String prompt =
        respondWithJSONTemplate.render(
            Map.of(
                "ingredients", ingredients,
                "amount", amount,
                "format", beanOutputConverter.getFormat()));
    return beanOutputConverter.convert(
        Objects.requireNonNull(recipesChatClient.getResponse(prompt)));
  }
}
