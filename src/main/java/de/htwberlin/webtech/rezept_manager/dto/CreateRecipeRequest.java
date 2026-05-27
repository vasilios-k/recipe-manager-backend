package de.htwberlin.webtech.rezept_manager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateRecipeRequest(
        @NotBlank
        String title,

        @NotBlank
        String category,

        @Min(1)
        int prepTimeMinutes
) {
}