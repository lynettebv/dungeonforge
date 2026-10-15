# Sprint 5 Plan — Week 7 — Command — SUPPLIED

**Epic:** E6, Player actions · **Pattern:** Command (+ Null Object, + a first taste of Memento)

> **Still supplied**, and that's a deliberate change of plan. Week 5 said this would be the
> week you wrote your own. Having seen how Sprint 4 went, you get one more sprint of worked
> examples — you now have **six** to model on. Read them as templates, not just as
> instructions, because you *will* be writing them soon.

## Sprint Goal

> Every action a player takes becomes an object, so the game can remember what happened —
> and give it back.

## Capacity — from your own numbers

| Sprint | Committed | Completed |
|---|---|---|
| Sprint 0 (Wk 2) | 7 | ____ |
| Sprint 1 (Wk 3) | 8 | ____ |
| Sprint 2 (Wk 4) | 10 | ____ |
| Sprint 3 (Wk 5) | 10 | ____ |
| Sprint 4 (Wk 6, catch-up) | — | ____ |

Five sprints of data. **If your completed column averages below 8, drop US-5.3 (the macro)
and say so in your retro.** It is the most self-contained story here and the sprint still
demonstrates Command without it.

## Committed stories

| Issue | Story | Points |
|---|---|---|
| US-5.1 | Every action is an object | 4 |
| US-5.2 | Take back a mistake | 3 |
| US-5.3 | One word, several actions | 2 |
| US-5.4 | The Chronomancer's Hourglass | 1 |

**Capacity:** ~10 points · **Committed:** 10 points

## Suggested order

1. **US-5.1** — `Command`, `AbstractCommand`, four or five concrete commands, `CommandParser`.
   Delete the if/else chain. Nothing else works until this exists.
2. **US-5.2** — `CommandHistory` and `TurnSnapshot`. **Budget most of your time here** — the
   naive version of undo does not work, and finding out why is the point.
3. **US-5.3** — `MacroCommand`. Small, and satisfying.
4. **US-5.4** — the `Hourglass`. Genuinely about twenty minutes, because the machinery exists.

> **US-5.4 being trivial is the lesson.** A feature that would have been impossible in Week 6
> costs one small class in Week 7. Note how long it actually takes you.

## Risks

| Risk | Mitigation |
|---|---|
| Naive undo — remembering one field — appears to work, then silently doesn't | Part D2 makes you *demonstrate* the failure before fixing it. Do it in that order. |
| Refused commands ("no exit south") landing on the undo stack | `isUndoable()` should reflect whether the command actually *ran*, not what type it is |
| Commands each holding four references to the world | Bundle them. `GameContext` exists for this. |
| Rewriting `Combat` from scratch | You are not. It was already split into `monsterTurns()` and `playerStrikes()` for you. |
| The parser growing its own if/else for aliases | Aliases are a `Map`, one line each |

## Definition of Done

`docs/definition-of-done.md`, unchanged.

---

# ↓ Fill in at the end of the week ↓

## Calibration

| Story | Estimated | Actual hours | High, low, about right? |
|---|---|---|---|
| US-5.1 | 4 | | |
| US-5.2 | 3 | | |
| US-5.3 | 2 | | |
| US-5.4 | 1 | | |

**Points completed:** ____ · **Running velocity:** ____

## Sprint Review — one sentence

