
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
    - `GET  /recipes` – Liste aller Rezepte
    - `GET  /recipes/{id}` – Einzelnes Rezept inkl. Zutaten/Schritten
    - `POST /recipes` – Rezept anlegen, **Antwort:** `{ "id": <neu> }`
    - `PUT  /recipes/{id}` – **Voll-Update** (Zutaten/Steps werden ersetzt)
    - `DELETE /recipes/{id}` – Rezept löschen (**204**)
- **Sonstiges**
    - CORS konfigurierbar (lokal + Render-Frontend)
    - DB-Konfiguration über ENV (`DB_URL` im JDBC-Format, `DB_USERNAME`, `DB_PASSWORD`)
    - `spring.jpa.hibernate.ddl-auto=update` für automatische Schema-Migration
