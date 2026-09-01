# Java 21 Upgrade Workplan

Date: 2026-09-01  
Status: Draft — defer until Phase 2.8 parse-timeout work is under control  
Audience: PSS parser maintainers

**Current**: Java 17 (`maven.compiler.release=17` in `parse/pom.xml`; Eclipse/IDE targets JavaSE-17)  
**Target**: Java 21 LTS  
**Scope**: `parse/` module only (`pss-parse-docs` packages markdown — no Java sources)

**Not the current priority.** Phase 2.8 (parse latency / Panto 90s timeouts) is active work on the Java 17 toolchain. This plan is a small, separable bump once that work lands.

---

## Why this is low-risk

- Java 17 → 21 is one LTS hop.
- No Spring Boot, no `javax`→`jakarta` migration.
- No `module-info.java` — `pss-parse` is a classic classpath JAR, not a JPMS module.
- Key build deps already support Java 21: ANTLR 4.13.1, JaCoCo 0.8.12, Surefire 3.2.5, maven-compiler-plugin 3.11.0.
- Production code uses public JDK reflection APIs only; no `sun.*` / `jdk.internal.*` imports.

---

## Steps

### Step 1 — Capture baseline (on current JDK 17)

```bash
mvn -f parse/pom.xml clean test
```

Record tests run, failures, and elapsed time. This is the acceptance floor. Do not mix in unrelated golden/token-id churn from other branches.

### Step 2 — Install JDK 21 and point the toolchain at it

Compiler `release=21` requires a **JDK 21** on the path used by Maven and the IDE.

```bash
java -version          # should report 21 after switch
echo "$JAVA_HOME"
mvn -version           # should show Java version: 21
```

macOS (Homebrew) example:

```bash
brew install openjdk@21
export JAVA_HOME=$(brew --prefix openjdk@21)
export PATH="$JAVA_HOME/bin:$PATH"
```

Or install from [Adoptium](https://adoptium.net).

In Cursor/VS Code, set **Java: Configure Java Runtime** so the Language Server and Test Runner also use JDK 21 (not only Maven CLI).

### Step 3 — Bump compiler release target

In `parse/pom.xml`:

```xml
<maven.compiler.release>17</maven.compiler.release>
```

→

```xml
<maven.compiler.release>21</maven.compiler.release>
```

(`maven-compiler-plugin` already binds `source`/`target` to `${maven.compiler.release}`.)

### Step 4 — Align IDE/Eclipse metadata (optional but avoids drift)

If you use the checked-in Eclipse project under `parse/`:

| File | Properties | Old | New |
|------|------------|-----|-----|
| `parse/.classpath` | JRE container | `JavaSE-17` | `JavaSE-21` |
| `parse/.settings/org.eclipse.jdt.core.prefs` | `compiler.compliance` | `17` | `21` |
| `parse/.settings/org.eclipse.jdt.core.prefs` | `compiler.codegen.targetPlatform` | `17` | `21` |
| `parse/.settings/org.eclipse.jdt.core.prefs` | `compiler.source` | `17` | `21` |

Then **Java: Clean Java Language Server Workspace** in the IDE.

### Step 5 — Verify compilation

```bash
mvn -f parse/pom.xml clean test-compile
```

Expected: clean compile with **no** source changes required.

### Step 6 — Run full test suite

```bash
mvn -f parse/pom.xml clean test
```

Compare pass/fail counts and timing to Step 1.

**Reflection note (`SqlParseMCPTest`):** lines 351 and 438 call `setAccessible(true)` on **private methods of `cli.SqlParseMCP` in the same unnamed module**. That is allowed on Java 21 for non-JDK types. You do **not** need `--add-opens` (that directive applies to named JPMS modules, which this project does not use). If those tests fail after the bump, fix the test hooks (package-visible helpers) — do not cargo-cult `--add-opens pss/cli=ALL-UNNAMED`; that module name does not exist here.

### Step 7 — Bump Gson (recommended, not required for Java 21)

`gson:2.9.0` is dated. Consider `2.13.1` in `parse/pom.xml` for CVE fixes. Gson 2.x API is stable — rerun tests after the bump.

### Step 8 — Commit

Suggested message:

```
chore(parse): target Java 21 and bump gson to 2.13.1
```

---

## What you are NOT doing here

- Migrating JUnit 4 → JUnit 5 (JUnit 4 runs on Java 21 as-is)
- Changing ANTLR (4.13.1 is current and Java-21-compatible)
- Fixing Phase 2.8 parse timeouts (separate work — see `parser-defects-enhancements-workplan.md` §2.8 and `parse/docs/rmcp-handoff/5.1.3-panto-outstanding/panto-513-parse-timeouts-2026-08-19.md`)
- Adding CI pipeline files (none in repo today)

---

## Future: Java 25 LTS

If you later target Java 25, repeat the same pattern: install JDK, bump `maven.compiler.release`, refresh Eclipse prefs, run tests. JUnit 4 may eventually need explicit vintage-engine wiring in new tooling — not an issue for this Maven/Surefire setup today.

---

## Quick reference — files to touch

| File | Property / setting | Old | New |
|------|-------------------|-----|-----|
| `parse/pom.xml` | `maven.compiler.release` | `17` | `21` |
| `parse/pom.xml` | `gson` version (optional) | `2.9.0` | `2.13.1` |
| `parse/.classpath` | JRE container | `JavaSE-17` | `JavaSE-21` |
| `parse/.settings/org.eclipse.jdt.core.prefs` | compliance / source / targetPlatform | `17` | `21` |
