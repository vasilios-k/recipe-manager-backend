// src/main/java/de/htw/berlin/webtech/recipe_manager/domain/Step.java
package de.htw.berlin.webtech.recipe_manager.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "recipe_step")
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reihenfolge im Rezept
    @Column(nullable = false)
    private int position;

    @Column(nullable = false, length = 2000)
    private String text;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    @JsonBackReference
    private Recipe recipe;

    public Step() {}

    public Long getId() { return id; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = recipe; }
}
