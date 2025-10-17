package de.htw.berlin.webtech.recipe_manager.web.dto;

import java.math.BigDecimal;

public record IngredientReadDto(
        Long id,
        String name,
        BigDecimal amount,
        String unit
) {}
