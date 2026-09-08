# Java 21 Upgrade Workplan

Date: 2026-09-01  
**Status: ✅ Complete (2026-09-04)** — delivered on branch `Fall-2026-Extensions`  
Audience: PSS parser maintainers

**Delivered**: Java 21 (`maven.compiler.release=21` in `parse/pom.xml`; Eclipse/IDE targets JavaSE-21)  
**Artifact version**: parent `5.1.4-1`  
**Scope**: `parse/` module (`pss-parse-docs` packages markdown — no Java sources)

---

## Closeout summary

| Item | Result |
|------|--------|
| Compiler target | `maven.compiler.release` **21** in `parse/pom.xml` |
| Gson | **2.13.1** |
| ANTLR | **4.13.2** (bumped after initial 4.13.1 Java 21 land) |
| IDE metadata | `parse/.classpath`, Eclipse prefs, `.vscode/settings.json` → JavaSE-21 |
| Golden refresh | Walker / AccessObject / QCD goldens refreshed for JDK 21 `HashMap` iteration order in `.toString()` comparisons (semantics unchanged) |
| Refresh tooling | `parse/tools/refresh_walker_goldens.py` (+ `AccessObjectGoldenCaptureOnce`) |
| Full suite | **2175** tests, **0** failures (Sep 2026) |
| CI | `.github/workflows/maven-test.yml` — Temurin JDK 21, `mvn -B test` |
| Phase 2.8 prerequisite | ✅ Closed before upgrade (timeout corpus complete) |

**Operator note:** Maven and the CLI require **JDK 21** on `PATH` / `JAVA_HOME`. If `mvn` reports `release version 21 not supported`, the shell is still pinned to JDK 17 (common when `~/.zshrc` exports `openjdk@17`). Use:

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
mvn -version   # Java version: 21
```

---

## Why this was low-risk

- Java 17 → 21 is one LTS hop.
- No Spring Boot, no `javax`→`jakarta` migration.
- No `module-info.java` — `pss-parse` is a classic classpath JAR, not a JPMS module.
- Build deps support Java 21: ANTLR 4.13.x, JaCoCo 0.8.12, Surefire 3.2.5, maven-compiler-plugin 3.11.0.
- Production code uses public JDK reflection APIs only; no `sun.*` / `jdk.internal.*` imports.

---

## Steps (historical — all complete)

### Step 1 — Capture baseline (on JDK 17) ✅

```bash
mvn -f parse/pom.xml clean test
```

Recorded tests run, failures, and elapsed time as the acceptance floor.

### Step 2 — Install JDK 21 and point the toolchain at it ✅

Compiler `release=21` requires a **JDK 21** on the path used by Maven and the IDE.

```bash
java -version          # should report 21 after switch
echo "$JAVA_HOME"
mvn -version           # should show Java version: 21
```

macOS (Homebrew) example:

```bash
brew install openjdk@21
export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

Or install from [Adoptium](https://adoptium.net).

In Cursor/VS Code, set **Java: Configure Java Runtime** so the Language Server and Test Runner also use JDK 21 (not only Maven CLI).

### Step 3 — Bump compiler release target ✅

In `parse/pom.xml`:

```xml
<maven.compiler.release>21</maven.compiler.release>
```

(`maven-compiler-plugin` binds `source`/`target` to `${maven.compiler.release}`.)

### Step 4 — Align IDE/Eclipse metadata ✅

| File | Setting | Value |
|------|---------|-------|
| `parse/.classpath` | JRE container | `JavaSE-21` |
| `parse/.settings/org.eclipse.jdt.core.prefs` | compliance / source / targetPlatform | `21` |
| `.vscode/settings.json` | default runtime | JDK 21 |

### Step 5 — Verify compilation ✅

```bash
mvn -f parse/pom.xml clean test-compile
```

Clean compile with no production source changes required.

### Step 6 — Run full test suite ✅

```bash
mvn -f parse/pom.xml clean test
```

**JDK 21 golden note:** Failures after the compiler bump were almost entirely **string ordering** in golden `.toString()` comparisons (JDK 21 `HashMap` iteration order). Fix by regenerating expected strings, not by changing walker semantics. Use `WalkerGoldenCaptureOnce` / `refresh_walker_goldens.py` — do not patch from Surefire truncated `expected:<…>` messages.

**Reflection note (`SqlParseMCPTest`):** `setAccessible(true)` on private methods of `cli.SqlParseMCP` in the same unnamed module is allowed on Java 21. No `--add-opens` required (no JPMS module in this project).

### Step 7 — Bump Gson ✅

`gson` → **2.13.1** in `parse/pom.xml`.

### Step 8 — Commit ✅

Landmark commits on `Fall-2026-Extensions` include toolchain + golden refresh batches and follow-on ANTLR/CI work.

---

## What was out of scope (unchanged)

- Migrating JUnit 4 → JUnit 5 (JUnit 4 runs on Java 21 as-is)
- Fixing Phase 2.8 parse timeouts (closed separately before this upgrade)
- Bulk lexer token-id golden refresh across unrelated tests (see `.cursor/rules/lexer-token-id-golden-policy.mdc`)

---

## Future: Java 25 LTS

If you later target Java 25, repeat the same pattern: install JDK, bump `maven.compiler.release`, refresh Eclipse prefs, run tests, refresh any hash-order-sensitive goldens. JUnit 4 may eventually need explicit vintage-engine wiring in new tooling — not an issue for this Maven/Surefire setup today.

---

## Quick reference — files touched

| File | Property / setting | Value |
|------|-------------------|-------|
| `parse/pom.xml` | `maven.compiler.release` | `21` |
| `parse/pom.xml` | `gson` version | `2.13.1` |
| `parse/pom.xml` | `antlr4.version` | `4.13.2` |
| `parse/.classpath` | JRE container | `JavaSE-21` |
| `parse/.settings/org.eclipse.jdt.core.prefs` | compliance / source / targetPlatform | `21` |
| `.github/workflows/maven-test.yml` | CI Java version | `21` |
