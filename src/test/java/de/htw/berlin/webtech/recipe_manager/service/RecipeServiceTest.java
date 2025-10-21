// tests/java/de/htw/berlin/webtech/recipe_manager/service/RecipeServiceTest.java
package de.htw.berlin.webtech.recipe_manager.service;

import de.htw.berlin.webtech.recipe_manager.domain.DietTag;
import de.htw.berlin.webtech.recipe_manager.domain.Ingredient;
import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import de.htw.berlin.webtech.recipe_manager.domain.Step;
import de.htw.berlin.webtech.recipe_manager.repo.RecipeRepository;
import de.htw.berlin.webtech.recipe_manager.web.dto.IngredientCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeUpdateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.StepCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.mapper.RecipeCreateMapper;
import de.htw.berlin.webtech.recipe_manager.web.mapper.RecipeReadMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecipeServiceTest {

    RecipeRepository repo;
    RecipeService svc;

    @BeforeEach
    void setup() {
        repo = mock(RecipeRepository.class);
        svc = new RecipeService(repo, new RecipeCreateMapper(), new RecipeReadMapper());
    }

    @Test
    void create_throws_when_two_baselines() {
        var dto = new RecipeCreateDto(
                "T", "", 0, 0,
                Set.of(DietTag.VEGAN, DietTag.VEGETARIAN),
                Set.of("x"),
                List.of(),
                List.of()
        );
        assertThrows(IllegalArgumentException.class, () -> svc.create(dto));
        verify(repo, never()).save(any());
    }

    @Test
    void updateBase_sets_fields_and_saves() {
        var r = new Recipe();
        setId(r, 1L);
        when(repo.findById(1L)).thenReturn(Optional.of(r));

        var dto = new RecipeUpdateDto(
                "Neu", "Desc", 5, 6,
                Set.of(DietTag.VEGAN),
                Set.of("cat")
        );

        svc.updateBase(1L, dto);

        assertEquals("Neu", r.getTitle());
        assertEquals(Integer.valueOf(5), r.getPrepMinutes());
        assertEquals(Integer.valueOf(6), r.getCookMinutes());
        assertTrue(r.getDietTags().contains(DietTag.VEGAN));
        verify(repo).save(r);
    }

    @Test
    void replaceIngredients_clears_and_sets_backrefs() {
        var r = new Recipe();
        setId(r, 1L);
        var old = new Ingredient(); old.setName("alt"); old.setRecipe(r);
        r.getIngredients().add(old);
        when(repo.findById(1L)).thenReturn(Optional.of(r));

        var list = List.of(new IngredientCreateDto("neu", new BigDecimal("1.0"), "g"));
        svc.replaceIngredients(1L, list);

        assertEquals(1, r.getIngredients().size());
        assertEquals("neu", r.getIngredients().get(0).getName());
        assertSame(r, r.getIngredients().get(0).getRecipe());
        verify(repo).save(r);
    }

    @Test
    void replaceSteps_clears_and_sorts_by_position() {
        var r = new Recipe();
        setId(r, 1L);
        var sOld = new Step(); sOld.setPosition(99); sOld.setRecipe(r);
        r.getSteps().add(sOld);
        when(repo.findById(1L)).thenReturn(Optional.of(r));

        var list = List.of(
                new StepCreateDto(2, "two"),
                new StepCreateDto(1, "one")
        );

        svc.replaceSteps(1L, list);

        assertEquals(2, r.getSteps().size());
        assertEquals(Integer.valueOf(1), r.getSteps().get(0).getPosition());
        assertEquals(Integer.valueOf(2), r.getSteps().get(1).getPosition());
        assertSame(r, r.getSteps().get(0).getRecipe());
        verify(repo).save(r);
    }

    private static void setId(Recipe r, Long id) {
        try {
            var f = Recipe.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(r, id);
        } catch (Exception ignored) {}
    }
}
