package de.htw.berlin.webtech.recipe_manager.domain;

import jakarta.persistence.*;                  // JPA-Annotationen (für Datenbank-Mapping)
import jakarta.validation.constraints.Min;     // Validierung: Mindestwerte prüfen
import jakarta.validation.constraints.NotBlank; // Validierung: nicht leerer String

import java.util.*; // Listen, Sets, Optional usw.

/**
 * Repräsentiert ein Rezept in der Datenbank (JPA-Entität).
 * Diese Klasse wird von JPA in eine Tabelle abgebildet.
 */
@Entity
public class Recipe {

    // ---- Primärschlüssel (eindeutige ID in der Datenbank) ----
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB erzeugt die ID automatisch (Auto-Increment)
    private Long id;

    // ---- Basisdaten zum Rezept ----
    @NotBlank // darf nicht null/leer/Whitespace sein
    private String title;

    @Column(length = 2000) // Beschreibung darf bis zu 2000 Zeichen lang sein
    private String description;

    @Min(0) // wenn gesetzt, muss >= 0 sein
    private Integer prepMinutes; // Vorbereitungszeit in Minuten (optional)

    @Min(0) // wenn gesetzt, muss >= 0 sein
    private Integer cookMinutes; // Koch-/Backzeit in Minuten (optional)

    // ---- Kategorisierung & Ernährungstags (als einfache Sammlungen gespeichert) ----

    /**
     * Kategorien wie "Schnell", "Italienisch" usw.
     * Wird in einer separaten Tabelle (recipe_categories) gespeichert,
     * weil es mehrere Werte pro Rezept geben kann.
     * LAZY = wird bei Bedarf geladen
     */
    @ElementCollection(fetch = FetchType.LAZY)  //Sammlung einfacher Werte (keine eigene Entität), hier String.
    @CollectionTable(name = "recipe_categories", joinColumns = @JoinColumn(name = "recipe_id")) //legt eine Neben-Tabelle an (recipe_categories) mit Fremdschlüssel recipe_id.
    @Column(name = "category")  //Name der Spalte für den String-Wert.
    private Set<String> categories = new LinkedHashSet<>();

    /**
     * Ernährungstags (Enum), z. B. VEGAN oder OMNIVORE.
     * Speicherung als Text (STRING) ist für Migrationen sicherer als Zahlen.
     * Auch hier separate Tabelle (recipe_diet_tags).
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "recipe_diet_tags", joinColumns = @JoinColumn(name = "recipe_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "diet_tag")
    private Set<DietTag> dietTags = new LinkedHashSet<>();

    // ---- Beziehungen zu Kind-Objekten: Zutaten & Schritte ----

    /**
     * Ein Rezept hat viele Zutaten.
     * mappedBy = "recipe": Die Fremdschlüsselspalte liegt in Ingredient (Kind-Seite).
     * cascade = ALL: Speichern/Löschen des Rezepts betrifft automatisch die Zutaten.
     * orphanRemoval = true: Entfernte Zutaten werden in der DB gelöscht.
     * LAZY: Zutaten werden erst geladen, wenn man sie wirklich braucht.
     * @OrderBy: definierte Reihenfolge beim Laden (hier nach ID).
     */
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<Ingredient> ingredients = new ArrayList<>();

    /**
     * Ein Rezept hat viele Schritte (1, 2, 3, ...).
     * Gleiches Prinzip wie bei ingredients.
     * Sortierung zuerst nach "position" (1,2,3...), dann nach ID.
     */
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("position ASC, id ASC")
    private List<Step> steps = new ArrayList<>();

    // ---- Bequeme Hilfsmethoden ----

    /**
     * Gesamtzeit (Vorbereitung + Kochen).
     * Nullwerte werden als 0 behandelt, damit es nicht crasht.
     */
    public Integer getTotalMinutes() {
        int p = prepMinutes != null ? prepMinutes : 0;
        int c = cookMinutes != null ? cookMinutes : 0;
        return p + c;
    }

    /**
     * Gibt das Baseline-Ernährungstag zurück (wenn vorhanden).
     * Baseline = genau eine Grundausrichtung wie VEGAN oder OMNIVORE.
     * Optional bedeutet: kann leer sein, wenn nichts gesetzt ist.
     */
    public Optional<DietTag> getBaselineTag() {
        return dietTags.stream()
                .filter(t -> t.getGroup() == DietTag.DietGroup.BASELINE)
                .findFirst();
    }

    // ---- Domänenregel: Maximal 1 Baseline-Tag erzwingen ----

    /**
     * Diese Methode wird automatisch VOR dem Speichern/Aktualisieren aufgerufen.
     * Sie verhindert, dass mehr als ein Baseline-Tag gesetzt ist.
     */
    @PrePersist
    @PreUpdate
    private void validateBaselineRule() {
        long baselineCount = (dietTags == null) ? 0
                : dietTags.stream().filter(t -> t.getGroup() == DietTag.DietGroup.BASELINE).count();

        if (baselineCount > 1) {
            // Einfacher Fehlerwurf. In der API kann das später als 400 Bad Request gemappt werden.
            throw new IllegalArgumentException(
                    "Es ist höchstens ein Baseline-Tag erlaubt (VEGAN/VEGETARIAN/PESCETARIAN/OMNIVORE)."
            );
        }
    }

    // ---- Getter/Setter (Standard) ----

    public Long getId() { return id; } // Kein Setter nötig: ID kommt von der DB

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPrepMinutes() { return prepMinutes; }
    public void setPrepMinutes(Integer prepMinutes) { this.prepMinutes = prepMinutes; }

    public Integer getCookMinutes() { return cookMinutes; }
    public void setCookMinutes(Integer cookMinutes) { this.cookMinutes = cookMinutes; }

    public Set<String> getCategories() { return categories; }

    /**
     * Ersetzt den Inhalt des Sets (statt das Set-Objekt auszutauschen).
     * Vorteil: JPA erkennt Änderungen sauber (Change-Tracking).
     */
    public void setCategories(Set<String> categories) {
        this.categories.clear();
        if (categories != null) this.categories.addAll(categories);
    }

    public Set<DietTag> getDietTags() { return dietTags; }

    /**
     * Ersetzt den Inhalt der DietTags und prüft DIREKT die Baseline-Regel.
     */
    public void setDietTags(Set<DietTag> dietTags) {
        this.dietTags.clear();
        if (dietTags != null) this.dietTags.addAll(dietTags);
        validateBaselineRule(); // sofortige Validierung (früher Fehler statt später Überraschung)
    }

    public List<Ingredient> getIngredients() { return ingredients; }

    /**
     * Setzt die Zutaten-Liste neu und stellt die Rückverknüpfung her:
     * Jede Zutat zeigt wieder auf "dieses" Rezept.
     */
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients.clear();
        if (ingredients != null) {
            for (Ingredient i : ingredients) {
                if (i != null) i.setRecipe(this); // Backref setzen (wichtig für JPA-Konsistenz)
            }
            this.ingredients.addAll(ingredients);
        }
    }

    public List<Step> getSteps() { return steps; }

    /**
     * Setzt die Schritt-Liste neu und stellt die Rückverknüpfung her:
     * Jeder Schritt zeigt wieder auf "dieses" Rezept.
     */
    public void setSteps(List<Step> steps) {
        this.steps.clear();
        if (steps != null) {
            for (Step s : steps) {
                if (s != null) s.setRecipe(this); // Backref setzen
            }
            this.steps.addAll(steps);
        }
    }
}
