package de.htw.berlin.webtech.recipe_manager.web;

import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import de.htw.berlin.webtech.recipe_manager.service.RecipeService;
import de.htw.berlin.webtech.recipe_manager.web.dto.CreatedIdResponse;
import de.htw.berlin.webtech.recipe_manager.web.dto.IngredientCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeReadDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeUpdateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.StepCreateDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST-API für Rezepte.
 * Nimmt DTOs entgegen, ruft den Service auf und gibt DTOs/ResponseEntitys zurück.
 */
@RestController
@RequestMapping("/recipes") // Basis-Pfad für alle Endpunkte hier
public class RecipeController {

    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    /**
     * GET /recipes?q=... (optional) – paginierte Liste.
     * - Pageable-Defaults: page=0, size=20, sort=id DESC (neueste zuerst).
     * - Wenn q fehlt/leer: normale Auflistung; sonst Suche über Titel/Beschreibung/Kategorien.
     */
    @GetMapping
    public Page<RecipeReadDto> getAll(
            @RequestParam(value = "q", required = false) String q,
            @PageableDefault(size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {
        return service.findPaged(q, pageable);
    }

    /**
     * GET /recipes/{id} – einzelnes Rezept.
     * Wirft 404, wenn es nicht existiert (Service übernimmt das).
     */
    @GetMapping("/{id}")
    public RecipeReadDto getOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    /**
     * POST /recipes – neues Rezept anlegen.
     * - @Valid löst Bean Validation auf dem Body (RecipeCreateDto) aus.
     * - Location-Header zeigt auf /recipes/{id}.
     * - Body enthält die neue ID als CreatedIdResponse.
     */
    @PostMapping
    public ResponseEntity<CreatedIdResponse> create(@RequestBody @Valid RecipeCreateDto dto) {
        Recipe created = service.create(dto);
        return ResponseEntity
                .created(URI.create("/recipes/" + created.getId())) // Location
                .body(new CreatedIdResponse(created.getId()));      // { "id": 123 }
    }

    /**
     * PUT /recipes/{id} – Stammdaten aktualisieren (KEINE Zutaten/Schritte).
     * - Gibt 204 No Content zurück bei Erfolg.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBase(@PathVariable Long id,
                                           @RequestBody @Valid RecipeUpdateDto dto) {
        service.updateBase(id, dto);
        return ResponseEntity.noContent().build(); // 204
    }

    /**
     * PUT /recipes/{id}/ingredients – komplette Zutatenliste ersetzen.
     * - @Valid prüft jede IngredientCreateDto.
     * - 204 No Content bei Erfolg.
     */
    @PutMapping("/{id}/ingredients")
    public ResponseEntity<Void> replaceIngredients(@PathVariable Long id,
                                                   @RequestBody @Valid List<IngredientCreateDto> body) {
        service.replaceIngredients(id, body);
        return ResponseEntity.noContent().build(); // 204
    }

    /**
     * PUT /recipes/{id}/steps – komplette Schrittliste ersetzen.
     * - @Valid prüft jede StepCreateDto.
     * - 204 No Content bei Erfolg.
     */
    @PutMapping("/{id}/steps")
    public ResponseEntity<Void> replaceSteps(@PathVariable Long id,
                                             @RequestBody @Valid List<StepCreateDto> body) {
        service.replaceSteps(id, body);
        return ResponseEntity.noContent().build(); // 204
    }

    /**
     * DELETE /recipes/{id} – Rezept löschen.
     * - 204 No Content bei Erfolg, 404 wenn nicht gefunden (Service).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
