package de.htw.berlin.webtech.recipe_manager.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htw.berlin.webtech.recipe_manager.domain.DietTag;
import de.htw.berlin.webtech.recipe_manager.service.RecipeService;
import de.htw.berlin.webtech.recipe_manager.web.dto.IngredientReadDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeReadDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.StepReadDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
@Import(GlobalExceptionHandler.class)
class RecipePagingSearchTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean
    RecipeService service;

    private RecipeReadDto dto(long id, String title) {
        return new RecipeReadDto(
                id,
                title,
                "",
                1,
                2,
                3,
                Set.of(DietTag.VEGAN),
                DietTag.VEGAN,
                Set.of("cat"),
                List.of(new IngredientReadDto(1L, "X", java.math.BigDecimal.ONE, "g")),
                List.of(new StepReadDto(1L, 1, "step"))
        );
    }

    @Test
    void getAll_with_query_and_paging_returns_page_json() throws Exception {
        // Mock: Seite 2 (0-basiert) mit size=5, sort=title,asc, totalElements=12
        var content = List.of(dto(10, "A Bowl"), dto(11, "B Bowl"));
        var pageable = PageRequest.of(2, 5, Sort.by(Sort.Order.asc("title")));
        var page = new PageImpl<>(content, pageable, 12);
        Mockito.when(service.findPaged(eq("bowl"), any(Pageable.class))).thenReturn(page);

        mvc.perform(get("/recipes")
                        .param("q", "bowl")
                        .param("page", "2")
                        .param("size", "5")
                        .param("sort", "title,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Seitenstruktur
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.totalElements").value(12))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.last").value(true))
                // inhaltliche Stichprobe
                .andExpect(jsonPath("$.content[0].title").value("A Bowl"));

        // Prüfen, dass Pageable korrekt weitergereicht wurde
        var captor = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(service).findPaged(eq("bowl"), captor.capture());
        Pageable p = captor.getValue();
        assertThat(p.getPageNumber()).isEqualTo(2);
        assertThat(p.getPageSize()).isEqualTo(5);
        assertThat(p.getSort().getOrderFor("title")).isNotNull();
        assertThat(p.getSort().getOrderFor("title").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void getAll_without_query_uses_default_pageable() throws Exception {
        // Mock: leere Seite mit Default-Pageable (size=20 im Controller)
        var page = new PageImpl<RecipeReadDto>(List.of(), PageRequest.of(0, 20), 0);
        Mockito.when(service.findPaged(eq(null), any(Pageable.class))).thenReturn(page);

        mvc.perform(get("/recipes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20));

        var captor = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(service).findPaged(eq(null), captor.capture());
        Pageable p = captor.getValue();
        assertThat(p.getPageNumber()).isEqualTo(0);
        assertThat(p.getPageSize()).isEqualTo(20);
    }
}
