package de.htw.berlin.webtech.recipe_manager.service;

import de.htw.berlin.webtech.recipe_manager.domain.DietTag;
import de.htw.berlin.webtech.recipe_manager.domain.Ingredient;
import de.htw.berlin.webtech.recipe_manager.domain.Recipe;
import de.htw.berlin.webtech.recipe_manager.domain.Step;
import de.htw.berlin.webtech.recipe_manager.repo.RecipeRepository;
import de.htw.berlin.webtech.recipe_manager.web.dto.IngredientCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeReadDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.RecipeUpdateDto;
import de.htw.berlin.webtech.recipe_manager.web.dto.StepCreateDto;
import de.htw.berlin.webtech.recipe_manager.web.mapper.RecipeCreateMapper;
import de.htw.berlin.webtech.recipe_manager.web.mapper.RecipeReadMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
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

    // PUT: nur Basisfelder
    @Transactional
    public void updateBase(long id, RecipeUpdateDto dto) {
        var r = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " not found"));

        validateBaseline(dto.dietTags());

        r.setTitle(dto.title());
        r.setDescription(dto.description());
        r.setPrepMinutes(dto.prepMinutes());
        r.setCookMinutes(dto.cookMinutes());
        r.setDietTags(dto.dietTags());
        r.setCategories(dto.categories());

        repository.save(r);
    }

    // Sub-Resource: Zutatenliste komplett ersetzen
    @Transactional
    public void replaceIngredients(long id, List<IngredientCreateDto> list) {
        var r = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " not found"));

        r.getIngredients().clear(); // orphanRemoval löscht alte
        if (list != null) {
            for (var d : list) {
                var i = new Ingredient();
                i.setName(d.name());
                i.setAmount(d.amount());
                i.setUnit(d.unit());
                i.setRecipe(r); // Backref
                r.getIngredients().add(i);
            }
        }
        repository.save(r);
    }

    // Sub-Resource: Schrittliste komplett ersetzen (sortiert nach position)
    @Transactional
    public void replaceSteps(long id, List<StepCreateDto> list) {
        var r = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " not found"));

        r.getSteps().clear();
        if (list != null) {
            list.stream()
                    .sorted(Comparator.comparing(s -> s.position() == null ? 0 : s.position()))
                    .forEach(d -> {
                        var s = new Step();
                        s.setPosition(d.position() == null ? 0 : d.position());
                        s.setText(d.text());
                        s.setRecipe(r); // Backref
                        r.getSteps().add(s);
                    });
        }
        repository.save(r);
    }

    @Transactional
    public void delete(long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " not found");
        }
        repository.deleteById(id);
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
