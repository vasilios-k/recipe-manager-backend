package de.htwberlin.webtech.recipe.manager.web;

import de.htwberlin.webtech.recipe.manager.web.api.Ingredient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.ArrayList;
import java.util.List;



public class IngredientRestController {

    private List<Ingredient> ingredient;

    public IngredientRestController(){
        ingredient = new ArrayList<>();
        ingredient.add(new Ingredient(1, "Apfel", 61, 0.3,0, 14, true));
    }

    @GetMapping(path = "/api/v1/ingredients")
    public ResponseEntity<List<Ingredient>> fetchIngredients(){
        return ResponseEntity.ok(ingredient);
    }
}
