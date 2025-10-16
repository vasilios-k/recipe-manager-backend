package de.htw.berlin.webtech.recipe_manager.web;

import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import de.htw.berlin.webtech.recipe_manager.service.RecipeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecipeController {

    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    // Milestone 1: Liste von Objekten (inkl. Ingredients & Steps)
    // http://localhost:8080/recipes
    @GetMapping("/recipes")
    public List<Recipe> list() {
        return service.findAll();
    }
}
