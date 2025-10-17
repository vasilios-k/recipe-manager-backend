package de.htw.berlin.webtech.recipe_manager.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record IngredientCreateDto(
        @NotBlank String name,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount,
        @NotBlank String unit
) {}
