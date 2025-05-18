package com.example.agent_test_camp.services;

import com.example.agent_test_camp.output.Recipe;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class OutputService {

    private final ChatClient chatClient;

    public OutputService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                               You are a helpful cooking assistant. Respond clearly and include recipes when relevant.
                               If somebody ask about something else, just say you don't know.
                        """)
                .build();
    }

    public List<String> respondWithList(String ingredients, Integer amount) {
        final ListOutputConverter listOutputConverter = new ListOutputConverter(new DefaultConversionService());
        final String promptText = """
                Given the following ingredients: {ingredients}
                Suggest a list of {amount} dishes I could make. Respond in this format: {format}
                """;

        final Prompt prompt = new PromptTemplate(
                promptText,
                Map.of(
                        "ingredients", ingredients,
                        "amount", amount,
                        "format", listOutputConverter.getFormat()
                )
        ).create();

        return listOutputConverter.convert(
                Objects.requireNonNull(chatClient
                        .prompt(prompt)
                        .call()
                        .content())
        );
    }

    public Object respondPlain(String ingredients) {
        final Prompt prompt = new PromptTemplate(
                """
                        Given the following ingredients: {ingredients}
                        Suggest a recipe.
                        """,
                Map.of(
                        "ingredients", ingredients
                )
        ).create();
        return this.chatClient.prompt(prompt).call().content();
    }

    public Object respondWithJSON(String ingredients, Integer amount) {
        ParameterizedTypeReference<List<Recipe>> typeReference = new ParameterizedTypeReference<>() {};
        final BeanOutputConverter<List<Recipe>> beanOutputConverter = new BeanOutputConverter<>(typeReference);
        final String promptText = """
                Given the following ingredients: {ingredients}
                Suggest {amount} dishes I could make. Include the dish name, country of origin
                and the number of calories in the dish. Just say "I don't know" if you don't know 
                the answer. {format} 
                """;
        final Prompt prompt = new PromptTemplate(
                promptText,
                Map.of(
                        "ingredients", ingredients,
                        "amount", amount,
                        "format", beanOutputConverter.getFormat()
                )
        ).create();
        return beanOutputConverter.convert(
                Objects.requireNonNull(chatClient
                        .prompt(prompt)
                        .call()
                        .content())
        );
    }
}
