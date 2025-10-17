package de.htw.berlin.webtech.recipe_manager.web.mapper;

import de.htw.berlin.webtech.recipe_manager.domain.Ingredient;
import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import de.htw.berlin.webtech.recipe_manager.domain.Step;
import de.htw.berlin.webtech.recipe_manager.web.dto.IngredientCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.StepCreateDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class RecipeCreateMapper {

    public Recipe toEntity(RecipeCreateDto dto) {
        Recipe r = new Recipe();
        r.setTitle(nvl(dto.title()));
        r.setDescription(dto.description());
        r.setPrepMinutes(nvl(dto.prepMinutes()));
        r.setCookMinutes(nvl(dto.cookMinutes()));
        if (dto.dietTags() != null) r.setDietTags(dto.dietTags());
        if (dto.categories() != null) r.setCategories(dto.categories());

        // Ingredients (mit Backref)
        var ings = new ArrayList<Ingredient>();
        if (dto.ingredients() != null) {
            for (IngredientCreateDto d : dto.ingredients()) {
                Ingredient i = new Ingredient();
                i.setName(nvl(d.name()));
                i.setAmount(d.amount());   // Ingredient.amount = BigDecimal
                i.setUnit(d.unit());
                i.setRecipe(r);
                ings.add(i);
            }
        }
        r.setIngredients(ings);

        // Steps (mit Backref)
        var steps = new ArrayList<Step>();
        if (dto.steps() != null) {
            for (StepCreateDto d : dto.steps()) {
                Step s = new Step();
                s.setPosition(d.position() != null ? d.position() : 0);
                s.setText(d.text());
                s.setRecipe(r);
                steps.add(s);
            }
        }
        r.setSteps(steps);

        return r;
    }

    private String nvl(String s) { return s == null ? "" : s; }
    private int nvl(Integer i) { return i == null ? 0 : i; }
}
