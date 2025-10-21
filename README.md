
# Recipe Manager – Backend
A tiny web application to gather and search for cooking/baking recipes. This app is created in the context of the course Webtechnologien at the HTW-Berlin.

## Funktionsumfang

- **Technologie:** Spring Boot 3.5, Spring Web, Spring Data JPA, Bean Validation, PostgreSQL
- **Domainmodell**
    - `Recipe`: `id, title, description, prepMinutes, cookMinutes, categories(Set<String>), dietTags(Set<DietTag>), ingredients(List<Ingredient>), steps(List<Step>)`
    - `Ingredient`: `id, name, amount(BigDecimal), unit, recipe`
    - `Step`: `id, position, text, recipe`
    - `DietTag` (Enum) mit **Baseline-Tags** (VEGAN / VEGETARIAN / PESCETARIAN / OMNIVORE) + kombinierbaren Tags (z. B. GLUTEN_FREE, HALAL, LOW_CARB …)
- **Regeln & Validierung**
    - **Maximal ein Baseline-Tag** pro Rezept (Server-Validierung → **400** bei Verstoß)
    - Pflichtfelder (z. B. `title`, Minuten ≥ 0) via Bean Validation → **400** mit Fehlermeldung
    - Nicht vorhandene IDs → **404**
- **REST-API**
  - `GET /recipes` → Liste (inkl. Zutaten/Schritte)
- `GET /recipes/{id}` → Einzelnes Rezept (404 wenn nicht vorhanden)
- `POST /recipes` → erstellt Rezept → `201 { "id": <neu> }`
- `PUT /recipes/{id}` → **Basisfelder aktualisieren** (title, description, prepMinutes, cookMinutes, dietTags, categories) → `204`
- `DELETE /recipes/{id}` → löscht Rezept → `204`

 **Sub-Resources (Listen werden vollständig ersetzt)**
- `PUT /recipes/{id}/ingredients` → ersetzt **gesamte Zutatenliste** → `204`
- `PUT /recipes/{id}/steps` → ersetzt **gesamte Schrittliste** → `204`

### Fehlerverhalten
- Validation (fehlende/ungültige Felder) → `400`
- Baseline-Regel verletzt (mehr als 1 Baseline-Tag) → `400`
- Nicht gefunden → `404`
- **Sonstiges**
    - CORS konfigurierbar (lokal + Render-Frontend)
    - DB-Konfiguration über ENV (`DB_URL` im JDBC-Format, `DB_USERNAME`, `DB_PASSWORD`)
    - `spring.jpa.hibernate.ddl-auto=update` für automatische Schema-Migration
