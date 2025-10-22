package de.htw.berlin.webtech.recipe_manager.web.dto;

import de.htw.berlin.webtech.recipe_manager.domain.DietTag;
import java.util.List;
import java.util.Set;

/**
 * AUSGABE-DTO (Response) für ein Rezept.
 * Enthält Convenience-Felder, damit das Frontend weniger rechnen muss.
 */
public record RecipeReadDto(
        Long id,                           // DB-ID
        String title,                      // Titel
        String description,                // Beschreibung (optional)
        Integer prepMinutes,               // Vorbereitungszeit
        Integer cookMinutes,               // Koch-/Backzeit
        Integer totalMinutes,              // Convenience: prep + cook
        Set<DietTag> dietTags,             // alle gesetzten DietTags
        DietTag baselineTag,               // Convenience: der eine Baseline-Tag (falls vorhanden)
        Set<String> categories,            // Kategorien
        List<IngredientReadDto> ingredients, // Bereits gemappte Zutaten fürs UI
        List<StepReadDto> steps              // Bereits gemappte Schritte fürs UI
) {}
