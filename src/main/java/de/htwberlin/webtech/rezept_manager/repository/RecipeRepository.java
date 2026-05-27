package de.htwberlin.webtech.rezept_manager.repository;

import de.htwberlin.webtech.rezept_manager.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}