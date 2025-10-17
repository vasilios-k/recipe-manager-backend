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

@Component
public class RecipeReadMapper {

    public RecipeReadDto toDto(Recipe r) {
        List<IngredientReadDto> ingredients = r.getIngredients().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        List<StepReadDto> steps = r.getSteps().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new RecipeReadDto(
                r.getId(),
                r.getTitle(),
                r.getDescription(),
                r.getPrepMinutes(),
                r.getCookMinutes(),
                r.getTotalMinutes(),
                r.getDietTags(),
                r.getBaselineTag().orElse(null),
                r.getCategories(),
                ingredients,
                steps
        );
    }

    private IngredientReadDto toDto(Ingredient i) {
        return new IngredientReadDto(
                i.getId(),
                i.getName(),
                i.getAmount(),
                i.getUnit()
        );
    }

    private StepReadDto toDto(Step s) {
        return new StepReadDto(
                s.getId(),
                s.getPosition(),
                s.getText()
        );
    }
}
