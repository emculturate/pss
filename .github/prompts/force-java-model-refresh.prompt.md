---
mode: ask
description: Force refresh Java/Maven model for parse workspace and verify diagnostics
---

Force-refresh Java project state for this workspace now.

Run these actions in order:
1. Run the VS Code task "Recover Java Model Now".
2. Re-sample diagnostics across the workspace.
3. Run parse module smoke tests:
   - parse/src/test/java/access/ParserAccessClassTest.java
   - parse/src/test/java/sql/walker/SqlParseEventWalkerWithAccessObjectTest.java
4. Report:
   - whether unresolved-type/import errors remain
   - whether only style/warning diagnostics remain
   - test pass/fail summary

If the task fails, run the script directly and report command output summary:
bash tools/recover-java-model.sh