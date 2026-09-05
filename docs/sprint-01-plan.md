# Sprint 1 Plan — Week 3 — SUPPLIED

**Epic:** E3, Configuration & randomness · **Pattern:** Singleton

> **You do not write this.** Read it. One of the three risks below is not actually a risk,
> and the arithmetic has a problem. Both are part of Lab Part C.

## Sprint Goal

> Every tunable value and every random roll comes from a single, seeded source.

A good sprint goal fits in one sentence and tells you what to cut when you run short of time.
If a story doesn't serve this sentence, it doesn't belong in this sprint.

## Committed stories

| Issue | Story | Points |
|---|---|---|
| US-1.1 | Settings live in one place | 3 |
| US-1.2 | The same seed produces the same dungeon | 3 |
| US-1.3 | The one-instance rule is enforced, not hoped for | 2 |
| US-1.4 | Improve the configuration code | 5 |

**Capacity:** ~10 points · **Committed:** 13 points

> Capacity is a starting guess. You have no velocity data yet — Sprint 0 gives you your first
> data point. From Week 6 you'll set your own capacity from your own history.

## Risks

| Risk | Mitigation |
|---|---|
| Reading a resource file from the classpath is unfamiliar territory | Prototype `getResourceAsStream` in a scratch file before touching `GameConfig` |
| US-1.2 depends on US-1.1 being finished first, since the seed is read from config | Sequence them: finish US-1.1 before starting US-1.2 |
| I might get busy this week | Try to stay on top of it |

## Definition of Done

The project-wide DoD in `docs/definition-of-done.md` applies. It is not renegotiated per
sprint, and it is not renegotiated mid-sprint.

---

## Notes for whoever runs this sprint

- The Singleton is the most over-used pattern in the textbook. This project permits exactly
  **two**: `GameConfig` and `RandomSource`. If you can pass a thing in as a parameter, it is
  not a Singleton — it is a dependency.
- `RandomSource` is the one with the strongest justification: one RNG plus one seed means a
  reproducible dungeon, which means reproducible bugs and deterministic tests.
- Week 3's lab document will tell you which of these stories to pull first.
