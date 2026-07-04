---
sessionId: session-260704-090205-18tr
---

# Requirements

### Overview & Goals
The goal is to fix the compilation error in `ListShopDatabase.kt` caused by missing SqlDelight column adapters. These adapters became necessary after the recent addition of custom types (`AS Int` and `AS Boolean`) in the `.sq` database definition files.

### Scope
- **In Scope**:
    - Explanation of the origin of the new adapter requirements.
    - Implementation of shared `Int` and `Boolean` column adapters.
    - Updating the `ListshopDb` constructor call in `ListShopDatabase.kt`.
- **Out of Scope**:
    - Changing the database schema or column types.
    - Modifying existing repository logic (other than ensuring they continue to work with the updated database instance).

# Technical Design

### Current Implementation
The project uses SqlDelight 2.x, which requires explicit `ColumnAdapter`s when a column is defined with a custom type using the `AS` keyword (e.g., `INTEGER AS Int`). 

The following tables in `ListDefinition.sq`, `LayoutDefinition.sq`, and `TagDefinition.sq` now use these keywords:
- `ShoppingListEntity` (`itemCount` as `Int`, `isStarter` as `Boolean`)
- `ListCategoryEntity` (`displayOrder` as `Int`)
- `ListItemEntity` (`usedCount` as `Int`, `wholeQuantity` as `Int`)
- `ListItemDetailEntity` (`containsUnspecified` as `Boolean`, `wholeQuantity` as `Int`)
- `LayoutEntity` (`isDefault` as `Boolean`)
- `LayoutCategoryEntity` (`isDefault` as `Boolean`)
- `TagEntity` (`isGroup` as `Boolean`)

The current `ListShopDatabase.kt` instantiates `ListshopDb` using only the `sqlDriver`, which fails because the generated constructor now expects the adapters for these tables.

### Key Decisions
- **Provide Adapters in ListShopDatabase**: Instead of reverting the column types to plain `INTEGER` (which would require manual `Long` to `Int` conversion in Kotlin), we will provide the required adapters. This keeps the Kotlin models clean and leverages SqlDelight's type mapping.
- **Shared Adapters**: Since all custom columns use either `Int` or `Boolean`, we will define two shared adapter instances (`intAdapter` and `booleanAdapter`) and reuse them across all table adapters.

### Proposed Changes
- **ListShopDatabase.kt**:
    - Add the necessary imports for `ColumnAdapter` and the generated Entity classes.
    - Define `intAdapter` and `booleanAdapter`.
    - Update the `db` property to pass all 7 required table adapters (including the 4 mentioned in the error message and the others found in the schema).

### File Structure Changes
- **Modified**: `listshop/src/commonMain/kotlin/com/listshop/bff/repositories/ListShopDatabase.kt`

### Risks & Mitigations
- **Adapter Parameter Names**: SqlDelight parameter names in the generated constructor follow a specific pattern (e.g. `ShoppingListEntityAdapter`). We will match the names exactly as reported in the compilation error.
- **Missing Adapters**: If other tables also require adapters, the compiler will continue to report them. We have proactively identified all `AS` usages in the current `.sq` files to minimize subsequent errors.

# Delivery Steps

### ✓ Step 1: Define shared column adapters in ListShopDatabase.kt
Define reusable SqlDelight column adapters for `Int` and `Boolean` types in `ListShopDatabase.kt`.

- Add `import app.cash.sqldelight.ColumnAdapter` to `ListShopDatabase.kt`.
- Implement `intAdapter` to convert between Kotlin `Int` and SqlDelight `Long`.
- Implement `booleanAdapter` to convert between Kotlin `Boolean` and SqlDelight `Long` (0/1).
- Ensure these adapters are defined as private properties for use within the file.

### ✓ Step 2: Update ListshopDb instantiation with table-specific adapters
Provide the required table adapters to the `ListshopDb` constructor to fix the compilation error.

- Import generated entity classes (`ShoppingListEntity`, `ListItemEntity`, etc.) from `com.listshop.bff.db`.
- Update the `db` property initialization in `ListShopDatabase` to pass all missing adapter parameters.
- For each table with custom types, instantiate its `Adapter` class (e.g., `ShoppingListEntity.Adapter`) using the shared `intAdapter` and `booleanAdapter`.
- Map the adapter parameters (like `itemCountAdapter`, `isStarterAdapter`) to the corresponding shared adapters.