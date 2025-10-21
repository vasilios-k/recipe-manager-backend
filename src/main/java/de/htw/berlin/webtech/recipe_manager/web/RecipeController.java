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

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

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

    @GetMapping("/{id}")
    public RecipeReadDto getOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PostMapping
    public ResponseEntity<CreatedIdResponse> create(@RequestBody @Valid RecipeCreateDto dto) {
        Recipe created = service.create(dto);
        return ResponseEntity
                .created(URI.create("/recipes/" + created.getId()))
                .body(new CreatedIdResponse(created.getId()));
    }

    // PUT: nur Basisfelder (kein Ingredients/Steps)
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBase(@PathVariable Long id,
                                           @RequestBody @Valid RecipeUpdateDto dto) {
        service.updateBase(id, dto);
        return ResponseEntity.noContent().build(); // 204
    }

    // Sub-Resource: komplette Zutatenliste ersetzen
    @PutMapping("/{id}/ingredients")
    public ResponseEntity<Void> replaceIngredients(@PathVariable Long id,
                                                   @RequestBody @Valid List<IngredientCreateDto> body) {
        service.replaceIngredients(id, body);
        return ResponseEntity.noContent().build(); // 204
    }

    // Sub-Resource: komplette Schrittliste ersetzen
    @PutMapping("/{id}/steps")
    public ResponseEntity<Void> replaceSteps(@PathVariable Long id,
                                             @RequestBody @Valid List<StepCreateDto> body) {
        service.replaceSteps(id, body);
        return ResponseEntity.noContent().build(); // 204
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
