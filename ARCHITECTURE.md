# NewTerm — file map (all files ≤100 LoC, enforced by `./gradlew checkLoc`)
Kotlin, minSdk 14, no XML except AndroidManifest, no AndroidX, no JSON. UI is built in code.
Package `com.newterm` (app/src/main/kotlin):
- `db/` SQLite (normalized): `Schema` DDL, `Seeds` initial rows (add a distro here), `Db` helper, one `*Dao` per table
  (session, terminal, bind_mount, distro, setting+setting_def).
- `core/` `Session` (dirs), `Terminal` (process+buffer), `SessionManager` (live terminals, lifecycle).
- `backend/` `Backend` interface; `Blank`/`Emuroot`/`Distro` impls; register new ones in `BackendRegistry`.
- `emuroot/` `EmurootInstaller` (ABI + asset copy), `EmurootRunner` (builds argv, applies `bind_mount`).
- `distro/` `Distro`, `Downloader`, `TarGzExtractor`, `DistroInstaller`.
- `perm/` `Permission` enum, `PermissionManager.granted/ensure`.
- `storage/` `StorageVolumes` (volumes list), `SessionStorage` (toggle bind mounts per session).
- `theme/` `Theme` fields, `Themes` LIGHT/DARK, `ThemeStore.current(ctx)`.
- `ui/dsl/` `Views` (vbox/hbox/label/add), `Shapes`, `Widgets` (button/chip/card/input).
- `ui/` screens: `Main`, `Session`, `Settings`, `Permissions` activities (extend `BaseActivity.build(theme)`),
  `NewSessionDialog`, `StorageDialog`, `TabBar`, `TerminalView`, `InputBar`.
- `term/` VT/xterm emulator: `Emulator` (state), `Parser` (escapes), `Csi`, `Modes`, `Pen`/`Palette` (colours),
  `Screen`+`ScreenOps` (grid), `Scrollback`; UI: `TermView`, `TermPainter`, `TermInput`, `Keys`.
- `core/Pty` runs commands on a real pty via `native/ptyhelper.c` (static C helper, no JNI).
- `util/` `Symlink` (API 14 safe).
Binaries: `app/fetch-emuroot.gradle.kts` + `app/ptyhelper.gradle.kts` build static musl emuroot/ptyhelper into `assets/emuroot/<abi>/`.
CI: `.github/workflows/build.yml` builds the APK on push/PR; tag `v*` publishes a GitHub release.
Tests: `./gradlew testDebugUnitTest` (emulator unit tests).
