package de.htw.berlin.webtech.recipe_manager.web.dto;

import java.math.BigDecimal;

public record IngredientCreateDto(
        String name,
        BigDecimal amount,
        String unit
) {}
