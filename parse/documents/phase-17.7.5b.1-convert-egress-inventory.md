# Phase 17.7.5b.1 — Convert egress derived-path inventory

**Date:** Aug 2026  
**Scope:** `SqlParseSymbolTreeHelper.java` (convert egress for PIVOT/UNPIVOT structured `derivation`)  
**Goal:** Checklist for **17.7.5b.2–.7**; no behavior change in this step.

**Canonical method:** `convertSymbolTableToTableDictionary` (~L3663).  
**Callers (publish paths):** `finalizeQueryScopeSymbolTable`, `finalizeUpdateScopeSymbolTable`, `finalizeDeleteScopeSymbolTable`, `reconcileJoinExtensionSymbolTable` (partial).

---

## Current egress order (actual, not target)

| Step | Approx. lines | What happens |
|------|---------------|--------------|
| A — Detach scope | L3678–L3736 | Remove interface, unresolved, aliases, table dict, **detach** `derivation` → `activeConvertEgress*` |
| Pre-interface | L3742–L3881 | UPDATE LHS, merge table collection, etc. |
| **Interface loop** | L3887–L4343 | Per output column ref: `resolveColumnRefAtConvertEgress` → derived expand/consume (**early path**) |
| Post-interface UNPIVOT/PIVOT | L4345–L4355 | `applyUnpivotDerivationsToQueryScope`, IN materialize, pivot value derivations |
| Reconcile (no-op) | L4357–L4363 | `reconcileRelationalModifierDerivedColumnLineageForConvertScope` — **stub** |
| **Diagnose derived ambiguous** | L4365–L4371 | `diagnoseAmbiguousUnqualifiedRelationalModifierDerivedColumnRefSites` → `forEachConvertEgressUnqualifiedColumnRefSite` + batch consume |
| Diagnose source ambiguous | L4373–L4379 | `diagnoseAmbiguousUnqualifiedRelationalModifierSourceOperandRefSites` |
| **Normal ingress** | L4383–L4395 | `resolveRemainingUnresolvedAgainstQuerySources` |
| **Clause probe** | L4397–L4411 | `probeArchivedScopeClauseColumns` → `validateArchivedClauseColumnRef` |
| Deferred harvest merge | L4413–L4417 | `mergeDeferredClauseHarvestSiteTokensIntoQueryDictionary` |
| **Clause lineage finalize** | L4419–L4425 | `finalizeRelationalModifierDerivedColumnLineageInClauseLists` → `expandRelationalModifierDerivedColumnLineageIn*` |
| D — Hygiene | L4427–L4435 | strip ephemeral locations, `consolidateConvertEgressColumnReferenceLists` |

**Gap vs 17.7.5b target:** Derived **expand/consume** runs in the **interface loop before** ambiguous diagnose; clause probe can expand/consume again; `finalizeRelationalModifierDerivedColumnLineageInClauseLists` runs **after** probe. Target is single **phase B** before normal phase C.

---

## `consumeDerivedColumnUnknownEntry` (definition ~L11207)

| # | Location | Context | 17.7.5b migration note |
|---|----------|---------|-------------------------|
| 1 | L1330 | `diagnoseAmbiguousUnqualifiedRelationalModifierDerivedColumnRefSites` — per ambiguous column name after site diagnostics | **Keep in phase B** (batch consume after all sites) |
| 2 | L3994 | Interface loop — qualified ref, `hasExpandedDerivedSourceLineage`, not retained | **Move to phase B** |
| 3 | L4011 | Interface loop — qualified ref, `isDerivedColumn`, not retained | **Move to phase B** |
| 4 | L4105 | Interface loop — qualified switch `RESOLVED_DERIVED_COLUMN`, `RESOLVED_UNPIVOT_*` | **Move to phase B** |
| 5 | L4220 | Interface loop — unqualified, expanded lineage, not retained | **Move to phase B** |
| 6 | L4237 | Interface loop — unqualified, `isDerivedColumn`, not retained | **Move to phase B** |
| 7 | L10039 | UPDATE assignment RHS probe — `isDerivedColumn` | **Phase B visitor** (assignment RHS in `forEachConvertEgress…`) |
| 8 | L11418 | `consumeLocallyResolvedUnqualifiedBeforeScopePassUp` (or similar pass-up) — `RESOLVED_DERIVED_COLUMN`, `RESOLVED_UNPIVOT_*` | Pass-up egress — **phase B/C** |
| 9 | L12471 | `applyUnqualifiedScopeResolutionResult` — `RESOLVED_DERIVED_COLUMN`, `RESOLVED_UNPIVOT_*` | Interface / ingress helper — **fold into phase B/C boundary** |
| 10 | L12553 | `applyUnqualifiedScopeResolutionResult` — `AMBIGUOUS_DERIVED_COLUMN` | Overlap with diagnose — **consolidate in phase B** |
| 11 | L12665 | `resolveRemainingUnresolvedAgainstQuerySources` — `isDerivedColumn` | **Should not consume derived** after B; remove in .4/.5 |
| 12 | L13058 | `validateArchivedClauseColumnRef` — expanded derived lineage | **Phase B** (clause sites) |
| 13 | L13095 | `validateArchivedClauseColumnRef` — `isDerivedColumn` | **Phase B** |
| 14 | L13182 | `validateArchivedClauseColumnRef` — unqualified switch `RESOLVED_DERIVED_COLUMN`, `RESOLVED_UNPIVOT_*` | **Phase B** |
| 15 | L14611 | Qualified unresolved ingress loop — `RESOLVED_DERIVED_COLUMN`, `RESOLVED_UNPIVOT_*` | Qualified derived — **phase B** |

