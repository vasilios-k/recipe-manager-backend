package de.htw.berlin.webtech.recipe_manager.web.dto;

import de.htw.berlin.webtech.recipe_manager.domain.DietTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Set;

/**
 * Vollständiges Update eines Rezepts (inkl. Zutaten und Schritten).
 * Typischerweise für PUT, wenn der komplette Zustand ersetzt werden soll.
 */
public record RecipeUpdateFullDto(
        @NotBlank String title,
        @Size(max = 2000) String description,
        @NotNull @PositiveOrZero Integer prepMinutes,
        @NotNull @PositiveOrZero Integer cookMinutes,

        @NotNull @Size(max = 20)
        Set<DietTag> dietTags,

        @NotNull @Size(max = 20)
        Set<@NotBlank String> categories,

        @NotNull @Size(max = 100)
        List<@Valid IngredientCreateDto> ingredients, // rekursiv validieren

        @NotNull @Size(max = 200)
        List<@Valid StepCreateDto> steps              // rekursiv validieren
) {}
