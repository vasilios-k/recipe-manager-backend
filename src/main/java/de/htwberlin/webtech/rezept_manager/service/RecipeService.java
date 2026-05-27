package de.htwberlin.webtech.rezept_manager.service;

import de.htwberlin.webtech.rezept_manager.model.Recipe;
import de.htwberlin.webtech.rezept_manager.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
}