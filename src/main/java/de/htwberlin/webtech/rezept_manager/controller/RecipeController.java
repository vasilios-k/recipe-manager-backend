package de.htwberlin.webtech.rezept_manager.controller;

import de.htwberlin.webtech.rezept_manager.dto.CreateRecipeRequest;
import de.htwberlin.webtech.rezept_manager.model.Recipe;
import de.htwberlin.webtech.rezept_manager.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe createRecipe(@Valid @RequestBody CreateRecipeRequest request) {
        return recipeService.createRecipe(request);
    }
}