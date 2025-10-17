package de.htw.berlin.webtech.recipe_manager.service;

import de.htw.berlin.webtech.recipe_manager.domain.DietTag;
import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import de.htw.berlin.webtech.recipe_manager.repo.RecipeRepository;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeReadDto;
import de.htw.berlin.webtech.recipe_manager.web.mapper.RecipeCreateMapper;
import de.htw.berlin.webtech.recipe_manager.web.mapper.RecipeReadMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class RecipeService {

    private final RecipeRepository repository;
    private final RecipeCreateMapper createMapper;
    private final RecipeReadMapper readMapper;

    public RecipeService(RecipeRepository repository, RecipeCreateMapper createMapper, RecipeReadMapper readMapper) {
        this.repository = repository;
        this.createMapper = createMapper;
        this.readMapper = readMapper;
    }

    @Transactional(readOnly = true)
    public List<RecipeReadDto> findAll() {
        return repository.findAll().stream()
                .map(readMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecipeReadDto findOne(long id) {
        var recipe = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " not found"));
        return readMapper.toDto(recipe);
    }

    @Transactional
    public Recipe create(RecipeCreateDto dto) {
        validateBaseline(dto.dietTags());
        Recipe entity = createMapper.toEntity(dto);
        return repository.save(entity);
    }

    @Transactional
    public void delete(long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " not found");
        }
        repository.deleteById(id); // Cascade/OrphanRemoval übernimmt Ingredients/Steps
    }

    private void validateBaseline(Set<DietTag> tags) {
        if (tags == null) return;
        long baseline = tags.stream()
                .filter(t -> t.getGroup() == DietTag.DietGroup.BASELINE)
                .count();
        if (baseline > 1) {
            throw new IllegalArgumentException("Maximal ein BASELINE-Tag erlaubt.");
        }
    }
}
