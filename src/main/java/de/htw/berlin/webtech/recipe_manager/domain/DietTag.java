package de.htw.berlin.webtech.recipe_manager.domain;

import java.util.EnumSet;       // effiziente Sets für Enums
import java.util.Set;           // allgemeines Set-Interface
import java.util.stream.Collectors; // Hilfen zum Gruppieren/Sammeln mit Streams

/**
 * Aufzählung (Enum) für Ernährungs-/Eigenschafts-Tags eines Rezepts.
 * Jeder Tag gehört genau zu einer "Gruppe" (DietGroup), z. B. BASELINE oder MACRO.
 * Wichtig: Von BASELINE-Tags soll genau EINER gewählt sein (Regel in Recipe.java).
 */
public enum DietTag {

    // ---- Baseline (genau EIN Tag sinnvoll) ----
    VEGAN(DietGroup.BASELINE),
    VEGETARIAN(DietGroup.BASELINE),
    PESCETARIAN(DietGroup.BASELINE),
    OMNIVORE(DietGroup.BASELINE),

    // ---- Allergen-/Unverträglichkeitsbezogen (beliebig kombinierbar) ----
    GLUTEN_FREE(DietGroup.ALLERGEN_FREE),
    LACTOSE_FREE(DietGroup.ALLERGEN_FREE),
    SOY_FREE(DietGroup.ALLERGEN_FREE),
    EGG_FREE(DietGroup.ALLERGEN_FREE),
    SHELLFISH_FREE(DietGroup.ALLERGEN_FREE),
    SESAME_FREE(DietGroup.ALLERGEN_FREE),
    PEANUT_FREE(DietGroup.ALLERGEN_FREE),
    LOW_FODMAP(DietGroup.ALLERGEN_FREE),

    // ---- religiös/ethisch (orthogonal zur BASELINE; kombinierbar) ----
    HALAL(DietGroup.OTHER),
    KOSHER(DietGroup.OTHER),

    // ---- Makro-/Lebensstil (kombinierbar) ----
    LOW_CARB(DietGroup.MACRO),
    HIGH_PROTEIN(DietGroup.MACRO),
    LOW_FAT(DietGroup.MACRO),
    LOW_SUGAR(DietGroup.MACRO),
    NO_ADDED_SUGAR(DietGroup.MACRO),
    KETO(DietGroup.MACRO),
    PALEO(DietGroup.MACRO),
    HIGH_FIBER(DietGroup.MACRO),
    LOW_SODIUM(DietGroup.MACRO),

    // ---- Technik/sonstige (kombinierbar) ----
    NO_BAKE(DietGroup.OTHER),
    AIR_FRYER(DietGroup.OTHER),
    INSTANT_POT(DietGroup.OTHER),
    ONE_POT(DietGroup.OTHER),
    MEAL_PREP(DietGroup.OTHER),
    SPICY(DietGroup.OTHER),
    ALCOHOL_FREE(DietGroup.OTHER);

    // ---- Internes Feld: Zu welcher Gruppe gehört dieser Tag? ----
    private final DietGroup group;

    // Konstruktor eines Enum-Konstanten: speichert die Gruppe
    DietTag(DietGroup group) {
        this.group = group;
    }

    // Zugriff auf die Gruppe (z. B. für Filter/Validierung)
    public DietGroup getGroup() {
        return group;
    }

    // Komfort: Ist dieser Tag ein Baseline-Tag?
    public boolean isBaseline() {
        return group == DietGroup.BASELINE;
    }

    // ---- Hilfsmethoden (nützlich für Regeln & UI) ----

    /** Liefert alle Baseline-Tags (VEGAN, VEGETARIAN, PESCETARIAN, OMNIVORE). */
    public static EnumSet<DietTag> baselines() {
        return filterByGroup(DietGroup.BASELINE);
    }

    /** Liefert alle Tags, die NICHT Baseline sind (alles andere). */
    public static EnumSet<DietTag> nonBaselines() {
        EnumSet<DietTag> all = EnumSet.allOf(DietTag.class);
        all.removeAll(baselines());
        return all;
    }

    /** Filtert alle Tags nach einer bestimmten Gruppe (z. B. fürs Frontend-Dropdown). */
    public static EnumSet<DietTag> filterByGroup(DietGroup group) {
        EnumSet<DietTag> set = EnumSet.noneOf(DietTag.class); // leeres EnumSet
        for (DietTag t : values()) {                          // alle Enum-Werte durchgehen
            if (t.group == group) set.add(t);
        }
        return set;
    }

    /**
     * Gruppiert alle Tags nach ihrer Gruppe:
     * Ergebnis ist eine Map: DietGroup -> Set<DietTag>
     * Praktisch für UI-Kataloge oder Filter.
     */
    public static java.util.Map<DietGroup, Set<DietTag>> grouped() {
        return java.util.Arrays.stream(values())      // alle Enum-Werte als Stream
                .collect(Collectors.groupingBy(       // gruppiere nach ...
                        DietTag::getGroup,           // ... der zugehörigen Gruppe
                        Collectors.toCollection(() -> EnumSet.noneOf(DietTag.class)) // sammle in EnumSet
                ));
    }

    /**
     * Gruppen-Typen, zu denen die Tags gehören.
     * BASELINE: genau ein Tag sinnvoll (Regel wird in Recipe geprüft).
     * ALLERGEN_FREE / MACRO / OTHER: frei kombinierbar.
     */
    public enum DietGroup {
        BASELINE,       // vegan / vegetarisch / pescetarisch / omnivor -> genau einer
        ALLERGEN_FREE,  // glutenfrei, laktosefrei, nussfrei -> kombinierbar
        MACRO,          // low-carb, high-protein, low-fat -> kombinierbar
        OTHER           // halal, koscher, no-bake, spicy, ... -> kombinierbar
    }
}
