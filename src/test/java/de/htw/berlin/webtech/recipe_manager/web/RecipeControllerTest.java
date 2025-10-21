package de.htw.berlin.webtech.recipe_manager.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htw.berlin.webtech.recipe_manager.domain.DietTag;
import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import de.htw.berlin.webtech.recipe_manager.service.RecipeService;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeUpdateDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
@Import(GlobalExceptionHandler.class)
class RecipeControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean
    RecipeService service;

    @Test
    void create_returns201_withId() throws Exception {
        var entity = new Recipe();
        setId(entity, 42L);
        Mockito.when(service.create(any())).thenReturn(entity);

        var dto = new RecipeCreateDto(
                "T", "", 0, 0,
                Set.of(DietTag.VEGAN),
                Set.of("x"),
                List.of(),
                List.of()
        );

        mvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void put_base_returns204() throws Exception {
        var dto = new RecipeUpdateDto(
                "T", "", 0, 0,
                Set.of(DietTag.VEGAN),
                Set.of("x")
        );

        mvc.perform(put("/recipes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void replace_ingredients_returns204() throws Exception {
        var body = List.of(
                new de.htw.berlin.webtech.recipe_manager.web.dto.IngredientCreateDto("A",
                        new java.math.BigDecimal("1.0"), "g")
        );

        mvc.perform(put("/recipes/1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isNoContent());
    }

    @Test
    void two_baselines_returns400() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Maximal ein BASELINE-Tag erlaubt."))
                .when(service).create(any());

        var dto = new RecipeCreateDto(
                "T", "", 0, 0,
                Set.of(DietTag.VEGAN, DietTag.VEGETARIAN),
                Set.of("x"),
                List.of(),
                List.of()
        );

        mvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void not_found_returns404() throws Exception {
        Mockito.when(service.findOne(999L))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "not found"));

        mvc.perform(get("/recipes/999"))
                .andExpect(status().isNotFound());
    }

    private static void setId(Recipe r, Long id) {
        try {
            Field f = Recipe.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(r, id);
        } catch (Exception ignored) {}
    }
}
