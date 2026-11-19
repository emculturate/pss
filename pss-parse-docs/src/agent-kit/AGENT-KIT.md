# PSS parser consumer agent kit

Parser: **pss:pss-parse:${project.version}**  
Documentation API: **${pss.parse.api.version}**

This kit contains consumer documentation, not the parser runtime. Its guides
are generated from the same canonical sources as the companion documentation
JAR. The [manifest](pss-parse-docs.manifest.json) identifies the release and lists
each guide's `id`, `role`, and relative `path`.

## Install in a dependent project

1. Extract the ZIP into an empty staging directory and review its contents.
2. Check the actual parser dependency or manually installed parser JAR. Its
   artifact version must match `parserVersion` in the manifest, not merely the
   API version. If it differs, obtain the matching kit before using these
   contracts. For unpublished local rebuilds sharing a version, use the kit
   delivered with that exact parser candidate; version equality alone cannot
   identify a local rebuild.
3. Copy the complete extracted contents into
   `docs/vendor/pss-parse/${pss.parse.api.version}/` in the dependent project.
   Keep this guide, the manifest, and the guides together. Review existing
   files before replacing them; preserve unrelated files and local edits.
4. Merge the following block into the project's existing agent instruction
   entry point. For example, use the configured Copilot instruction file,
   `AGENTS.md`, or a Cursor rule/skill as appropriate for that project's tool.
   Do not overwrite an existing instruction file. Ensure the chosen entry
   point is enabled and actually loaded by the agent.

```markdown
## PSS parser consumers

Before changing or explaining code that calls pss-parse, reads its parse
results, walks symbol tables or dictionaries, traces lineage, or validates
parser payloads, read:
docs/vendor/pss-parse/${pss.parse.api.version}/AGENT-KIT.md

Verify the installed parser matches the kit's manifest. Read the primary
symbol-table guide and the task-relevant guides selected from that manifest.
Do not guess bucket semantics from golden strings or assume another parser
version's contracts apply.
```

The ZIP extension and this guide's presence do not automatically register
instructions with an agent. The explicit pointer above is the discovery step.
Keep the installed docs available to the agent's file/search tools; commit
them if that is how the dependent project shares its development instructions.
Project policies and higher-priority agent instructions still apply.

## Agent reading workflow

1. Verify [manifest identity](pss-parse-docs.manifest.json) against the parser
   actually used by the project. Report a mismatch or missing guide explicitly;
   do not silently substitute a different installed version or online guide.
2. Start with the [symbol-table bucket reference](symbol-table-bucket-reference.md)
   for published scopes, buckets, `def_*` keys, `dependent_queries`, and the
   recursive consumer tracing algorithms.
3. Select additional guides using the manifest's `id` and `role`: dictionaries,
   PIVOT/UNPIVOT and modifier lineage, grammar/endpoints, window buckets,
   ordered select-list aliases, global CTE aliases, or duplicate set-op output
   names. Open each selected relative `path` from this directory.
4. Implement against those documented contracts and validate the dependent
   project's behavior with its existing tests. The kit is a reference, not a
   substitute for testing the actual parser candidate.

Read only the guides relevant to the task after the primary reference; there
is no requirement to load the entire bundle into every conversation.

## Scope and maintenance

This is the public consumer bundle. Parser implementation workplans, internal
release evidence, and repo-only Panto platform design documents are not
included. If a task requires those sources, request them from the parser
maintainers rather than inferring their contracts.

To upgrade, obtain the kit accompanying the new parser, stage and review it,
then replace the owned files and update the instruction pointer together with
the parser dependency. Do not select documentation by the highest directory
version when multiple parser versions coexist. For a same-version local
candidate, refresh both the kit and parser together.

To uninstall, remove only this kit's files and its instruction block after
checking that no remaining consumer needs them. Preserve other documentation
and project instructions.

Maintainers regenerate this ZIP and the documentation JAR with
`mvn -pl pss-parse-docs package` from the PSS repository root. Maven filters the
artifact/API versions into this guide and packages the existing consumer
guides and vendor manifest without a separate guide allowlist for the ZIP.
Do not hand-edit generated output as the maintained source. Local packaging
does not authorize publication.
