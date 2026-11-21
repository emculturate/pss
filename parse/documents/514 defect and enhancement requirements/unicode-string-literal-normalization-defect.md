# Unicode String-Literal Normalization Defect

Date: 2026-11-20

Classification: **confirmed 5.1.4 defect**

This is a black-box finding. No parser source was consulted.

PSS 5.1.4 silently normalizes some Unicode punctuation and whitespace inside
single-quoted string literals. The input text supplied to 5.0.0-3 and 5.1.4 is
identical, both parsers report successful parses with no diagnostics, but only
5.1.4 changes the literal value published in its AST.

String-literal contents are data, not SQL syntax. Replacing these characters
can change equality, `IN`, grouping, joining, and persisted-output semantics.

## En dash reproduction

Endpoint: `SQL`

Complete input:

```sql
select 'A – B' as x
```

Input and expected AST literal:

```text
'A – B'
```

5.0.0-3 AST literal:

```text
'A – B'
```

5.1.4 AST literal:

```text
'A - B'
```

The source character `–` is EN DASH (`U+2013`). 5.1.4 replaces it with
HYPHEN-MINUS (`U+002D`).

## Em dash reproduction

Endpoint: `SQL`

Complete input:

```sql
select 'A — B' as x
```

Expected and 5.0.0-3 AST literal:

```text
'A — B'
```

5.1.4 AST literal:

```text
'A - B'
```

The source character `—` is EM DASH (`U+2014`). 5.1.4 replaces it with
HYPHEN-MINUS (`U+002D`).

## Minus-sign reproduction

Endpoint: `SQL`

Complete input:

```sql
select 'A − B' as x
```

Expected and 5.0.0-3 AST literal:

```text
'A − B'
```

5.1.4 AST literal:

```text
'A - B'
```

The source character `−` is MINUS SIGN (`U+2212`). 5.1.4 replaces it with
HYPHEN-MINUS (`U+002D`).

## Non-breaking-space reproduction

Endpoint: `SQL`

Complete input (the space between `A` and `B` is `U+00A0`):

```sql
select 'A B' as x
```

Expected and 5.0.0-3 AST literal:

```text
'A B'
```

5.1.4 AST literal:

```text
'A B'
```

5.1.4 replaces NO-BREAK SPACE (`U+00A0`) with SPACE (`U+0020`).

## Narrow non-breaking-space reproduction

Endpoint: `SQL`

Complete input (the space between `A` and `B` is `U+202F`):

```sql
select 'A B' as x
```

Expected and 5.0.0-3 AST literal:

```text
'A B'
```

5.1.4 AST literal:

```text
'A B'
```

5.1.4 replaces NARROW NO-BREAK SPACE (`U+202F`) with SPACE (`U+0020`).

## Endpoint matrix

Each literal above was submitted independently through both `SQL` and
`IN_LIST`. `IN_LIST` used a complete parenthesized input of this form:

```sql
('A – B')
```

The result was identical through both endpoints:

- 5.0.0-3 preserved the source character.
- 5.1.4 replaced dashes/minus signs with `U+002D` and non-breaking spaces
  with `U+0020`.
- Neither parser emitted a diagnostic.

This rules out an endpoint-specific AST builder.

## Passing Unicode controls

The following complete `SQL` inputs were preserved by 5.1.4:

```sql
select 'A ’ B' as x
```

```sql
select 'A “ B ” C' as x
```

```sql
select 'café' as x
```

```sql
select 'ＡＢ' as x
```

The defect therefore does not indiscriminately remove or normalize all
non-ASCII characters.

## Corpus evidence

The strict comparison first exposed this behavior in snippet row 1817:

```text
ALR/ALR.Partner Code Mapping.First Source:v01/in_list
```

Both parser requests contained:

```sql
('College Fair/High School Fair',
'College Fair',
'Recruitment Outreach – Wisconsin Education Fair',
'Conference')
```

5.0.0-3 published:

```text
'Recruitment Outreach – Wisconsin Education Fair'
```

5.1.4 published:

```text
'Recruitment Outreach - Wisconsin Education Fair'
```

The same mutation occurs in full-query corpus rows, including row 312
(`ALR/Inquiry/transformation_query_inquiry`), where values such as
`'Art – Studio Art'` become `'Art - Studio Art'`.

## Parser-agent guidance

Inspect preprocessing or character canonicalization performed before or during
lexing. Syntax-oriented normalization may be appropriate outside literals, but
it must not rewrite characters inside quoted literal tokens.

Add contract tests that assert exact code-point preservation in AST literal
values through at least `SQL`, `QUERY`, `PREDICAND`, and `IN_LIST`. Include
`U+2013`, `U+2014`, `U+2212`, `U+00A0`, and `U+202F`, plus passing controls
for Unicode characters that are not currently changed.
