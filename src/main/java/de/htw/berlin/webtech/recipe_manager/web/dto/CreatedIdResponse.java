package de.htw.berlin.webtech.recipe_manager.web.dto;

/**
 * Einfache Antwort, wenn etwas neu erstellt wurde.
 * Enthält nur die neue Datenbank-ID (z. B. nach POST /recipes).
 */
public record CreatedIdResponse(Long id) {}
