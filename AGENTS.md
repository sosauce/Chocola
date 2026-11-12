# Chocola

- Chocola is an offline Android music player for browsing and playing a local music library.
- Work in the single `:app` module.

## Stack

- Kotlin, Jetpack Compose, Material 3 Expressive, Navigation 3, Koin, Room, DataStore Preferences, NekoBites.
- One Gradle module; versions and dependency aliases live in `gradle/libs.versions.toml`.

## Structure

```text
app/src/main/java/com/sosauce/chocola/
  app/                 Application, activity, playback service, Nav3, DI, widgets
  core/data/           Shared persistence, library scanning, media and platform helpers
  core/domain/         Shared domain models and utilities
  core/designsystem/   Theme and reusable design components
  core/presentation/   Shared UI, player, navigation and preference helpers
  feature/<name>/      Feature code; existing features commonly use presentation/ and data/
app/src/main/res/      Strings, drawables and other Android resources
```

## Commands

- `./gradlew :app:assembleDebug`
- `./gradlew :app:installDebug`
- `./gradlew :app:lintDebug`
- `./gradlew :app:testDebugUnitTest` (no tests yet)
- `./gradlew :app:connectedDebugAndroidTest` (no tests yet)
- Don't run Gradle builds unprompted; ask first.

## Architecture and conventions

- Feature ViewModels expose `StateFlow` UI state; Compose collects with `collectAsStateWithLifecycle()`.
- Keep screens stateless where practical; route actions to ViewModels. One-off effects use event flows (see Bakery).
- Navigation keys are `Screen` types in `core/presentation/navigation/Screen.kt`; entries and ViewModel creation are in `app/navigation/Navigation.kt`.
- Register shared dependencies/ViewModels in `app/di/AppModule.kt`; Bakery has separate wiring in `app/di/BakeryModule.kt`.
- Persist preferences through `PreferencesKeys` and `UserPreferences`; Compose preference helpers are in `core/presentation/preferences/`.
- Room databases are separate: `PlaylistDatabase` (`playlist.db`) and `BakeryDatabase` (`bakery.db`); their DAO/entity types are beside each database.

## Checklists

1. **Add a screen:** Add a serializable `Screen` key in `core/presentation/navigation/Screen.kt`; add its `entry<Screen.X>` in `app/navigation/Navigation.kt`; create feature UI/state/ViewModel in the feature package; register the ViewModel in `app/di/AppModule.kt` or the feature's existing module.
2. **Add a setting:** Add a typed key in `core/data/datastore/DataStore.kt` (`PreferencesKeys`); add Flow/read and edit/write access in `core/data/datastore/UserPreferences.kt`; connect it through the existing setting UI/preference helper; add user-facing text to `app/src/main/res/values/strings.xml`.
3. **Add a Room table:** Add the entity and DAO in the appropriate `core/data` database package; include them in the database `@Database`; bump its version and add/register a migration (Playlist wiring: `core/data/local/PlaylistDatabase.kt`, `app/di/AppModule.kt`; Bakery wiring: `core/data/bakery/BakeryDatabase.kt`, `app/di/BakeryModule.kt`).
4. **Add a dependency:** Add its version and library/plugin alias to `gradle/libs.versions.toml`; reference the alias from `app/build.gradle.kts`. Never put a version directly in the build script.

## Rules

- Match existing code, package layout and naming before introducing new patterns.
- Put all user-facing text in Android string resources; use `stringResource` or `context.getString`.
- Use theme colors and existing Compose/NekoBites components.
- Never hardcode user-facing strings or colors.
- Never add dependencies without asking.
- Never edit generated files.
- Never rewrite unrelated code.
- Never skip a Room schema migration.

## Gotchas

- `namespace` is `com.sosauce.chocola`, but `applicationId` is `com.sosauce.cutemusic` (debug adds `.debug`); do not normalize them casually.
- Koin starts from `app/App.kt`; preserve its existing module composition when registering dependencies.
- `PlaylistDatabase` is version 3 with migrations through `MIGRATION_2_3`; `BakeryDatabase` is a separate version-1 database with no migrations registered yet.
- Nav3 entry decorators in `Navigation.kt` provide saved-state and ViewModel stores; keep entry-scoped ViewModels inside their entries.
- There are no `app/src/test` or `app/src/androidTest` source trees; don't assume tests cover a change.
