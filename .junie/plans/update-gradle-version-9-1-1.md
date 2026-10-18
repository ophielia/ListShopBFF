---
sessionId: session-261018-092342-ctds
---

# Requirements

### Overview & Goals
Evaluate the required effort and provide the implementation steps to update the Gradle build tool / plugin to version `9.1.1`.

### Scope
#### In Scope
- Updating Gradle wrapper distribution configuration to version `9.1.1`.
- Verifying compatibility with Kotlin Multiplatform `2.2.0`, Android Gradle Plugin `8.12.0`, and companion plugins (SQLDelight, SKIE, KMMBridge, Mokkery, Kover).
- Validating build, test, and publishing tasks across all modules.

#### Out of Scope
- Major architectural changes or refactoring of existing build scripts unless required by deprecation in the target version.
- Upgrading unrelated third-party library dependencies.

### Effort Estimate
- **Gradle Wrapper (9.1.0 -> 9.1.1):** ~5–10 minutes (Trivial patch upgrade).
- **Android Gradle Plugin (8.12.0 -> 9.x):** ~1–3 hours (Requires validating experimental KMP Android plugin DSL and multiplatform integrations).

# Technical Design

### Current Implementation
- Gradle Wrapper is configured at `gradle-9.1.0-bin.zip` in `gradle/wrapper/gradle-wrapper.properties`.
- Android Gradle Plugin is set to `8.12.0` in `gradle/libs.versions.toml`.
- Kotlin Multiplatform version is `2.2.0`.

### Key Decisions
- **Upgrade Target:** Update `distributionUrl` in `gradle/wrapper/gradle-wrapper.properties` to `gradle-9.1.0-bin.zip` -> `gradle-9.1.1-bin.zip`.
- **Validation Strategy:** Run clean builds and full test passes across Android host tests, iOS simulator tests, and sample Android application builds.

### Proposed Changes
- Update `gradle/wrapper/gradle-wrapper.properties` to target Gradle `9.1.1`.
- If updating plugin versions, bump the corresponding entry in `gradle/libs.versions.toml`.

### File Structure
- `gradle/wrapper/gradle-wrapper.properties` (modified)
- `gradle/libs.versions.toml` (optional / modified if updating plugin catalog)

# Testing

### Validation Approach
Verify that all existing tasks (compilation, testing, and publishing) function identically under Gradle `9.1.1`.

### Key Scenarios
- **Wrapper Initialization:** Ensure `./gradlew --version` reports Gradle `9.1.1` and launcher JVM 21.
- **Module Compilation:** Verify `./gradlew assemble` builds `:listshop`, `:analytics`, `:allshared`, and `:testapps:android`.
- **Test Suite:** Run `./gradlew allTests` to confirm zero regression across common, host, and native test suites.
- **Artifact Publishing:** Confirm `./gradlew publishToMavenLocal` publishes artifacts without configuration cache or metadata errors.

# Delivery Steps

###   Step 1: Update Gradle Configuration
Update Gradle wrapper configuration or version catalog to target version 9.1.1.

- Update `gradle/wrapper/gradle-wrapper.properties` or `gradle/libs.versions.toml` to specify version `9.1.1`.
- Regenerate wrapper checksums and metadata via `./gradlew wrapper --gradle-version 9.1.1` if upgrading the wrapper.
- Verify Gradle daemon initialization and plugin resolution with `./gradlew --version`.

###   Step 2: Verify Builds and Test Execution
Ensure all module builds, test suites, and packaging tasks execute cleanly without regressions.

- Execute `./gradlew clean build` to verify multiplatform targets (`listshop`, `analytics`, `allshared`, `testapps:android`).
- Run `./gradlew allTests` to validate Android host and native simulator unit tests.
- Verify local artifact publishing via `./gradlew publishToMavenLocal`.