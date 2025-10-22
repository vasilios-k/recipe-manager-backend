package de.htw.berlin.webtech.recipe_manager.web.dto;

/**
 * Ausgabe-DTO für einen Arbeitsschritt.
 */
public record StepReadDto(
        Long id,         // DB-ID
        Integer position,// 1,2,3,...
        String text      // Beschreibung des Schritts
) {}
