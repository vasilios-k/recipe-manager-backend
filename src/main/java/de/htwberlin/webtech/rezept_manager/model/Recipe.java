package de.htwberlin.webtech.rezept_manager.model;

public class Recipe {
    private Long id;
    private String title;
    private String category;
    private int prepTimeMinutes;

    public Recipe() {
    }

    public Recipe(Long id, String title, String category, int prepTimeMinutes) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(int prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }
}
