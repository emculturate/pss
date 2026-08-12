# pss-parse consumer documentation (vendor tree)

This directory holds **version-pinned** copies of the symbol-table consumer guide for a specific `pss-parse` API release. It is the source layout for **Option D** (git submodule or subtree in consumer repositories).

## Layout

```
docs/vendor/pss-parse/
  latest.version                 # current API version string (e.g. 5.1.3)
  <api-version>/
    symbol-table-bucket-reference.md
    symbol-table-bucket-reference-pss-parse-<api-version>.md
    table-and-query-dictionary-design.md
    relational-modifier-resolution-policy.md
    sql-grammar-extensions-since-2026-01-01.md
    phase-17.6.9-window-query-dictionary-policy.md
    pss-parse-docs.manifest.json   # lists each document id, role, and path
```

The canonical editable source lives at `parse/documents/symbol-table-bucket-reference.md`. The vendor tree is regenerated when you build the `pss-parse-docs` Maven module (or run `scripts/sync-pss-parse-docs-vendor.sh` directly).

## Option D — git submodule in a consumer repo

Pin the **pss** repository at a release tag and mount only the vendor path you need.

### 1. Add submodule

From your consumer repository root:

```bash
git submodule add -b Spring-2026-Extensions \
  https://github.com/emculturate/pss.git docs/vendor/pss
```

Use the branch or tag that matches your `pss-parse` dependency. Prefer a release tag when available, for example `pss-parse-5.1.3`.

### 2. Point agents or docs at the versioned file

```text
docs/vendor/pss/docs/vendor/pss-parse/5.1.3/symbol-table-bucket-reference.md
```

Or the versioned filename:

```text
docs/vendor/pss/docs/vendor/pss-parse/5.1.3/symbol-table-bucket-reference-pss-parse-5.1.3.md
```

Read `pss-parse-docs.manifest.json` in that folder for machine-readable metadata (`parserVersion`, `parserVersionMin`, paths).

### 3. Bump on parser upgrades

When you upgrade `pss-parse` in `pom.xml`:

1. Update the submodule commit or tag to the matching pss release.
2. Change your consumer path from `.../pss-parse/<old-api>/` to `.../pss-parse/<new-api>/`.
3. Commit the submodule pointer and path updates together.

```bash
cd docs/vendor/pss
git fetch --tags
git checkout pss-parse-5.1.3   # example tag
cd ../../..
git add docs/vendor/pss
```

### Sparse checkout (optional)

If the full pss tree is too large, use git sparse checkout in the submodule to keep only `docs/vendor/pss-parse/`:

```bash
git clone --filter=blob:none --sparse https://github.com/emculturate/pss.git docs/vendor/pss
cd docs/vendor/pss
git sparse-checkout set docs/vendor/pss-parse
git checkout pss-parse-5.1.3
```

## Option D — git subtree (alternative)

Subtree merges vendor docs into your repo history (no separate submodule pointer):

```bash
git subtree add --prefix docs/vendor/pss-parse \
  https://github.com/emculturate/pss.git Spring-2026-Extensions \
  -- docs/vendor/pss-parse
```

To pull updates:

```bash
git subtree pull --prefix docs/vendor/pss-parse \
  https://github.com/emculturate/pss.git Spring-2026-Extensions
```

After a subtree pull, select the subdirectory for your API version (e.g. `docs/vendor/pss-parse/5.1.3/`).

## Release tags

Tag parser + docs together when publishing:

```bash
git tag -a pss-parse-5.1.3 -m "pss-parse 5.1.3 API / docs"
git push origin pss-parse-5.1.3
```

Consumer repos should pin submodules or subtree pulls to that tag.

## Regenerate locally

```bash
./scripts/sync-pss-parse-docs-vendor.sh 5.1.3 5.1.3-1
```

Or build the Maven docs artifact (also runs the sync script):

```bash
mvn -pl pss-parse-docs package
```
