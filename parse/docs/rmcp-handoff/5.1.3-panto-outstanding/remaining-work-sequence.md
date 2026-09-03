# Panto timeout corpus — work complete (2026-09-03)

**§2.8**, **§2.11**, and related W-track items are **closed**. Production parsing is **`SqlParserAccess` (LL only)**. `ParseLatencyDiagnosticService` is opt-in timing only.

---

## Closed

| Item | Outcome |
|------|---------|
| **§2.8 E1–E3** | 74/74 rows under 90s (`PantoTimeoutCorpusE3GateTest` — timing only) |
| **§2.11 W-track** | W1 superseded (always walk); W2–W4 done; W5 retracted |
| **§2.11.2 Cluster B** | 10 rows clean on production LL path (`ClusterBSllRegressionTest`) |
| **§2.11.4** | One-off triage CLIs removed |
| **§2.10 SLL→LL** | **Abandoned** — LL accuracy/fidelity preferred over SLL performance |
| **Row 4197** | Utility workbook exclusion (`panto-corpus-exclusion-list.json`) |
| **W5 syntax_corrupt** | Retracted — diagnostic false positives (rows 28–32, 41, 314–315) |

---

## Production policy (permanent)

- **`SqlParserAccess`**: LL prediction only — no SLL, no two-stage fallback.
- **Correctness gates**: `SqlParserAccess` on frozen `sql/csv-row-*.sql` fixtures.
- **E3 gate**: 90s wall-clock only — not validity.
- **SLL**: `diagnoseWithSllProbe()` exists for future perf investigations only; never for validation.

---

## Regression gates

```bash
mvn -pl parse -Dtest=PantoTimeoutCorpusE3GateTest,ClusterBSllRegressionTest,PantoSubMapSkipListRegressionTest test
```

---

## What's next (outside this pack)

See `parser-defects-enhancements-workplan.md` **Phase 3** (Snowflake `PARSE_URL` / `:` field access) and other open phases.
