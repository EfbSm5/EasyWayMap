# Repository Guidelines

## Project Structure & Module Organization

EasyWay is a single-module Android app. Kotlin lives under `app/src/main/java/com/efbsm5/easyway/`. Keep Compose code in `ui/`, screen state in `viewmodel/` and `contract/`, data access in `repo/` and `data/`, and shared models in `model/`. Resources are in `app/src/main/res/`; bundled images and sample data belong in `app/src/main/assets/`. JVM tests use `app/src/test/`, while device and Compose UI tests use `app/src/androidTest/`. Dependency versions are centralized in `gradle/libs.versions.toml`.

## Build, Test, and Development Commands

Run all commands from the repository root with the checked-in Gradle wrapper:

- `./gradlew assembleDebug` builds a debuggable APK.
- `./gradlew installDebug` installs it on a connected emulator or device.
- `./gradlew testDebugUnitTest` runs local JUnit tests.
- `./gradlew connectedDebugAndroidTest` runs Espresso and Compose tests on a connected device.
- `./gradlew lintDebug` performs Android static analysis.

Use JDK 19, matching the module targets. Android Studio can launch `app` for interactive map testing.

## Coding Style & Naming Conventions

Follow standard Kotlin style with four-space indentation and trailing commas in multiline declarations. Use `PascalCase` for classes, ViewModels, and `@Composable` functions; `camelCase` for methods and properties; and `UPPER_SNAKE_CASE` for constants. Keep Composables focused on rendering, expose state from ViewModels, and coordinate persistence or networking in repositories. Prefer immutable UI state and explicit Event/State/Effect types. Run Android Studio formatting and `lintDebug`; no separate formatter is configured.

## Testing Guidelines

Tests use JUnit 4, AndroidX Test/Espresso, and Compose UI APIs. Name classes after the subject (`RoutePlanRepositoryTest`) and methods after behavior (`returnsEmptyRoute_whenLocationMissing`). Add local tests for state and repository logic; use `androidTest` for navigation, permissions, map rendering, or Android-dependent behavior. No coverage threshold is enforced, but changed behavior should have regression coverage.

## Commit & Pull Request Guidelines

Recent history favors short, imperative, single-purpose subjects in English or Chinese (for example, `refactor HomePage and ViewModel`). Avoid vague messages such as `simple change`. Pull requests should explain the problem, implementation, and verification; link the issue; and include before/after media for UI or map changes. Note the tested device and API level for location, permission, or external-map behavior.

## Security & Configuration

Do not commit new API keys, signing material, or machine-specific paths. Keep local SDK configuration in ignored `local.properties`, and route environment-specific endpoints through Gradle or `BuildConfig`. Review manifest permission changes carefully and document why each added permission is required.
