// src/main/java/de/htw/berlin/webtech/recipe_manager/domain/DietTag.java
package de.htw.berlin.webtech.recipe_manager.domain;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum DietTag {

    // genau EIN Tag aus BASELINE ist sinnvoll
    VEGAN(DietGroup.BASELINE),
    VEGETARIAN(DietGroup.BASELINE),
    PESCETARIAN(DietGroup.BASELINE),
    OMNIVORE(DietGroup.BASELINE),

    // allergen-/unverträglichkeitsbezogen (beliebig kombinierbar)
    GLUTEN_FREE(DietGroup.ALLERGEN_FREE),
    LACTOSE_FREE(DietGroup.ALLERGEN_FREE),
    NUT_FREE(DietGroup.ALLERGEN_FREE),

    // religiös/ethisch (orthogonal zur BASELINE)
    HALAL(DietGroup.OTHER),
    KOSHER(DietGroup.OTHER),

    // Makro-/Lebensstil
    LOW_CARB(DietGroup.MACRO),
    HIGH_PROTEIN(DietGroup.MACRO),
    LOW_FAT(DietGroup.MACRO),

    // Technik/sonstige
    NO_BAKE(DietGroup.OTHER);

    private final DietGroup group;

    DietTag(DietGroup group) {
        this.group = group;
    }

    public DietGroup getGroup() {
        return group;
    }

    public boolean isBaseline() {
        return group == DietGroup.BASELINE;
    }

    // ---- Helfer/Optionals für Regeln & UI ----
    /** Alle Baseline-Tags. */
    public static EnumSet<DietTag> baselines() {
        return filterByGroup(DietGroup.BASELINE);
    }

    /** Alle Nicht-Baseline-Tags. */
    public static EnumSet<DietTag> nonBaselines() {
        EnumSet<DietTag> all = EnumSet.allOf(DietTag.class);
        all.removeAll(baselines());
        return all;
    }

    /** Tags nach Gruppe filtern. Praktisch für Dropdowns im Frontend. */
    public static EnumSet<DietTag> filterByGroup(DietGroup group) {
        EnumSet<DietTag> set = EnumSet.noneOf(DietTag.class);
        for (DietTag t : values()) if (t.group == group) set.add(t);
        return set;
    }

    /** Gruppierung als Map<Group, Set<Tag>> für Frontend-Kataloge. */
    public static java.util.Map<DietGroup, Set<DietTag>> grouped() {
        return java.util.Arrays.stream(values())
                .collect(Collectors.groupingBy(
                        DietTag::getGroup,
                        Collectors.toCollection(() -> EnumSet.noneOf(DietTag.class))
                ));
    }

    public enum DietGroup {
        BASELINE,       // vegan / vegetarisch / pescetarisch / omnivor -> genau einer
        ALLERGEN_FREE,  // glutenfrei, laktosefrei, nussfrei -> beliebig kombinierbar
        MACRO,          // low-carb, high-protein, low-fat -> kombinierbar
        OTHER           // halal, koscher, no-bake -> kombinierbar
    }
}
