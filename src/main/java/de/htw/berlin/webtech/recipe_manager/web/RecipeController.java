package de.htw.berlin.webtech.recipe_manager.web;

import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import de.htw.berlin.webtech.recipe_manager.service.RecipeService;
import de.htw.berlin.webtech.recipe_manager.web.dto.CreatedIdResponse;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeReadDto;
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
    public List<RecipeReadDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public RecipeReadDto getOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PostMapping
    public ResponseEntity<CreatedIdResponse> create(@RequestBody @jakarta.validation.Valid RecipeCreateDto dto) {
        Recipe created = service.create(dto);
        return ResponseEntity
                .created(URI.create("/recipes/" + created.getId()))
                .body(new CreatedIdResponse(created.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
