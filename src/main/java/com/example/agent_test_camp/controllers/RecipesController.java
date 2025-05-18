package com.example.agent_test_camp.controllers;

import com.example.agent_test_camp.output.OUTPUT_TYPE;
import com.example.agent_test_camp.services.OutputService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipes")
public class RecipesController {

    private final OutputService outputService;

    public RecipesController(OutputService outputService) {
        this.outputService = outputService;
    }

    @GetMapping("/suggest-recipe")
    public ResponseEntity<Object> suggestRecipe(
            @RequestParam(name = "output", defaultValue = "PLAIN") OUTPUT_TYPE outputType,
            @RequestParam(name = "amount", defaultValue = "10") Integer amount,
            @RequestParam(name = "ingredients", required = false) String ingredients) {
        if (ingredients == null || ingredients.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("The 'ingredients' parameter is required.");
        }
        return ResponseEntity.ok(
                switch (outputType) {
                    case JSON -> outputService.respondWithJSON(ingredients, amount);
                    case LIST -> outputService.respondWithList(ingredients, amount);
                    default -> outputService.respondPlain(ingredients);
                }
        );
    }
}
