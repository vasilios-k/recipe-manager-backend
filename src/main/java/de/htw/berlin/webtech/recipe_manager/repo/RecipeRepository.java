package de.htw.berlin.webtech.recipe_manager.repo;

import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("""
      select distinct r from Recipe r
      left join r.categories c
      where lower(r.title) like lower(concat('%', :q, '%'))
         or lower(r.description) like lower(concat('%', :q, '%'))
         or lower(c) like lower(concat('%', :q, '%'))
    """)
    Page<Recipe> search(@Param("q") String q, Pageable pageable);
}
