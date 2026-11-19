# pss-parse-docs (Maven artifact)

Maven module packaging the **pss-parse consumer documentation bundle** for a specific API version as a documentation JAR and an optional agent-kit ZIP. The JAR remains **Option C** for sharing documentation alongside the parser.

## Artifact coordinates

| Field | Value |
|-------|-------|
| `groupId` | `pss` |
| `artifactId` | `pss-parse-docs` |
| `version` | Same as `pss-parse` (currently `5.1.4-1`) |

## Bundled documents

See `META-INF/pss-parse/pss-parse-docs.manifest.json` for the machine-readable list. The bundle includes:

| Document | Role |
|----------|------|
| `symbol-table-bucket-reference.md` | Primary consumer guide for symbol-table buckets, tracing algorithms, and scope semantics |
| `table-and-query-dictionary-design.md` | Design contract for global and per-scope `table_dictionary` and `query_dictionary` |
| `relational-modifier-resolution-policy.md` | PIVOT/UNPIVOT semantics, `derivation` buckets, and modifier column lineage |
| `sql-grammar-extensions-since-2026-01-01.md` | Parse coverage and grammar extensions for pss-parse 5.1.4+ |
| `phase-17.6.9-window-query-dictionary-policy.md` | Window clause policy: `window_partition_by` / `window_ordered_by` vs `query_dictionary` |
| `ordered-select-list-output-alias-policy.md` | Ordered select-list aliases and interface lineage |
| `set-operation-interface-duplicate-output-names-policy.md` | Set-op branch interface distinctness; duplicate output names, diagnostic cascade, author remediation |
| `global-table-dictionary-cte-alias-policy.md` | 5.1.4 global `tableDictionary`: CTE/query aliases via `table_alias` |

## Build

From the repository root:

```bash
mvn -pl pss-parse-docs package
```

Outputs:

- `pss-parse-docs/target/pss-parse-docs-5.1.4-1.jar`
- `pss-parse-docs/target/pss-parse-docs-5.1.4-1-agent-kit.zip`

Building also refreshes `docs/vendor/pss-parse/<api-version>/` for Option D (submodule/subtree) consumers.

### Agent kit (manual / non-Maven consumers)

The ZIP contains `PSS-PARSER-AGENT-KIT.md`, the eight consumer guides, and a
`pss-parse-docs.manifest.json` whose paths are relative to the extracted
directory. Extract into an empty staging directory, open `PSS-PARSER-AGENT-KIT.md`, and
follow its installation instructions. Copy the complete kit into the dependent
project's versioned vendor directory, then merge its small instruction block
into the project's enabled agent configuration. Existing instructions are
never automatically overwritten.

The PSS-specific guide name avoids collisions with other libraries' agent-kit
instructions; keep each kit's supporting documents in its own vendor directory.

The ZIP reuses the documentation JAR's staged guides and the generated vendor
manifest. Maven filters artifact/API versions into the
[maintained bootstrap](src/agent-kit/PSS-PARSER-AGENT-KIT.md); no separate ZIP guide
allowlist or hand-maintained copy of the consumer docs is needed.
Changing a bundled canonical guide and running `package` refreshes both
formats. Version changes follow the parent POM properties.

The kit supplements, rather than replaces, the documentation JAR. It is
generated locally during `package`; it is not attached as an additional Maven
publication artifact. Neither this build nor kit creation authorizes an
Archiva upload.

## JAR layout

```text
META-INF/pss-parse/<api-version>/
  symbol-table-bucket-reference.md
  table-and-query-dictionary-design.md
  relational-modifier-resolution-policy.md
  sql-grammar-extensions-since-2026-01-01.md
  phase-17.6.9-window-query-dictionary-policy.md
META-INF/pss-parse/
  pss-parse-docs.manifest.json
symbol-table-bucket-reference-pss-parse-<api-version>.md   # primary doc at JAR root
```

Manifest entries (`PSS-Parser-Artifact`, `PSS-Parser-Version`, `PSS-Parser-Api-Version`) identify the parser release.

## Consumer dependency (Maven)

Add the dependency aligned with your `pss-parse` version:

```xml
<dependency>
  <groupId>pss</groupId>
  <artifactId>pss-parse-docs</artifactId>
  <version>5.1.4-1</version>
</dependency>
```

### Unpack into your project (optional)

Use the Maven dependency plugin during `generate-resources`:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-dependency-plugin</artifactId>
  <executions>
    <execution>
      <id>unpack-pss-parse-docs</id>
      <phase>generate-resources</phase>
      <goals>
        <goal>unpack</goal>
      </goals>
      <configuration>
        <artifactItems>
          <artifactItem>
            <groupId>pss</groupId>
            <artifactId>pss-parse-docs</artifactId>
            <version>${pss.parse.version}</version>
            <outputDirectory>${project.build.directory}/pss-parse-docs</outputDirectory>
            <includes>META-INF/pss-parse/**</includes>
          </artifactItem>
        </artifactItems>
      </configuration>
    </execution>
  </executions>
</plugin>
```

Point Cursor rules, agent prompts, or internal docs at the bundle directory:

```text
target/pss-parse-docs/META-INF/pss-parse/5.1.4/
```

## Publish (GitHub Packages)

Publishing uses the `github-packages` profile on the parent POM:

```bash
mvn -Pgithub-packages deploy
```

Configure `~/.m2/settings.xml` with a GitHub personal access token (`read:packages`, `write:packages`) for server id `github`.

Consumers add the GitHub Packages repository and the same `pss-parse-docs` dependency.

## Version alignment

- **Maven version** (`5.1.4-1`): artifact coordinates; must match `pss-parse`.
- **API version** (`5.1.4`): docs path segment and `parserVersionMin` in the manifest.

When bumping the parser, update `pss.parse.api.version` in the root `pom.xml` if the public API version changes.
