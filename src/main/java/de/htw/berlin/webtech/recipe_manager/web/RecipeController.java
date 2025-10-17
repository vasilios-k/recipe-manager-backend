package de.htw.berlin.webtech.recipe_manager.web;

import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import de.htw.berlin.webtech.recipe_manager.service.RecipeService;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeCreateDto;
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
    public List<Recipe> getAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<de.htw.berlin.webtech.recipe_manager.web.dto.CreatedIdResponse> create(
            @RequestBody @jakarta.validation.Valid de.htw.berlin.webtech.recipe_manager.web.dto.RecipeCreateDto dto
    ) {
        var created = service.create(dto);
        var body = new de.htw.berlin.webtech.recipe_manager.web.dto.CreatedIdResponse(created.getId());
        return ResponseEntity
                .created(URI.create("/recipes/" + created.getId()))
                .body(body);
    }
}
