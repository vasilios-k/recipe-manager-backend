package de.htw.berlin.webtech.recipe_manager.web.dto;

import de.htw.berlin.webtech.recipe_manager.domain.DietTag;

import java.util.List;
import java.util.Set;

public record RecipeReadDto(
        Long id,
        String title,
        String description,
        Integer prepMinutes,
        Integer cookMinutes,
        Integer totalMinutes,          // Convenience
        Set<DietTag> dietTags,
        DietTag baselineTag,           // Convenience
        Set<String> categories,
        List<IngredientReadDto> ingredients,
        List<StepReadDto> steps
) {}
