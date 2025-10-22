package de.htw.berlin.webtech.recipe_manager.web.dto;

import de.htw.berlin.webtech.recipe_manager.domain.DietTag;
import jakarta.validation.constraints.*;
import java.util.Set;

/**
 * Eingabe-DTO zum AKTUALISIEREN eines Rezepts (PUT).
 * Enthält nur die skalaren Felder; Zutaten/Schritte werden meist getrennt behandelt.
 */
public record RecipeUpdateDto(
        @NotBlank              // Titel bleibt Pflicht
        String title,

        @Size(max = 2000)      // Beschreibung optional, max. 2000
        String description,

        @NotNull @PositiveOrZero // >= 0
        Integer prepMinutes,

        @NotNull @PositiveOrZero // >= 0
        Integer cookMinutes,

        @NotNull
        @Size(max = 20)        // max. 20 DietTags
        Set<DietTag> dietTags,

        @NotNull
        @Size(max = 20)        // max. 20 Kategorien
        Set<@NotBlank String> categories
) {}
