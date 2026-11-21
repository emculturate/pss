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

**Default build** (`mvn test` / package): production correctness tests only; latency corpus gates are `@Ignore` (manual).

```bash
# Default CI / package build
mvn -pl parse -Dtest=ClusterBSllRegressionTest,PantoSubMapSkipListRegressionTest test

# Manual timing gates (not run on package build)
mvn -pl parse -Dtest=PantoTimeoutCorpusE3GateTest test
mvn -pl parse -Dtest=ParseLatencyDiagnosticTest test
```

---

## What's next (outside this pack)

**5.1.3 / Spring closure (2026-11-21):** this pack closes the historical
5.1.3→5.1.4 RMCP timeout/degradation handoff. Parser work continues only on
`Fall-2026-Extensions` under
[sql-grammar-5.1.4-workplan.md](../../../documents/sql-grammar-5.1.4-workplan.md)
(Entries **1**, **5–10**, **15**, **28–29**, etc.). Entries **21–27** Fall
remediation closed with default accuracy suite **5,488 / 0 failures** on
2026-11-21; see workplan § *Spring / 5.1.3 closure and Fall remediation gate*.
