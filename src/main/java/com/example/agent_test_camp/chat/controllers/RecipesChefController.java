package com.example.agent_test_camp.chat.controllers;

import com.example.agent_test_camp.chat.services.RecipesChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/recipes/chef")
public class RecipesChefController {

    private final RecipesChatClient chatClient;
    private final List<Message> conversation;

    public RecipesChefController(RecipesChatClient chatClient) {
        this.conversation = new ArrayList<>() {{
                add(new SystemMessage("""
                        You are a chef cooking assistant.
                        Respond clearly and include recipes when relevant.
                        If somebody ask about something else, just say you don't know"""));
        }};
        this.chatClient = chatClient;
    }

    @GetMapping("/suggest-recipe")
    public String suggestRecipe(@RequestParam(
            name = "message",
            defaultValue = "Suggest a recipe for dinner."
    ) String message) {
        this.conversation.add(new UserMessage(message));
        final String response = chatClient.getResponse(this.conversation);
        this.conversation.add(new AssistantMessage(response));
        return response;
    }
}
