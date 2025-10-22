package de.htw.berlin.webtech.recipe_manager.domain;

import jakarta.persistence.*;                    // JPA-Annotationen für das DB-Mapping
import jakarta.validation.constraints.NotBlank;  // Validierung: String darf nicht leer/whitespace sein
import jakarta.validation.constraints.NotNull;   // Validierung: Wert darf nicht null sein

import java.math.BigDecimal; // Für genaue Mengenangaben (besser als double/float)

/**
 * Repräsentiert eine Zutat eines Rezepts (JPA-Entität).
 * Wird in der Datenbank in einer eigenen Tabelle gespeichert.
 */
@Entity
public class Ingredient {

    // ---- Primärschlüssel (eindeutige ID in der DB) ----
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB erzeugt ID automatisch (Auto-Increment)
    private Long id;

    // ---- Fachliche Felder ----
    @NotBlank               // Name muss vorhanden sein (nicht null, nicht leer, nicht nur Leerzeichen)
    private String name;

    @NotNull                // amount muss gesetzt sein (keine null erlaubt)
    @Column(precision = 19, // insgesamt bis zu 19 Stellen
            scale = 4)      // davon 4 Nachkommastellen (z. B. 1.2500)
    private BigDecimal amount; // Menge (präzise Dezimalzahl, z. B. 0.5, 1.2500)


    @NotNull // Einheit muss vorhanden sein (z. B. "g", "ml", "Stk")
    @Enumerated(EnumType.STRING)
    private Unit unit;

    // ---- Beziehung zurück zum Rezept (Viele Zutaten gehören zu EINEM Rezept) ----
    @ManyToOne(fetch = FetchType.LAZY,  // Rezept wird erst geladen, wenn man es wirklich braucht (Performance)
            optional = false)        // Pflicht-Beziehung: jede Zutat MUSS ein Rezept haben
    @JoinColumn(name = "recipe_id")     // Name der Fremdschlüsselspalte in der Ingredient-Tabelle
    private Recipe recipe;

    // ---- Getter/Setter (Standard) ----
    public Long getId() { return id; }  // kein Setter: ID kommt von der DB

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Unit getUnit() { return unit; }
    public void setUnit(Unit unit) { this.unit = unit; }

    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = recipe; } // wird beim Setzen in Recipe automatisch gesetzt
}