---

## `applyConvertEgressExpandedDerivedSourceLineageToReferenceList` (~L2255)

| # | Location | Context | 17.7.5b migration note |
|---|----------|---------|-------------------------|
| 1 | L3999 | Interface loop — qualified, after optional consume | **Phase B only** (.3) |
| 2 | L4225 | Interface loop — unqualified, after optional consume | **Phase B only** (.3) |

**Related (not same symbol):** `finalizeRelationalModifierDerivedColumnLineageInClauseLists` / `expandRelationalModifierDerivedColumnLineageInMutableReferenceList` (~L2383) — runs L4419+; **merge into phase B** expand step (.3).

---

## `shouldRetainDerivedColumnUnknownUntilAmbiguousDiagnose` (~L1554)

Bridge: returns true when `isAmbiguousUnqualifiedStructuredDerivedColumn` (≥2 structured derived buckets).

| # | Location | Context |
|---|----------|---------|
| 1 | L3990 | Interface qualified — skip consume when ambiguous |
| 2 | L4007 | Interface qualified — `isDerivedColumn` |
| 3 | L4216 | Interface unqualified — expanded lineage |
| 4 | L4233 | Interface unqualified — `isDerivedColumn` |

**17.7.5b.5:** Delete helper; phase B diagnoses all ambiguous sites before any consume.

---

## `ConvertEgressColumnResolutionResult.isDerivedColumn()` branches

| # | Line | Method / surface |
|---|------|------------------|
| 1 | L3931 | Interface loop — substitution column/predicand branch (paired with qualified resolve, not pure derived flag) |
| 2 | L4006 | Interface loop — qualified |
| 3 | L4232 | Interface loop — unqualified |
| 4 | L10038 | UPDATE RHS probe |
| 5 | L12664 | `resolveRemainingUnresolvedAgainstQuerySources` |
| 6 | L13084 | `validateArchivedClauseColumnRef` |

**Also:** `hasExpandedDerivedSourceLineage()` at L3989, L4215, L13047 — same migration bucket as expand (.3).

---

## `resolveColumnRefAtConvertEgress` call sites (convert egress)

| Line | Caller |
|------|--------|
| L3927 | Interface — substitution |
| L3985 | Interface — qualified |
| L4211 | Interface — unqualified |
| L10037 | UPDATE RHS |
| L12663 | `resolveRemainingUnresolvedAgainstQuerySources` |
| L13046 | `validateArchivedClauseColumnRef` |

**17.7.5b.2:** Central read-only **classify** wrapper around this resolver (no consume side effects).

---

## Ambiguous derived / source diagnosis

| Symbol | Line | Role |
|--------|------|------|
| `diagnoseAmbiguousUnqualifiedRelationalModifierDerivedColumnRefSites` | L1259 | FATAL per site via `forEachConvertEgressUnqualifiedColumnRefSite` |
| `diagnoseAmbiguousUnqualifiedRelationalModifierSourceOperandRefSites` | L1343 | SEVERE `AMBIGUOUS_COLUMN_REFERENCE` for multi-bucket **source** operands |
| `forEachConvertEgressUnqualifiedColumnRefSite` | L1459 | Shared visitor over interface + archived containers + UPDATE RHS |
| `isAmbiguousUnqualifiedStructuredDerivedColumn` | L1545+ | ≥2 buckets — used by retain guard + diagnose skip in probe |

**Clause probe skips** ambiguous structured derived (L13013–L13017) and ambiguous source operand (L13020–L13024) — assumes diagnose already ran or interface loop retained unknowns.

---

## `reconcileRelationalModifierDerivedColumnLineageForConvertScope`

- **L1192–L1201:** No-op; comment points to `finalizeRelationalModifierDerivedColumnLineageInClauseLists`.
- Called at L4357 **before** diagnose — ordering artifact; safe to remove or repurpose in .3.

---

## 17.7.5b.2–.4 touch map (from this inventory)

| Step | Primary symbols to refactor |
|------|----------------------------|
| **.2** | New `classifyColumnRefAtConvertEgress` (read-only); all call sites above |
| **.3** | Remove L3999/L4225 interface expand; relocate L4419 finalize expand into phase B; align `validateArchivedClauseColumnRef` expand |
| **.4** | Delete derived branches L3989–L4242 (consume/expand/`isDerivedColumn` early exit) from interface loop; keep substitution, pivot operand, unpivot IN, physical/query materialize |
| **.5** | Delete `shouldRetainDerivedColumnUnknownUntilAmbiguousDiagnose`; single consume batch in diagnose visitor |

---

## Verification (17.7.5b.1)

- [x] Grep inventory captured in this document  
- [x] No production code changes  
- [x] Full `parse/` module test suite green — **1731/1731** (Aug 2026 baseline before **17.7.5b.2**)
