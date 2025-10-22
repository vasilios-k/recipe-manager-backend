package de.htw.berlin.webtech.recipe_manager.web.mapper;

import de.htw.berlin.webtech.recipe_manager.domain.Ingredient;
import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import de.htw.berlin.webtech.recipe_manager.domain.Step;
import de.htw.berlin.webtech.recipe_manager.web.dto.IngredientReadDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeReadDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.StepReadDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper: wandelt eine Recipe-Entität in das Ausgabe-DTO RecipeReadDto um.
 * Befüllt auch Convenience-Felder (totalMinutes, baselineTag) und mappt die Kinderlisten.
 */
@Component
public class RecipeReadMapper {

    public RecipeReadDto toDto(Recipe r) {
        // Zutaten -> DTOs
        List<IngredientReadDto> ingredients = r.getIngredients().stream()
                .map(this::toDto)
                .toList();

        // Schritte -> DTOs
        List<StepReadDto> steps = r.getSteps().stream()
                .map(this::toDto)
                .toList();

        // Hauptobjekt mit Convenience-Feldern
        return new RecipeReadDto(
                r.getId(),
                r.getTitle(),
                r.getDescription(),
                r.getPrepMinutes(),
                r.getCookMinutes(),
                r.getTotalMinutes(),               // Convenience: prep + cook
                r.getDietTags(),
                r.getBaselineTag().orElse(null),   // Convenience: der eine Baseline-Tag
                r.getCategories(),
                ingredients,
                steps
        );
    }

    // Ingredient -> ReadDto
    private IngredientReadDto toDto(Ingredient i) {
        return new IngredientReadDto(
                i.getId(),
                i.getName(),
                i.getAmount(),
                i.getUnit()
        );
    }

    // Step -> ReadDto
    private StepReadDto toDto(Step s) {
        return new StepReadDto(
                s.getId(),
                s.getPosition(),
                s.getText()
        );
    }
}
