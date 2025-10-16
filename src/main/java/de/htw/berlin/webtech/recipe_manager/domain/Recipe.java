package de.htw.berlin.webtech.recipe_manager.domain;

import java.util.List;

public class Recipe {

    private Long id;
    private String title;
    private int prepMinutes;
    private boolean vegan;
    private boolean noBaking;
    private List<Ingredient> ingredients;
    private List<Step> steps;

    protected Recipe() { }

    public Recipe(Long id, String title, int prepMinutes, boolean vegan, boolean noBaking,
                  List<Ingredient> ingredients, List<Step> steps) {
        this.id = id; this.title = title; this.prepMinutes = prepMinutes;
        this.vegan = vegan; this.noBaking = noBaking;
        this.ingredients = ingredients; this.steps = steps;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public int getPrepMinutes() { return prepMinutes; }
    public boolean isVegan() { return vegan; }
    public boolean isNoBaking() { return noBaking; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<Step> getSteps() { return steps; }
}
