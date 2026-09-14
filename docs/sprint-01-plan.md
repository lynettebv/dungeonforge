# Sprint 1 Plan — Week 3 — Singleton — SUPPLIED

**Epic:** E3, Configuration & randomness · **Pattern:** Singleton

> Still supplied. You write your first sprint plan in Week 6.

## Sprint Goal

> Every tunable value and every random roll comes from a single, seeded source.

## Planning decision — US-1.4 was CUT

In Week 2 you were asked to find a defective story. It was **US-1.4, "Improve the
configuration code."** Here is the planning meeting you would have had:

| | |
|---|---|
| **The problem** | It dictated the implementation (*"static HashMap with double-checked locking"*), named no beneficiary (*"the code is better and more professional"*), and had acceptance criteria nobody could check (*"it looks clean"*). |
| **The decision** | **Cut.** Not deferred, not rewritten — cut. There is no user need underneath it. If a specific locking strategy turns out to be necessary, that is a sub-task of US-1.1, not a story of its own. |
| **The consequence** | Sprint 1 drops from **13 points to 8**, against a capacity of ~10. |

**Notice what just happened.** The sprint was over-committed by three points, and the fix
was not "work harder" or "cut a good story." It was removing work that had no articulable
value. That is the most common real outcome of a sprint planning meeting, and it is worth
remembering the next time your own sprint doesn't fit.

## Committed stories

| Issue | Story | Points |
|---|---|---|
| US-1.1 | Settings live in one place | 3 |
| US-1.2 | The same seed produces the same dungeon | 3 |
| US-1.3 | The one-instance rule is enforced, not hoped for | 2 |
| ~~US-1.4~~ | ~~Improve the configuration code~~ | ~~5~~ **CUT** |

**Capacity:** ~10 points · **Committed:** 8 points

## Suggested order

1. **US-1.1 first.** US-1.2 reads the seed from `GameConfig`, so config has to exist first.
2. **US-1.2 second.**
3. **US-1.3 last** — though writing the tests as you go is better practice than saving them
   for the end. The story is listed last because it can only be *finished* last.

## Risks

| Risk | Mitigation |
|---|---|
| Reading a resource from the classpath is unfamiliar | The starter includes `Json.java` and a worked `getResourceAsStream` example in the lab doc |
| Missing a hardcoded literal somewhere, so AC2 fails quietly | `grep -rn "60\|new Random(" src/main/java` before opening the PR |
| Over-applying the pattern — making a third or fourth singleton | Part C is the audit that stops this. Only two classes qualify. |

## Definition of Done

`docs/definition-of-done.md` applies unchanged. Note the box you repaired in Week 2.

---

# ↓ You fill in these two sections at the end of the week ↓

## Calibration — actual vs estimate

| Story | Estimated | Actual hours | High, low, or about right? |
|---|---|---|---|
| US-1.1 | 3 | | |
| US-1.2 | 3 | | |
| US-1.3 | 2 | | |

**Points completed:** ____ · **Sprint 0 velocity for comparison:** ____

## Sprint Review — one sentence

> What can the project do now that it could not do last week?


