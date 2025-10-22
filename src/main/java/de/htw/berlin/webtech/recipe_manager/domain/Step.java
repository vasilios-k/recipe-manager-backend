package de.htw.berlin.webtech.recipe_manager.domain;

import jakarta.persistence.*;                    // JPA-Annotationen fürs DB-Mapping
import jakarta.validation.constraints.Min;       // Validierung: Mindestwert prüfen
import jakarta.validation.constraints.NotBlank;  // Validierung: String darf nicht leer/whitespace sein

/**
 * Ein einzelner Arbeitsschritt eines Rezepts (JPA-Entität).
 */
@Entity
@Table(name = "recipe_step") // Tabellenname explizit setzen (statt Standard "step")
public class Step {

    // ---- Primärschlüssel (eindeutige ID in der DB) ----
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID wird von der DB automatisch vergeben
    private Long id;

    // ---- Fachliche Felder ----
    @Min(1)                 // Schritt-Position muss >= 1 sein (1,2,3,...)
    private Integer position;

    @NotBlank               // Schritttext muss vorhanden sein (nicht null/leer/whitespace)
    @Column(length = 2000)  // bis zu 2000 Zeichen erlaubt
    private String text;

    // ---- Beziehung zurück zum Rezept (viele Steps gehören zu EINEM Recipe) ----
    @ManyToOne(fetch = FetchType.LAZY, // Recipe wird nur bei Bedarf nachgeladen (Performance)
            optional = false)       // Pflicht: jeder Step muss ein Rezept haben
    @JoinColumn(name = "recipe_id")    // Fremdschlüsselspalte in recipe_step
    private Recipe recipe;

    // ---- Getter/Setter (Standard) ----
    public Long getId() { return id; } // kein Setter: ID kommt von der DB

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = recipe; } // wird beim Setzen im Recipe aggregatseitig gepflegt
}
