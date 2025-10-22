package de.htw.berlin.webtech.recipe_manager.web.dto;

import de.htw.berlin.webtech.recipe_manager.domain.DietTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Set;

/**
 * Eingabe-DTO zum ANLEGEN eines Rezepts.
 * "record" = kompakte, unveränderliche Datenträger-Klasse mit auto-Gettern.
 */
public record RecipeCreateDto(
        @NotBlank                 // Titel ist Pflicht (nicht null/leer/whitespace)
        String title,

        @Size(max = 2000)         // Beschreibung optional, max. 2000 Zeichen
        String description,

        @NotNull @PositiveOrZero  // Vorbereitungszeit >= 0 (null nicht erlaubt)
        Integer prepMinutes,

        @NotNull @PositiveOrZero  // Koch-/Backzeit >= 0 (null nicht erlaubt)
        Integer cookMinutes,

        @NotNull
        @Size(max = 20)           // höchstens 20 DietTags (praktisches Limit gegen Missbrauch)
        Set<DietTag> dietTags,

        @NotNull
        @Size(max = 20)           // höchstens 20 Kategorien
        Set<@NotBlank String> categories, // keine leeren Kategorien erlauben

        @NotNull
        @Size(max = 100)          // max. 100 Zutaten pro Rezept
        List<@Valid IngredientCreateDto> ingredients, // jede Zutat wird rekursiv validiert

        @NotNull
        @Size(max = 200)          // max. 200 Schritte
        List<@Valid StepCreateDto> steps              // jeder Schritt wird rekursiv validiert
) {}
