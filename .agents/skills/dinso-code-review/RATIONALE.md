# Why `dinso-code-review` is written this way

Separate description for the optional Dinso assignment task (agent skill for code review).

## Placement

The skill lives under `.agents/skills/dinso-code-review/`, next to the repo's other agent skills (`dinso-design`, `grill-me`, `system-documentation`, …). The assignment asks to put the skill in the project's existing structure; inventing a parallel `.cursor/skills/` tree would fork how agents already discover Dinso skills.

## Activation: opt-in only

`disable-model-invocation: true` is intentional. `AGENTS.md` forbids starting code analysis / security review on the agent's own initiative. A review skill that auto-attaches to every coding turn would fight that rule. The skill should run when the user asks for a review (or names the skill), not as a side effect of implementing a feature.

## Three passes, not one mixed checklist

Reviews fail when correctness, regressions, and style are jumbled into one list — a naming nit can drown a broken authz check. The skill forces three passes in order:

1. **Correctness** — is the new code right?
2. **Regression scan** — did it break or skew the rest of the project (especially other customers and shared modules)?
3. **Project alignment** — does it match how Dinso already works?

Dinso-specific traps (three customers, three authz axes, seed/demo rules, i18n overlays) sit under those passes as reference, not as a fourth competing structure.

## Severity names

Findings use **Must fix**, **Should fix**, and **Nice to have** — plain language for “blocking”, “important but clear”, and “optional polish”. Vague labels like “suggestion” or “nit” hide whether approval should wait.

## Companion file vs one big SKILL.md

`RATIONALE.md` holds the “why” so `SKILL.md` stays an executable procedure. Agents load the passes first; graders reading the assignment deliverable open the rationale without wading through review steps.
