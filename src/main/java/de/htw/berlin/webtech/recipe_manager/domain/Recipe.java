// src/main/java/de/htw/berlin/webtech/recipe_manager/domain/Recipe.java
package de.htw.berlin.webtech.recipe_manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.*;

@Entity
public class Recipe {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @Column(length = 2000)
    private String description;

    @Min(0)
    private Integer prepMinutes;

    @Min(0)
    private Integer cookMinutes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_categories", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "category")
    private Set<String> categories = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_diet_tags", joinColumns = @JoinColumn(name = "recipe_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "diet_tag")
    private Set<DietTag> dietTags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<Ingredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("position ASC, id ASC")
    private List<Step> steps = new ArrayList<>();

    public Integer getTotalMinutes() {
        int p = prepMinutes != null ? prepMinutes : 0;
        int c = cookMinutes != null ? cookMinutes : 0;
        return p + c;
    }

    public Optional<DietTag> getBaselineTag() {
        return dietTags.stream()
                .filter(t -> t.getGroup() == DietTag.DietGroup.BASELINE)
                .findFirst();
    }

    // --- Lifecycle: Baseline-Regel absichern (max. 1 Baseline) ---
    @PrePersist @PreUpdate
    private void validateBaselineRule() {
        long baselineCount = dietTags == null ? 0 :
                dietTags.stream().filter(t -> t.getGroup() == DietTag.DietGroup.BASELINE).count();
        if (baselineCount > 1) {
            throw new IllegalArgumentException(
                    "Es ist höchstens ein Baseline-Tag erlaubt (VEGAN/VEGETARIAN/PESCETARIAN/OMNIVORE)."
            );
        }
    }

    // Getters/Setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getPrepMinutes() { return prepMinutes; }
    public void setPrepMinutes(Integer prepMinutes) { this.prepMinutes = prepMinutes; }
    public Integer getCookMinutes() { return cookMinutes; }
    public void setCookMinutes(Integer cookMinutes) { this.cookMinutes = cookMinutes; }

    public Set<String> getCategories() { return categories; }
    public void setCategories(Set<String> categories) {
        this.categories.clear();
        if (categories != null) this.categories.addAll(categories);
    }

    public Set<DietTag> getDietTags() { return dietTags; }
    public void setDietTags(Set<DietTag> dietTags) {
        this.dietTags.clear();
        if (dietTags != null) this.dietTags.addAll(dietTags);
        validateBaselineRule();
    }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients.clear();
        if (ingredients != null) {
            for (Ingredient i : ingredients) {
                if (i != null) i.setRecipe(this);  // Backref
            }
            this.ingredients.addAll(ingredients);
        }
    }

    public List<Step> getSteps() { return steps; }
    public void setSteps(List<Step> steps) {
        this.steps.clear();
        if (steps != null) {
            for (Step s : steps) {
                if (s != null) s.setRecipe(this);  // Backref
            }
            this.steps.addAll(steps);
        }
    }
}
