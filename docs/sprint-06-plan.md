# Sprint 6 Plan — Week 8 — State — SUPPLIED

**Epic:** E7, Game modes · **Pattern:** State

> **Still supplied.** You now have seven worked sprints. Keep reading them as templates —
> particularly the acceptance criteria phrased as an *absence* ("zero matches for..."), which
> is the hardest kind to write and the most useful kind to have.

## Sprint Goal

> The game knows what mode it is in, and monsters know where they are in their own lifecycle —
> so nobody has to remember to check.

## Capacity

| Sprint | Committed | Completed |
|---|---|---|
| Sprint 3 (Wk 5) | 10 | ____ |
| Sprint 4 (Wk 6, catch-up) | — | ____ |
| Sprint 5 (Wk 7) | 10 | ____ |

**If Sprint 5 came in under 8, drop US-6.2 and say so in your retro.** The pack screen is the
most self-contained story here and the sprint still demonstrates State without it.

## Committed stories

| Issue | Story | Points |
|---|---|---|
| US-6.1 | The game has modes, and each mode allows its own actions | 4 |
| US-6.2 | The pack is a place you go, and death is somewhere you stay | 2 |
| US-6.3 | Monsters have a lifecycle | 3 |
| US-6.4 | A monster's *when* and *how* are independent | 1 |

**Capacity:** ~10 points · **Committed:** 10 points

## Suggested order

1. **US-6.3 first.** The monster lifecycle is the smaller, more contained state machine, and
   it maps almost exactly onto the Gumball Machine in your reading. Build it while the chapter
   is fresh.
2. **US-6.1 second.** `GameState`, the two main modes, and the parser gate. **Then delete the
   three scattered guards** — the deletion is the deliverable.
3. **US-6.2** — `InventoryState` and `GameOverState`.
4. **US-6.4** — one demonstration and a couple of tests. Twenty minutes, and it is the story
   the exam will ask about.

## Risks

| Risk | Mitigation |
|---|---|
| Building State as an `enum` with a `switch` | That is the string field with extra steps. Part D2 measures the difference. |
| States mutating each other directly | Transitions go through the context. A state *returns* its successor; it does not assign one. |
| `syncState()` yanking the player out of a screen they chose | Sub-states entered on purpose are exempt. There is a test for this. |
| Confusing this with Week 5's Strategy | Good. Part D1 makes you grade your own Week 5 prediction. |
| Forgetting that `onEnter` runs on transition | It is the hook that lets a state adjust the monster. Do not call it yourself. |

## Definition of Done

`docs/definition-of-done.md`, unchanged.

---

# ↓ Fill in at the end of the week ↓

## Calibration

| Story | Estimated | Actual hours | High, low, about right? |
|---|---|---|---|
| US-6.1 | 4 | | |
| US-6.2 | 2 | | |
| US-6.3 | 3 | | |
| US-6.4 | 1 | | |

**Points completed:** ____ · **Running velocity:** ____

## Sprint Review — one sentence

