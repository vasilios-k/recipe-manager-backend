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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Geschäftslogik rund um Rezepte (Use-Cases).
 * Orchestriert Repository + Mapper und kapselt Regeln (z. B. Baseline-Tag).
 */
@Service
public class RecipeService {

    private final RecipeRepository repository;
    private final RecipeCreateMapper createMapper;
    private final RecipeReadMapper readMapper;

    // Konstruktor-Injection (Testbarkeit, Immutabilität)
    public RecipeService(RecipeRepository repository, RecipeCreateMapper createMapper, RecipeReadMapper readMapper) {
        this.repository = repository;
        this.createMapper = createMapper;
        this.readMapper = readMapper;
    }

    // ---- Lese-Use-Cases ------------------------------------------------------

    /**
     * Alle Rezepte als Liste zurückgeben.
     * readOnly-Transaktion: keine Änderungen, bessere Performance.
     */
    @Transactional(readOnly = true)
    public List<RecipeReadDto> findAll() {
        return repository.findAll().stream()
                .map(readMapper::toDto) // Entity -> Read-DTO (inkl. Convenience-Felder)
                .toList();
    }

    /**
     * Ein einzelnes Rezept per ID.
     * Wirft 404, wenn nicht gefunden.
     */
    @Transactional(readOnly = true)
    public RecipeReadDto findOne(long id) {
        var recipe = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " not found"));
        return readMapper.toDto(recipe);
    }

    /**
     * Paginierte Suche: Wenn q leer ist, normale findAll(pageable),
     * sonst die Search-Query (Titel/Beschreibung/Kategorien, case-insensitive).
     */
    @Transactional(readOnly = true)
    public Page<RecipeReadDto> findPaged(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return repository.findAll(pageable).map(readMapper::toDto);
        }
        return repository.search(q, pageable).map(readMapper::toDto);
    }

    // ---- Schreib-Use-Cases ----------------------------------------------------

    /**
     * Neues Rezept anlegen.
     * - prüft Baseline-Regel (max. 1 Baseline-Tag)
     * - mappt Create-DTO -> Entity (inkl. Backrefs)
     * - speichert via Repository
     */
    @Transactional
    public Recipe create(RecipeCreateDto dto) {
        validateBaseline(dto.dietTags());      // Domänenregel (spiegelt Entity-Lifecycle)
        Recipe entity = createMapper.toEntity(dto); // baut auch Ingredients/Steps + Backrefs
        return repository.save(entity);        // Cascade + orphanRemoval greifen für Kinder
    }

    /**
     * Basisfelder eines Rezepts updaten (PUT auf "Stammdaten").
     * Zutaten/Schritte werden HIER nicht angefasst (dafür gibt's Sub-Resource-Methoden).
     */
    @Transactional
    public void updateBase(long id, RecipeUpdateDto dto) {
        var r = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " not found"));

        validateBaseline(dto.dietTags()); // erneut Regel prüfen (schnelles Fail-First)

        // Skalare Felder + Sets direkt via Entity-Setter (Change Tracking)
        r.setTitle(dto.title());
        r.setDescription(dto.description());
        r.setPrepMinutes(dto.prepMinutes());
        r.setCookMinutes(dto.cookMinutes());
        r.setDietTags(dto.dietTags());
        r.setCategories(dto.categories());

        repository.save(r); // flush/persist Änderungen
    }

    /**
     * Zutatenliste KOMPLETT ersetzen (Sub-Resource).
     * - löscht alte Kinder via orphanRemoval
     * - baut neue Kinder aus DTOs und setzt Backrefs
     */
    @Transactional
    public void replaceIngredients(long id, List<IngredientCreateDto> list) {
        var r = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " not found"));

        r.getIngredients().clear(); // orphanRemoval löscht alte Datensätze in der DB
        if (list != null) {
            for (var d : list) {
                var i = new Ingredient();
                i.setName(d.name());
                i.setAmount(d.amount());
                i.setUnit(d.unit()); // aktuell String; (Unit-Entscheidung kommt am Ende)
                i.setRecipe(r);      // Backref: Kind zeigt auf Parent
                r.getIngredients().add(i);
            }
        }
        repository.save(r);
    }

    /**
     * Schrittliste KOMPLETT ersetzen (Sub-Resource).
     * - sortiert eingehende DTOs nach position
     * - löscht alte Steps via orphanRemoval
     * - setzt Backrefs
     */
    @Transactional
    public void replaceSteps(long id, List<StepCreateDto> list) {
        var r = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " not found"));

        r.getSteps().clear();
        if (list != null) {
            list.stream()
                    .sorted(Comparator.comparing(StepCreateDto::position))
                    .forEach(d -> {
                        var s = new Step();
                        s.setPosition(d.position());
                        s.setText(d.text());
                        s.setRecipe(r); // Backref
                        r.getSteps().add(s);
                    });
        }
        repository.save(r);
    }

    /**
     * Rezept löschen (inkl. Kinder dank orphanRemoval).
     * Wirft 404, wenn ID nicht existiert.
     */
    @Transactional
    public void delete(long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " not found");
        }
        repository.deleteById(id);
    }

    // ---- Interne Regel: max. 1 Baseline-Tag ----------------------------------

    /**
     * Zählt BASELINE-Tags (VEGAN/VEGETARIAN/PESCETARIAN/OMNIVORE) und erlaubt höchstens 1.
     * Wirft IllegalArgumentException bei Verstoß (wird i. d. R. zu 400 gemappt).
     */
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
