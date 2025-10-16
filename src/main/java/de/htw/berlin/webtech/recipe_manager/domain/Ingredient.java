package de.htw.berlin.webtech.recipe_manager.domain;

import java.math.BigDecimal;

public class Ingredient {

    private String name;
    private BigDecimal amount;
    private String unit;

    protected Ingredient() { }
    public Ingredient(String name, BigDecimal amount, String unit) {
        this.name = name; this.amount = amount; this.unit = unit;
    }
    public String getName() { return name; }
    public BigDecimal getAmount() { return amount; }
    public String getUnit() { return unit; }

}
