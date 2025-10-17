package de.htw.berlin.webtech.recipe_manager.web.dto;

import jakarta.validation.constraints.*;

public record StepCreateDto(
        @NotNull @PositiveOrZero Integer position,
        @NotBlank @Size(max = 2000) String text
) {}
