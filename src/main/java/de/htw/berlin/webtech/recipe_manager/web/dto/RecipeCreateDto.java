package de.htw.berlin.webtech.recipe_manager.web.dto;

import de.htw.berlin.webtech.recipe_manager.domain.DietTag;
import java.util.List;
import java.util.Set;

public record RecipeCreateDto(
        String title,
        String description,
        Integer prepMinutes,
        Integer cookMinutes,
        Set<DietTag> dietTags,
        Set<String> categories,
        List<IngredientCreateDto> ingredients,
        List<StepCreateDto> steps
) {}
