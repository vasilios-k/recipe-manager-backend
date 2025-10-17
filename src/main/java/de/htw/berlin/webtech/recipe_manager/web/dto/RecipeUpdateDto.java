package de.htw.berlin.webtech.recipe_manager.web.dto;

import de.htw.berlin.webtech.recipe_manager.domain.DietTag;
import jakarta.validation.constraints.*;

import java.util.Set;

public record RecipeUpdateDto(
        @NotBlank String title,
        @Size(max = 2000) String description,
        @NotNull @PositiveOrZero Integer prepMinutes,
        @NotNull @PositiveOrZero Integer cookMinutes,
        @NotNull @Size(max = 20) Set<DietTag> dietTags,
        @NotNull @Size(max = 20) Set<@NotBlank String> categories
) {}
