// src/main/java/de/htw/berlin/webtech/recipe_manager/domain/Recipe.java
package de.htw.berlin.webtech.recipe_manager.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private int prepMinutes;

    @Column(nullable = false)
    private int cookMinutes;

    // DietTags (Enum). Baseline-Regel: höchstens EIN Tag aus DietGroup.BASELINE.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_diet_tags", joinColumns = @JoinColumn(name = "recipe_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "diet_tag", nullable = false)
    private Set<DietTag> dietTags = new HashSet<>();

    // Freie Kategorien (Strings)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_categories", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "category", length = 100, nullable = false)
    private Set<String> categories = new HashSet<>();

    // Zutaten 1:n
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @OrderBy("id ASC")
    private List<Ingredient> ingredients = new ArrayList<>();

    // Schritte 1:n
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @OrderBy("position ASC")
    private List<Step> steps = new ArrayList<>();

    public Recipe() {}

    // ---- Getter/Setter (Framework-kompatibel) ----
    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPrepMinutes() { return prepMinutes; }
    public void setPrepMinutes(int prepMinutes) { this.prepMinutes = prepMinutes; }

    public int getCookMinutes() { return cookMinutes; }
    public void setCookMinutes(int cookMinutes) { this.cookMinutes = cookMinutes; }

    public Set<DietTag> getDietTags() { return Collections.unmodifiableSet(dietTags); }
    public void setDietTags(Set<DietTag> dietTags) {
        if (dietTags == null) dietTags = Collections.emptySet();
        ensureSingleBaseline(dietTags);
        this.dietTags.clear();
        this.dietTags.addAll(dietTags);
    }

    public boolean addDietTag(DietTag tag) {
        Set<DietTag> next = new HashSet<>(this.dietTags);
        next.add(tag);
        ensureSingleBaseline(next);
        return this.dietTags.add(tag);
    }

    public boolean removeDietTag(DietTag tag) {
        return this.dietTags.remove(tag);
    }

    public Set<String> getCategories() { return Collections.unmodifiableSet(categories); }
    public void setCategories(Set<String> categories) {
        this.categories.clear();
        if (categories != null) this.categories.addAll(categories);
    }
    public boolean addCategory(String category) { return this.categories.add(category); }
    public boolean removeCategory(String category) { return this.categories.remove(category); }

    public List<Ingredient> getIngredients() { return Collections.unmodifiableList(ingredients); }
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients.clear();
        if (ingredients != null) this.ingredients.addAll(ingredients);
    }

    public List<Step> getSteps() { return Collections.unmodifiableList(steps); }
    public void setSteps(List<Step> steps) {
        this.steps.clear();
        if (steps != null) this.steps.addAll(steps);
    }

    // ---- Convenience/Optionals ----
    /** Gesamtzeit (nicht persistiert). */
    @Transient
    public int getTotalMinutes() {
        return Math.max(0, prepMinutes) + Math.max(0, cookMinutes);
    }

    /** Optional: baseline Tag, falls gesetzt. */
    @Transient
    public Optional<DietTag> getBaselineTag() {
        return dietTags.stream()
                .filter(t -> t.getGroup() == DietTag.DietGroup.BASELINE)
                .findFirst();
    }

    /** Setzt den Baseline-Tag (ersetzt einen existierenden Baseline-Tag). Null entfernt Baseline. */
    public void setBaselineTag(DietTag baselineOrNull) {
        // Entferne evtl. vorhandene Baseline
        dietTags.removeIf(t -> t.getGroup() == DietTag.DietGroup.BASELINE);
        if (baselineOrNull != null) {
            if (baselineOrNull.getGroup() != DietTag.DietGroup.BASELINE) {
                throw new IllegalArgumentException("Übergebener Tag ist kein BASELINE-Tag: " + baselineOrNull);
            }
            dietTags.add(baselineOrNull);
        }
    }

    // ---- Regelprüfung ----
    private void ensureSingleBaseline(Set<DietTag> tags) {
        long count = tags.stream()
                .filter(t -> t.getGroup() == DietTag.DietGroup.BASELINE)
                .count();
        if (count > 1) {
            throw new IllegalArgumentException(
                    "Nur ein BASELINE-Tag erlaubt (VEGAN/VEGETARIAN/PESCETARIAN/OMNIVORE)."
            );
        }
    }
}
