package de.htw.berlin.webtech.recipe_manager.service;

import de.htw.berlin.webtech.recipe_manager.domain.Ingredient;
import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import de.htw.berlin.webtech.recipe_manager.domain.Step;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RecipeService {


    private final List<Recipe> seed = List.of(
            new Recipe(
                    1L, "Tomato Pasta", 15, true, false,
                    List.of(
                            new Ingredient("Spaghetti", new BigDecimal("200"), "g"),
                            new Ingredient("Tomaten", new BigDecimal("3"), "pcs"),
                            new Ingredient("Knoblauch", new BigDecimal("1"), "clove")
                    ),
                    List.of(
                            new Step(1, "Wasser salzen und zum Kochen bringen."),
                            new Step(2, "Spaghetti al dente kochen."),
                            new Step(3, "Tomaten & Knoblauch anschwitzen, Pasta unterheben.")
                    )
            ),
            new Recipe(
                    2L, "Pancakes", 20, false, true,
                    List.of(
                            new Ingredient("Mehl", new BigDecimal("200"), "g"),
                            new Ingredient("Milch", new BigDecimal("300"), "ml"),
                            new Ingredient("Ei", new BigDecimal("2"), "pcs")
                    ),
                    List.of(
                            new Step(1, "Teig anrühren."),
                            new Step(2, "In der Pfanne portionsweise ausbacken.")
                    )
            )
    );

    public List<Recipe> findAll() {
        return seed;
    }
}
