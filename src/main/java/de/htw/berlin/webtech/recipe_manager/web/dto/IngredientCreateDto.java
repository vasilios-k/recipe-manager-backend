package de.htw.berlin.webtech.recipe_manager.web.dto;

import de.htw.berlin.webtech.recipe_manager.domain.Unit;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Eingabe-DTO zum Anlegen einer Zutat.
 */
public record IngredientCreateDto(
        @NotBlank String name,                                             // Pflicht
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount, // > 0, präzise Dezimalzahl
        @NotNull Unit unit                                              // Pflicht, z. B. "G"
) {}
