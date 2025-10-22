package de.htw.berlin.webtech.recipe_manager.repo;

import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import org.springframework.data.domain.Page;      // Seite (mit Inhalt + Metadaten)
import org.springframework.data.domain.Pageable; // Seitierungs-/Sortier-Parameter aus dem Request
import org.springframework.data.jpa.repository.*; // JpaRepository & @Query
import org.springframework.data.repository.query.Param; // @Param für benannte Query-Parameter

/**
 * Datenzugriffsschicht (Repository) für Recipe.
 * Erbt Standard-CRUD (findById, save, delete, ...) von JpaRepository.
 */
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * Volltext-ähnliche Suche über Titel, Beschreibung und Kategorien.
     * - DISTINCT: verhindert Dubletten, falls durch Join mehrere Kategorien matchen.
     * - LEFT JOIN r.categories: auch Rezepte ohne Kategorien bleiben sichtbar.
     * - lower(...) + like: case-insensitive Suche, '%q%' (enthält).
     * - Pageable: ermöglicht page/size/sort direkt aus dem Controller.
     */
    @Query("""
      select distinct r from Recipe r
      left join r.categories c
      where lower(r.title) like lower(concat('%', :q, '%'))
         or lower(r.description) like lower(concat('%', :q, '%'))
         or lower(c) like lower(concat('%', :q, '%'))
    """)// es wird geguckt ob die Eingabe mit title, description oder category matcht. falls ein Rezept mehrere z.b Kategorien hat die matchen wird das Rezept nur 1 mal angezeigt
    Page<Recipe> search(@Param("q") String q, Pageable pageable);
}
