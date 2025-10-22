package de.htw.berlin.webtech.recipe_manager.web.mapper;

import de.htw.berlin.webtech.recipe_manager.domain.Ingredient;
import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import de.htw.berlin.webtech.recipe_manager.domain.Step;
import de.htw.berlin.webtech.recipe_manager.web.dto.IngredientCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.StepCreateDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Mapper: wandelt ein eingehendes RecipeCreateDto in die persistierbare Recipe-Entität um.
 * Setzt dabei auch die Backrefs (Kind -> Parent).
 */
@Component
public class RecipeCreateMapper {

    /**
     * DTO -> Entity.
     */
    public Recipe toEntity(RecipeCreateDto dto) {
        Recipe r = new Recipe();

        // Grunddaten (Null-sicher, aber: siehe Hinweise zu nvl(...) weiter unten)
        r.setTitle(dto.title());
        r.setDescription(dto.description());    // Beschreibung darf null sein
        r.setPrepMinutes(dto.prepMinutes());
        r.setCookMinutes(dto.cookMinutes());

        // Tags & Kategorien (nutzt die Set-Methoden der Entity -> Change Tracking + Baseline-Validierung)
        if (dto.dietTags() != null) r.setDietTags(dto.dietTags());
        if (dto.categories() != null) r.setCategories(dto.categories());

        // Ingredients (mit Backref auf r)
        var ings = new ArrayList<Ingredient>();
        if (dto.ingredients() != null) {
            for (IngredientCreateDto d : dto.ingredients()) {
                Ingredient i = new Ingredient();
                i.setName(d.name());
                i.setAmount(d.amount());     // BigDecimal
                i.setUnit(d.unit());         // aktuell String (Enum-Entscheidung am Ende)
                i.setRecipe(r);              // Backref: Kind zeigt auf Parent
                ings.add(i);
            }
        }
        r.setIngredients(ings); // Entity-Setter setzt Backrefs ebenfalls

        // Steps (mit Backref auf r)
        var steps = new ArrayList<Step>();
        if (dto.steps() != null) {
            for (StepCreateDto d : dto.steps()) {
                Step s = new Step();
                s.setPosition(d.position());  // DTO garantiert  >= 1
                s.setText(d.text());
                s.setRecipe(r);              // Backref
                steps.add(s);
            }
        }
        r.setSteps(steps);

        return r;
    }


}
