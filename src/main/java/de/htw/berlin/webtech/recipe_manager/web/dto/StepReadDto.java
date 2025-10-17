package de.htw.berlin.webtech.recipe_manager.web.dto;

public record StepReadDto(
        Long id,
        Integer position,
        String text
) {}
