package de.htw.berlin.webtech.recipe_manager.web.dto;

import java.math.BigDecimal;

/**
 * Ausgabe-DTO für eine Zutat (so geht sie ans Frontend zurück).
 */
public record IngredientReadDto(
        Long id,            // DB-ID
        String name,        // Name
        BigDecimal amount,  // Menge
        String unit         // Einheit (als String; ggf. später Enum-Name)
) {}
