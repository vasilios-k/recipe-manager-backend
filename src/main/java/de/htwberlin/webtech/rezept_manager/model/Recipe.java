package de.htwberlin.webtech.rezept_manager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;
    private int prepTimeMinutes;

    protected Recipe() {
        // Required by JPA
    }

    public Recipe(String title, String category, int prepTimeMinutes) {
        this.title = title;
        this.category = category;
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }
}