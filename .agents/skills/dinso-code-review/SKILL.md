---
name: dinso-code-review
description: >-
  Reviews Dinso changes in three passes: correctness of the new code, regression
  risk across the rest of the project, then alignment with existing patterns.
  Use when the user asks for a code review, PR review, or review of a
  diff/commit in Dinso — never on the agent's own initiative.
disable-model-invocation: true
---

# Dinso code review

Review **only when the user asks**. Do not start a review as a side effect of
implementation (see `AGENTS.md`).

For design of new/changed UI, also read `.agents/skills/dinso-design/SKILL.md`
when the diff touches `client/` views or components.

## Scope first

1. Identify what to review: uncommitted diff, a commit range, a PR, or named files.
2. Prefer `git diff` / `git show` / `gh pr diff` over re-reading whole modules.
3. Note which **customer variants** are affected (`svenskebanken`, `pensionsbolaget`, `finbanken`). Shared modules affect all three.

## Three review passes (always in this order)

Run these as separate passes. Do not mix findings from different passes in one
unordered list — group output by pass.

### Pass 1 — Correctness

Does the **new or changed code** do what it claims, safely and completely?

- Logic, edge cases, error paths, and null/empty handling for the touched flow
- Authz for new/changed endpoints and UI controls (see layers below)
- Persistence/flush/constraint behaviour on write paths
- Tests that actually prove the claimed behaviour (not only happy path)
- No secrets, no real BankID/payment side effects, no unsolicited docs

### Pass 2 — Regression scan

Did the change **hurt anything else** in the project?

- Call sites, shared modules, and other portals that consume the same types/APIs
- All three customers: especially FinBanken when company-portal assumptions leak
- Seed/demo profiles still usable after a fresh start
- Login/portal entry rules still coherent (e.g. zero company grants → no COMPANY portal)
- Static demo (`VITE_USE_API` off) and API mode both still coherent for touched flows
- Existing tests still meaningful; shared changes need per-customer coverage where behaviour differs

### Pass 3 — Project alignment

Does the change **look and behave like Dinso**, not a one-off?

- Shared vs `client/customers/*` / `server/customers/*` placement
- Reuse of existing Vue primitives (`Panel`, `DataTable`, `EmptyState`, `PageHeader`, …)
- Naming, package layout, and patterns consistent with neighbouring code
- `t()` for user-facing copy; English overlays in every shipping customer `en.json`
- Authz layers kept separate (do not invent a fourth model)
- No drive-by refactors or style-only churn outside the task

## Dinso-specific reference (use during the passes)

### Authorization layers (do not conflate)

| Concern | Where it lives | Question to ask |
| --- | --- | --- |
| Customer capability | `CustomerRules` / `companyMutations` | Does this *variant* support the mutation at all? |
| Company scoping | `DemoRole` + `CompanyAuthorizationEntity` | Which *companies* may this profile act on? |
| Per-person action | `CompanyAction` + `ProfileActionGrant*` | May *this person* perform this action? |
| Portal entry | `PortalType` + session login rules | May this profile enter COMPANY / PRIVATE / SYSTEM? |

Red flags: new `DemoRole` values for action combos; `CompanyMutation` as a permission store; company endpoints without a matching `CompanyAction` check; `/api/system/**` without `SYSTEM_ADMIN`.

### Verification commands (when the diff warrants them)

- Frontend: `npm run typecheck` and `npm run build:all`
- Backend: `./gradlew :customers:svenskebanken:test :customers:pensionsbolaget:test :customers:finbanken:test`

## Finding severity

Use exactly these three labels — map every finding to one:

| Severity | Meaning | Blocks approval? |
| --- | --- | --- |
| **Must fix** | Wrong, broken, unsafe, or violates a hard Dinso rule (authz, customer boundary, broken demo). | Yes |
| **Should fix** | Clear defect or inconsistency that will bite soon; leave a note if deferred. | Usually yes, unless the risk is accepted |
| **Nice to have** | Polish, clarity, or small consistency gains. Optional. | No |

Do not invent other severity names.

## Output format

```markdown
## Summary
<1–2 sentences: what changed and review scope>

## Pass 1 — Correctness
- **Must fix** — … (`path`)
- **Should fix** — … (`path`)
- **Nice to have** — … (`path`)
(or: no findings)

## Pass 2 — Regression scan
- **Must fix** — …
- **Should fix** — …
- **Nice to have** — …
(or: no findings)

## Pass 3 — Project alignment
- **Must fix** — …
- **Should fix** — …
- **Nice to have** — …
(or: no findings)

## Verdict
<approve | approve with nits | request changes>
```

- **request changes** if any **Must fix** remains (or an unaccepted **Should fix** that breaks behaviour).
- **approve with nits** if only **Nice to have** (and maybe deferred, acknowledged **Should fix**).
- **approve** if no findings, or only accepted optional notes.

Keep the write-up short (match `AGENTS.md` closing style: no essay).

## Additional context

- Why this skill is shaped this way: see [RATIONALE.md](RATIONALE.md).
