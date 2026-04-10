package de.htwberlin.webtech.rezept_manager.service;

import de.htwberlin.webtech.rezept_manager.model.Recipe;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {
    public List<Recipe> getAllRecipes() {
        return List.of(
                new Recipe(1L, "Spaghetti Bolognese", "Dinner", 30),
                new Recipe(2L, "Pancakes", "Breakfast", 20),
                new Recipe(3L, "Caesar Salad", "Lunch", 15)
        );
    }
}
