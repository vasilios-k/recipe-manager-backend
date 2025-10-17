package de.htw.berlin.webtech.recipe_manager.repo;

import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}